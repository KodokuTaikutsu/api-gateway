package com.example.multipagos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.multipagos.model.UserCompany;
import java.util.List;

public interface UserCompanyRepository extends JpaRepository<UserCompany, Long> {
    List<UserCompany> findByUserId(Long userId);
}
