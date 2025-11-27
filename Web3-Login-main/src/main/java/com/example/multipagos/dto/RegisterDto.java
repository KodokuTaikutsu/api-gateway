package com.example.multipagos.dto;

public class RegisterDto {
    public String username;
    public String email;
    public String password;
    // optional: companyId to associate user at registration
    public Long companyId;
}
