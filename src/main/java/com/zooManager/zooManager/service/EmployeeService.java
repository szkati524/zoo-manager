package com.zooManager.zooManager.service;

import com.zooManager.zooManager.Animal;
import com.zooManager.zooManager.Employee;
import com.zooManager.zooManager.repository.EmployeeRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {
    private final EmployeeRepository employeeRepository;


    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }
    public Employee addEmployee(Employee employee){
       return employeeRepository.save(employee);
    }
    public List<Employee> getAllEmployees(){
        return employeeRepository.findAll();
    }
@Transactional
    public void deleteEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Employee not found"));
        for (Animal aml : employee.getAnimals()){
            aml.getEmployees().remove(employee);
        }
        employee.getAnimals().clear();
        employeeRepository.deleteById(id);
    }
    public Optional<Employee> findById(Long id){
        return employeeRepository.findById(id);
    }
}
