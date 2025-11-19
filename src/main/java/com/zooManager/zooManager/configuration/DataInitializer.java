package com.zooManager.zooManager.configuration;

import ch.qos.logback.core.model.conditional.ElseModel;
import com.zooManager.zooManager.Employee;
import com.zooManager.zooManager.repository.EmployeeRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer {
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(EmployeeRepository employeeRepository, PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @PostConstruct
    public void init(){
      employeeRepository.findByUsername("admin")
              .orElseGet(() -> {
                  Employee admin = new Employee(
                          "Admin",
                          "Super",
                          "admin@example.com",
                          "admin",
                          passwordEncoder.encode("admin123"),
                          "ADMIN"
                  );
                 return employeeRepository.save(admin);
              });

        }
    }

