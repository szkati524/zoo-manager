package com.zooManager.zooManager.service;

import com.zooManager.zooManager.Animal;
import com.zooManager.zooManager.Employee;
import com.zooManager.zooManager.repository.AnimalRepository;
import com.zooManager.zooManager.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {
    @Mock
    private AnimalRepository animalRepository;
    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private EmployeeService employeeService;
    private Employee testEmployee;
    private Employee testEmployee2;
    private Animal testAnimal;
    @BeforeEach
    void setUp() {
        testEmployee = new Employee(1L, "Jan", "Kowalski");
        testEmployee2 = new Employee(2L, "Anna", "Nowak");
        testAnimal = new Animal(5L, "Reks", "Pies", true);
        testEmployee.setAnimals(new ArrayList<>(List.of(testAnimal)));
        testAnimal.setEmployees(new ArrayList<>(List.of(testEmployee)));
    }
    @Test
    void addEmployee_ShouldReturnSavedEmployee(){
        when(employeeRepository.save(any(Employee.class))).thenReturn(testEmployee);
        Employee result = employeeService.addEmployee(new Employee());
        assertNotNull(result);
        assertEquals(1L,result.getId());
        verify(employeeRepository,times(1)).save(any(Employee.class));
    }
    @Test
    void getAllEmployees_ShouldReturnAllEmployees(){
        List<Employee> expectedEmployee = Arrays.asList(testEmployee,testEmployee2  );
        when(employeeRepository.findAll()).thenReturn(expectedEmployee);
        List<Employee> result = employeeService.getAllEmployees();
        assertNotNull(result);
        assertEquals(2,result.size());
        assertEquals("Jan",result.get(0).getName());
        verify(employeeRepository,times(1)).findAll();
    }
    @Test
    void findById_ShouldReturnEmployee_WhenFound(){
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));
        Optional<Employee> result = employeeService.findById(1L);
        assertTrue(result.isPresent());
        assertEquals("Jan",result.get().getName());
        verify(employeeRepository,times(1)).findById(1L);
    }
@Test
    void findById_ShouldReturnEmptyOptional_WhenNotFound(){
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());
        Optional<Employee> result = employeeService.findById(99L);
        assertFalse(result.isPresent());
        verify(employeeRepository,times(1)).findById(99L);
}
@Test
    void deleteEmployeeById_ShouldDeleteEmployeeAndRemoveFromAnimals(){
        Long employeeId = testEmployee.getId();
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(testEmployee));
        assertTrue(testAnimal.getEmployees().contains(testEmployee));
        employeeService.deleteEmployeeById(employeeId);
        assertFalse(testAnimal.getEmployees().contains(testEmployee));
        assertTrue(testEmployee.getAnimals().isEmpty());
        verify(employeeRepository,times(1)).deleteById(employeeId);
        verify(employeeRepository,times(1)).findById(employeeId);
}
@Test
    void deleteEmployeeById_ShouldThrowException_WhenEmployeeNotFound(){
        Long nonExistsId = 99L;
        when(employeeRepository.findById(nonExistsId)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class,() -> {
            employeeService.deleteEmployeeById(nonExistsId);

        });
        verify(employeeRepository,never()).deleteById(anyLong());
}
@Test
    void addEmployeeWithImage_ShouldSaveEmployee_WithoutImageWhenFileIsEmpty() throws Exception {
    MockMultipartFile emptyFile = new MockMultipartFile("image","empty.txt","text/plain",new byte[0]);
when(employeeRepository.save(any(Employee.class))).thenReturn(testEmployee);
employeeService.addEmployeeWithImage(testEmployee,emptyFile);
assertNull(testEmployee.getImagePath());
verify(employeeRepository,times(1)).save(testEmployee);
}
@Test
    void addEmployeeWithImage_ShouldThrowException_WhenEmployeeIsNull(){
        MockMultipartFile mockFile = new MockMultipartFile("image","test.jpg","image/jpeg","test".getBytes());
        assertThrows(IllegalArgumentException.class,() -> {
            employeeService.addEmployeeWithImage(null,mockFile);

        });
        verify(employeeRepository,never()).save(any(Employee.class  ));
}

}
