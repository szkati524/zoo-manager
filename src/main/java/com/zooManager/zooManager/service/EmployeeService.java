package com.zooManager.zooManager.service;

import com.zooManager.zooManager.Animal;
import com.zooManager.zooManager.Employee;
import com.zooManager.zooManager.repository.EmployeeRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class EmployeeService {
    private final EmployeeRepository employeeRepository;


    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public Employee addEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    public Employee addEmployeeWithImage(Employee employee, MultipartFile image) throws Exception {
        if (employee == null) {
            throw new IllegalArgumentException("Employee cannot be null");
        }
        if (image != null && !image.isEmpty()) {
            Path projectDir = Paths.get(System.getProperty("user.dir"));
            Path uploadPath = projectDir.resolve("uploads");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            image.transferTo(filePath.toFile());
            employee.setImagePath(fileName);
        }
        return employeeRepository.save(employee);
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @Transactional
    public void deleteEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        for (Animal aml : employee.getAnimals()) {
            aml.getEmployees().remove(employee);
        }
        employee.getAnimals().clear();
        employeeRepository.deleteById(id);
    }

    public Optional<Employee> findById(Long id) {
        return employeeRepository.findById(id);
    }


    public Optional<Employee> findByUserName(String username) {
       return employeeRepository.findByUsername(username);
    }
}
