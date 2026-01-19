package com.bk.ems3.controller;

import com.bk.ems3.dto.LoginDTO;
import com.bk.ems3.dto.LoginResponse;
import com.bk.ems3.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.bk.ems3.dto.RegisterDTO;
import com.bk.ems3.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin("http://localhost:3000")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterDTO request) {
        authService.register(request);
        return ResponseEntity.ok("Registration successful. Waiting for approval.");
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }

}
