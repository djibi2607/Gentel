package com.abdoul.hotel.Services;

import com.abdoul.hotel.Config.KycStatus;
import com.abdoul.hotel.Config.KycType;
import com.abdoul.hotel.DTO.UserDTO;
import com.abdoul.hotel.Entities.KycModel;
import com.abdoul.hotel.Entities.UserModel;
import com.abdoul.hotel.Entities.WalletModel;
import com.abdoul.hotel.Exceptions.*;
import com.abdoul.hotel.Repositories.KycRepository;
import com.abdoul.hotel.Repositories.UserRepository;
import com.abdoul.hotel.Repositories.WalletRepository;
import com.abdoul.hotel.Utils.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password4j.Argon2Password4jPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {
    private final Argon2Password4jPasswordEncoder encoder;
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final KycRepository kycRepository;
    private final TwilioUtil twilioUtil;
    private final TwoFactorUtil twoFactorUtil;
    private final RedisUtil redisUtil;
    private final ResendUtil resend;
    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, String> redisTemplate;

    public UserService (Argon2Password4jPasswordEncoder encoder, UserRepository userRepository, WalletRepository walletRepository, KycRepository kycRepository, TwilioUtil twilioUtil, TwoFactorUtil twoFactorUtil, RedisUtil redisUtil, ResendUtil resend
    , JwtUtil jwtUtil, RedisTemplate<String, String> redisTemplate){
        this.encoder = encoder;
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.kycRepository = kycRepository;
        this.twilioUtil = twilioUtil;
        this.twoFactorUtil = twoFactorUtil;
        this.redisUtil = redisUtil;
        this.resend = resend;
        this.jwtUtil = jwtUtil;
        this.redisTemplate = redisTemplate;
    }

    @Transactional
    public Map<String, String> createAccount (UserDTO.SignUp data){
        boolean noPhone = data.getPhone() == null || data.getPhone().isBlank();
        boolean noEmail = data.getEmail() == null || data.getEmail().isBlank();

        if (noPhone && noEmail) {
            throw new BadRequestException("You must enter a phone number or email");
        }

        if (data.getBirthDate().isAfter(LocalDate.now().minusYears(18))){
            throw new BadRequestException("You must be 18 years old or older");
        }

        UserModel existingUser = userRepository.findByEmailOrPhone(data.getEmail(), data.getPhone());

        if (existingUser != null && !existingUser.isDeleted()){
            throw new ConflictException("Account already exists");
        }

        UserModel newUser = new UserModel();
        newUser.setName(data.getName());
        newUser.setEmail(data.getEmail());
        newUser.setPhone(data.getPhone());
        newUser.setBirthDate(data.getBirthDate());
        newUser.setPassword(encoder.encode(data.getPassword()));

        userRepository.saveAndFlush(newUser);

        WalletModel newWallet = new WalletModel();
        newWallet.setUser(newUser);

        walletRepository.save(newWallet);

        KycModel newKyc = new KycModel();
        newKyc.setUser(newUser);
        newKyc.setKycType(KycType.SELFIE);

        KycModel neWKyc = new KycModel();
        neWKyc.setUser(newUser);
        neWKyc.setKycType(KycType.ID);

        List<KycModel> kycs = List.of(newKyc, neWKyc);

        kycRepository.saveAll(kycs);

        String smsCode = twoFactorUtil.createCode();
        String emailCode = twoFactorUtil.createCode();

        if (!noPhone) {
            redisUtil.saveCode(smsCode, String.valueOf(newUser.getId()), "Sign-up-phone-verification");
            twilioUtil.sendWelcomeSmsWithPhoneVerification(data.getPhone(), data.getName(), smsCode);
        }

        if (!noEmail) {
            redisUtil.saveCode(emailCode, String.valueOf(newUser.getId()), "Sign-up-email-verification");
            resend.sendWelcomeEmailWithEmailVerification(newUser.getName(), emailCode);
        }

        String token = jwtUtil.generateAccessToken(String.valueOf(newUser.getId()));

        Map<String, String> response = new LinkedHashMap<>();
        response.put("notice", "Welcome " + data.getName() + ", your account has been successfully created");
        response.put("message", "A notification will be sent to you soon to verify your phone/email");
        response.put("temporary-token", token);
        response.put("token-type", "Bearer ");

        return response;
    }

    @Transactional
    public Map<String, String> verifyEmailOrPhone (UserDTO.Verification data, UserModel currentUser){
        if (data.getEmailCode() == null && data.getSmsCode() == null){
            throw new BadRequestException("You must enter at least one code");
        }

        if (currentUser.isEmailVerified() && currentUser.isPhoneVerified()){
            throw new BadRequestException("Your credentials have already been verified");
        }

        Map<String, String> response = new LinkedHashMap<>();

        if (currentUser.getEmail() != null && currentUser.getPhone() != null){

            String emailCode = redisUtil.getCode(String.valueOf(currentUser.getId()), "Sign-up-email-verification");

            if (emailCode == null){
                throw new BadRequestException("Code expired");
            }

            if (!emailCode.equals(data.getEmailCode())){
                throw new BadRequestException("Incorrect code");
            }
            else {
                currentUser.setEmailVerified(true);
                response.put("notice", "Your email has been verified");
            }

            String smsCode = redisUtil.getCode(String.valueOf(currentUser.getId()), "Sign-up-phone-verification");

            if (smsCode == null){
                throw new BadRequestException("Code expired");
            }

            if (!smsCode.equals(data.getSmsCode())){
                throw new BadRequestException("Incorrect code");
            }
            else {
                currentUser.setPhoneVerified(true);
                response.put("message", "Your phone has been verified");
            }

        }

        if (currentUser.getPhone() == null){
            String emailCode = redisUtil.getCode(String.valueOf(currentUser.getId()), "Sign-up-email-verification");

            if (emailCode == null){
                throw new BadRequestException("Code expired");
            }

            if (!emailCode.equals(data.getEmailCode())){
                throw new BadRequestException("Incorrect code");
            }
            else {
                currentUser.setEmailVerified(true);
                response.put("notice", "Your email has been verified");
            }
        }
        if (currentUser.getEmail() == null) {
            String smsCode = redisUtil.getCode(String.valueOf(currentUser.getId()), "Sign-up-phone-verification");

            if (smsCode == null){
                throw new BadRequestException("Code expired");
            }

            if (!smsCode.equals(data.getSmsCode())){
                throw new BadRequestException("Incorrect code");
            }
            else {
                currentUser.setPhoneVerified(true);
                response.put("notice", "Your phone has been verified");
            }
        }

        userRepository.save(currentUser);

        return response;
    }


    @Transactional
    public Map<String, String> logIn (UserDTO.Login data){
        if (data.getEmail() == null && data.getPhone() == null){
            throw new BadRequestException("You must provide an email or phone number");
        }

        Map<String, String> response = new LinkedHashMap<>();

        UserModel user = userRepository.findByEmailOrPhone(data.getEmail(), data.getPhone());

        if (user == null) {
            throw new NotFoundException("Account not found");
        }

        if (user.isDeleted()){
            throw new NotFoundException("Account not found");
        }

        if (!user.isPhoneVerified() || !user.isEmailVerified()){
            throw new BadRequestException("You must verify your information first");
        }

        if (!encoder.matches(data.getPassword(), user.getPassword())){
            throw new BadRequestException("Incorrect password");
        }

        KycModel idKyc = kycRepository.findByUserAndKycType(user, KycType.ID);

        KycModel selfieKyc = kycRepository.findByUserAndKycType(user, KycType.SELFIE);

        boolean idInValid = user.getCreatedAt().plusDays(14).isBefore(ZonedDateTime.now(ZoneId.of("UTC"))) && !idKyc.getKycStatus().equals(KycStatus.Approved);

        boolean selfieInValid = user.getCreatedAt().plusDays(14).isBefore(ZonedDateTime.now(ZoneId.of("UTC"))) && !selfieKyc.getKycStatus().equals(KycStatus.Approved);

        if (idInValid || selfieInValid){
            throw new ForbiddenException("Your account has been deactivated for failure to submit documentation");
        }

        if (user.isFaEnabled()){
            String temporaryToken = jwtUtil.generateAccessToken(String.valueOf(user.getId()));

            redisUtil.saveToken("Temporary-Token", temporaryToken, String.valueOf(user.getId()), Duration.ofMinutes(15));

            String code = twoFactorUtil.createCode();

            redisUtil.saveCode(code, String.valueOf(user.getId()), "Login-Two-Factor-Code");

            resend.sendWelcomeEmailWithEmailVerification(user.getName(), code);

            twilioUtil.sendWelcomeSmsWithPhoneVerification(user.getPhone(), user.getName(), code);

            response.put("notice", "A verification code has been sent to your email and phone number");

            response.put("temporary-token", temporaryToken);

            return response;
        }

        String accessToken = jwtUtil.generateAccessToken(String.valueOf(user.getId()));

        String refresh = jwtUtil.createRefresh();

        redisUtil.saveToken("Refresh-Token", refresh, String.valueOf(user.getId()), Duration.ofHours(2));

        response.put("notice", "Login successful");
        response.put("access token", accessToken);
        response.put("refresh token", refresh);
        response.put("token type", "Bearer ");

        return response;
    }


    public Map<String, String> loginWith2fa (UserDTO.LoginWith2fa data){
        String userId = redisUtil.getIdFromToken("Temporary-Token", data.getToken());

        if (userId == null){
            throw new UnauthorizedException("Invalid token");
        }

        String storedCode = redisUtil.getCode(userId, "Login-Two-Factor-Code");

        if (storedCode == null) {
            throw new BadRequestException("Code expired");
        }

        if (!storedCode.equals(data.getCode())){
            throw new BadRequestException("Invalid code");
        }

        Map<String, String> response = new LinkedHashMap<>();

        String accessToken = jwtUtil.generateAccessToken(userId);

        String refresh = jwtUtil.createRefresh();

        redisUtil.saveToken("Refresh-Token", refresh, userId, Duration.ofHours(2));

        response.put("notice", "Login successful");
        response.put("access token", accessToken);
        response.put("refresh token", refresh);
        response.put("token type", "Bearer ");

        redisTemplate.delete ("Temporary-Token" + data.getToken());
        redisTemplate.delete("Login-Two-Factor-Code" + userId);

        return response;
    }
}
