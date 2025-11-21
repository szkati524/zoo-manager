package com.zooManager.zooManager.repository;

import com.zooManager.zooManager.Employee;
import com.zooManager.zooManager.configuration.EmailChangeToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Repository
public interface EmailChangeTokenRepository extends JpaRepository<EmailChangeToken,Long> {
    Optional<EmailChangeToken> findByToken(String token);
    void deleteByUser(Employee user);
}
