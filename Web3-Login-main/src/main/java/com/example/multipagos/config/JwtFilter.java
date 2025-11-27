package com.example.multipagos.config;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.multipagos.util.JwtUtil;
import com.example.multipagos.repository.UserRepository;
import com.example.multipagos.model.User;
import com.example.multipagos.model.UserCompany;

import io.jsonwebtoken.Claims;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepo;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {
        String header = req.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                if (jwtUtil.validate(token)) {
                    Claims claims = jwtUtil.getClaims(token);
                    String username = claims.getSubject();
                    User user = userRepo.findByUsername(username).orElse(null);
                    if (user != null) {
                        // roles from token
                        List<String> roles = claims.get("roles", List.class);
                        if (roles == null) roles = user.getCompanies().stream().map(uc -> uc.getRole().name()).distinct().collect(Collectors.toList());
                        List<SimpleGrantedAuthority> authorities = roles.stream()
                                .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                                .collect(Collectors.toList());

                        // companyIds
                        List<Integer> companyIdsInt = claims.get("companyIds", List.class);
                        List<Long> companyIds = new ArrayList<>();
                        if (companyIdsInt != null) {
                            for (Integer i : companyIdsInt) companyIds.add(i.longValue());
                        } else {
                            // fallback: from DB
                            for (UserCompany uc : user.getCompanies()) {
                                companyIds.add(uc.getCompany().getId());
                            }
                        }

                        Map<String,Object> details = new HashMap<>();
                        details.put("companyIds", companyIds);

                        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, null, authorities);
                        auth.setDetails(details);
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }
                }
            } catch (Exception ex) {
                // invalid token, ignore and proceed anonymous
            }
        }
        chain.doFilter(req, res);
    }
}
