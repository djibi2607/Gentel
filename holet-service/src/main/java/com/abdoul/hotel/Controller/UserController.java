package com.abdoul.hotel.Controller;

import com.abdoul.hotel.DTO.UserDTO;
import com.abdoul.hotel.Entities.UserModel;
import com.abdoul.hotel.Services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController (UserService userService){
        this.userService = userService;
    }

    @PostMapping ("/sign-up")
    public ResponseEntity<Map<String, String>> createAccount (@Valid @RequestBody UserDTO.SignUp data) {
        return ResponseEntity.ok().body(userService.createAccount(data));
    }

    @PostMapping("/verify")
    public ResponseEntity<Map<String, String>> verifyEmailOrPhone (@RequestBody UserDTO.Verification data, HttpServletRequest request){
        UserModel currentUser = (UserModel) request.getAttribute("currentUser");

        return ResponseEntity.ok().body(userService.verifyEmailOrPhone(data, currentUser));
    }

    @PostMapping("login")
    public ResponseEntity<Map<String, String>> login (@Valid @RequestBody UserDTO.Login data){
        return ResponseEntity.ok().body(userService.logIn(data));
    }
}
