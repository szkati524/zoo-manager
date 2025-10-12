package com.zooManager.zooManager.controller;

import com.zooManager.zooManager.Animal;
import com.zooManager.zooManager.Employee;
import com.zooManager.zooManager.repository.AnimalRepository;
import com.zooManager.zooManager.service.AnimalService;
import com.zooManager.zooManager.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.aspectj.apache.bcel.Repository.instanceOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.hamcrest.Matchers.isA;

@WebMvcTest(EmployeeController.class)
public class EmployeeControllerTest {
    @Autowired
    private MockMvc mockMvc;


    @MockBean
    private EmployeeService employeeService;

    @MockBean
    private AnimalService animalService;

    private Employee testEmployee;
    private Animal testAnimal;

    @BeforeEach
    void setUp() {
        testEmployee = new Employee(1L, "Jan", "Kowalski");
        testAnimal = new Animal(5L, "Reks", "Pies", true);


        testEmployee.setAnimals(new java.util.ArrayList<>());
        testAnimal.setEmployees(new java.util.ArrayList<>());
    }


    private Employee employeeTest;
    private Animal animalTest;

    @Test
    void showEmployees_ShouldReturnEmployeeViewWithAllEmployees() throws Exception{
List<Employee> allEmployees = List.of(testEmployee);
when(employeeService.getAllEmployees()).thenReturn(allEmployees);
mockMvc.perform(get("/employee"))
        .andExpect(status().isOk())
        .andExpect(view().name("employee"))
        .andExpect(model().attribute("employees",allEmployees));
verify(employeeService,times(1)).getAllEmployees();
    }
    @Test
    void showAddEmployees_ShouldReturnAddEmployeeView()throws Exception{
        List<Animal> allAnimals = List.of(testAnimal);
        when(animalService.getAllAnimals()).thenReturn(allAnimals);
        mockMvc.perform(get("/add-employee"))
                .andExpect(status().isOk())
                .andExpect(view().name("add-employee"))
                .andExpect(model().attribute("employee",isA(Employee.class)))
                        .andExpect(model().attributeExists("allAnimals"));
        verify(animalService,times(1)).getAllAnimals();
    }
    @Test
    void viewEmployee_ShouldReturnEmployeeDetailsView() throws Exception{
        when(employeeService.findById(1L)).thenReturn(Optional.of(testEmployee));
        mockMvc.perform(get("/employee/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("employee-details"))
                .andExpect(model().attribute("employee",testEmployee));

    }
    @Test
    void viewEmployee_NotFound_ShouldReturn404() throws Exception{
        when(employeeService.findById(99L)).thenReturn(Optional.empty());
        mockMvc.perform(get("/employee/99"))
                .andExpect(status().isNotFound());

    }
    @Test
    void addEmployee_WithImageAndAnimals_ShouldProcessRelationsAndCallService() throws Exception{
        Long animalId = testAnimal.getId();
        MockMultipartFile mockFile = new MockMultipartFile("image","avatar.jpg","image/jpeg","data".getBytes());
        when(animalService.findAllByIds(anyList())).thenReturn(List.of(testAnimal));
        when(employeeService.addEmployeeWithImage(any(Employee.class),any())).thenReturn(testEmployee   );
        when(employeeService.addEmployee(any(Employee.class))).thenReturn(testEmployee);

        Employee employeeToSave = new Employee(1L,"Jan","Kowalski");
        employeeToSave.setAnimalIds(Collections.singletonList(animalId));
        mockMvc.perform(multipart("/add-employee")
                .file(mockFile)
                .flashAttr("employee",employeeToSave)
                .param("animalIds",String.valueOf(animalId)))
                .andExpect(status().isOk())
                .andExpect(view().name("add-employee"))
                .andExpect(model().attribute("success",true));


        verify(animalService, times(1)).findAllByIds(List.of(animalId));
        verify(employeeService, times(1)).addEmployeeWithImage(
                argThat(e -> e.getAnimals() != null && !e.getAnimals().isEmpty()),
                eq(mockFile));


        assertTrue(testAnimal.getEmployees().contains(employeeToSave));
    }
    @Test
    void addEmployee_ServiceThrowsException_ShouldShowError() throws Exception{
        MockMultipartFile mockFIle = new MockMultipartFile("image","avatar.jpg","image/jpeg","data".getBytes());
        doThrow(new RuntimeException("Błąd I/O lub bazy  danych")).when(employeeService).addEmployeeWithImage(any(Employee.class),any());
        mockMvc.perform(multipart("/add-employee")
                .file(mockFIle)
                .flashAttr("employee",new Employee(1L,"Jan","Kowalski")))
                .andExpect(status().isOk())
                .andExpect(view().name("add-employee"))
                .andExpect(model().attribute("error",true));
        verify(animalService,times(1)).getAllAnimals();
    }
    @Test
    void deleteEmployeeById_ShouldCallServiceAndDeleteAndRedirect()throws Exception{
        Long employeeId = 1L;
        mockMvc.perform(post("/employee/delete/{id}",employeeId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/employee"));
        verify(employeeService,times(1)).deleteEmployeeById(employeeId);

    }
    }




