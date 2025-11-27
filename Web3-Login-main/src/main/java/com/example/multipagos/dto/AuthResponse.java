package com.example.multipagos.dto;

import java.util.List;

public class AuthResponse {
    public String token;
    public String username;
    public List<Long> companyIds;

    public AuthResponse(String token, String username, List<Long> companyIds) {
        this.token = token;
        this.username = username;
        this.companyIds = companyIds;
    }
}
