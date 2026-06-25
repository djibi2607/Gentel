package com.abdoul.hotel.Services;

import com.abdoul.hotel.Config.KycType;
import com.abdoul.hotel.DTO.UserDTO;
import com.abdoul.hotel.Entities.KycModel;
import com.abdoul.hotel.Entities.UserModel;
import com.abdoul.hotel.Entities.WalletModel;
import com.abdoul.hotel.Exceptions.BadRequestException;
import com.abdoul.hotel.Exceptions.ConflictException;
import com.abdoul.hotel.Repositories.KycRepository;
import com.abdoul.hotel.Repositories.UserRepository;
import com.abdoul.hotel.Repositories.WalletRepository;
import com.abdoul.hotel.Utils.RedisUtil;
import com.abdoul.hotel.Utils.ResendUtil;
import com.abdoul.hotel.Utils.TwilioUtil;
import com.abdoul.hotel.Utils.TwoFactorUtil;
import org.springframework.security.crypto.password4j.Argon2Password4jPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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

    public UserService (Argon2Password4jPasswordEncoder encoder, UserRepository userRepository, WalletRepository walletRepository, KycRepository kycRepository, TwilioUtil twilioUtil, TwoFactorUtil twoFactorUtil, RedisUtil redisUtil, ResendUtil resend){
        this.encoder = encoder;
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.kycRepository = kycRepository;
        this.twilioUtil = twilioUtil;
        this.twoFactorUtil = twoFactorUtil;
        this.redisUtil = redisUtil;
        this.resend = resend;
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

        Map<String, String> response = new LinkedHashMap<>();
        response.put("notice", "Welcome " + data.getName() + ", your account has been successfully created");
        response.put("message", "A notification will be sent to you soon to verify your phone/email");

        return response;
    }

}
