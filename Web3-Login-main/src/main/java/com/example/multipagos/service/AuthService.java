package com.example.multipagos.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.multipagos.dto.RegisterDto;
import com.example.multipagos.dto.LoginDto;
import com.example.multipagos.dto.AuthResponse;
import com.example.multipagos.model.User;
import com.example.multipagos.model.UserCompany;
import com.example.multipagos.model.Company;
import com.example.multipagos.model.Role;
import com.example.multipagos.repository.UserRepository;
import com.example.multipagos.repository.CompanyRepository;
import com.example.multipagos.repository.UserCompanyRepository;
import com.example.multipagos.util.JwtUtil;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private CompanyRepository companyRepo;

    @Autowired
    private UserCompanyRepository ucRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Transactional
    public void register(RegisterDto dto) {
        if (userRepo.existsByUsername(dto.username)) {
            throw new RuntimeException("username_taken");
        }
        if (dto.email != null && userRepo.findByEmail(dto.email).isPresent()) {
            throw new RuntimeException("email_taken");
        }
        User u = new User();
        u.setUsername(dto.username);
        u.setEmail(dto.email);
        u.setPasswordHash(passwordEncoder.encode(dto.password));
        userRepo.save(u);

        if (dto.companyId != null) {
            Optional<Company> c = companyRepo.findById(dto.companyId);
            if (c.isPresent()) {
                UserCompany uc = new UserCompany();
                uc.setUser(u);
                uc.setCompany(c.get());
                uc.setRole(Role.USER);
                ucRepo.save(uc);
            }
        }
    }

    public AuthResponse login(LoginDto dto) {
        User u = userRepo.findByUsername(dto.username).orElseThrow(() -> new RuntimeException("user_not_found"));
        if (!passwordEncoder.matches(dto.password, u.getPasswordHash())) {
            throw new RuntimeException("invalid_credentials");
        }
        List<UserCompany> ucs = ucRepo.findByUserId(u.getId());
        List<Long> companyIds = ucs.stream().map(uc -> uc.getCompany().getId()).collect(Collectors.toList());
        List<String> roles = ucs.stream().map(uc -> uc.getRole().name()).distinct().collect(Collectors.toList());

        // If user passed a companyId to select, place it first (optional)
        if (dto.companyId != null && companyIds.contains(dto.companyId)) {
            List<Long> reordered = new ArrayList<>();
            reordered.add(dto.companyId);
            for (Long c : companyIds) if (!c.equals(dto.companyId)) reordered.add(c);
            companyIds = reordered;
        }

        String token = jwtUtil.generateToken(u.getUsername(), companyIds, roles);
        return new AuthResponse(token, u.getUsername(), companyIds);
    }
}
