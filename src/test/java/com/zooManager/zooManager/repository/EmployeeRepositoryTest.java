package com.zooManager.zooManager.repository;

import com.zooManager.zooManager.Animal;
import com.zooManager.zooManager.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class EmployeeRepositoryTest {
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private AnimalRepository animalRepository;

    private Employee testEmployee;

    @BeforeEach
    void setUp(){
employeeRepository.deleteAll();
testEmployee = new Employee(null,"Jan","Kowalski");
testEmployee.setEmail("jan.kowalski@o2.pl");


    }
    @Test
    void save_ShouldPersistEmployee(){
        Employee savedEmployee = employeeRepository.save(testEmployee);
        assertThat(savedEmployee).isNotNull();
        assertThat(savedEmployee.getId()).isNotNull();
        assertThat(savedEmployee.getName()).isEqualTo("Jan");
    }
    @Test
    void findAll_ShouldReturnAllEmployees(){
        Employee employee2 = new Employee(null,"anna","nowak");
        employeeRepository.save(testEmployee);
        employeeRepository.save(employee2);
        List<Employee> employees = employeeRepository.findAll();
        assertThat(employees).hasSize(2);
        assertThat(employees).extracting(Employee::getName).containsExactlyInAnyOrder("Jan","anna");

    }
    @Test
    void findById_ShouldFindSavedEmployee(){
        Employee savedEmployee = employeeRepository.save(testEmployee);
        Optional<Employee> foundEmployee = employeeRepository.findById(savedEmployee.getId());
        assertThat(foundEmployee).isPresent();
        assertThat(foundEmployee.get().getSurname()).isEqualTo("Kowalski");
    }
    @Test
    void deleteById_ShouldRemoveEmployee(){
        Employee savedEmployee = employeeRepository.save(testEmployee);
        Long id = savedEmployee.getId();
        employeeRepository.deleteById(id);
        Optional<Employee> deletedEmployee = employeeRepository.findById(id);
        assertThat(deletedEmployee).isNotPresent();
        assertThat(employeeRepository.count()).isEqualTo(0);
    }


}
