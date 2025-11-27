package com.example.multipagos.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.multipagos.dto.RegisterDto;
import com.example.multipagos.dto.LoginDto;
import com.example.multipagos.dto.AuthResponse;
import com.example.multipagos.service.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public void register(@RequestBody RegisterDto dto) {
        authService.register(dto);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginDto dto) {
        return authService.login(dto);
    }
}
