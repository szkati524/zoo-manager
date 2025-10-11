package com.zooManager.zooManager.controller;

import com.zooManager.zooManager.Animal;
import com.zooManager.zooManager.Document;
import com.zooManager.zooManager.Employee;
import com.zooManager.zooManager.repository.AnimalRepository;
import com.zooManager.zooManager.repository.EmployeeRepository;
import com.zooManager.zooManager.service.AnimalService;
import com.zooManager.zooManager.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@WebMvcTest(AnimalController.class)
public class AnimalControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AnimalService animalService;
    @MockBean
    private EmployeeRepository employeeRepository;
    @MockBean
    private EmployeeService employeeService;

    private Animal testAnimal;
    private Employee testEmployee;

    @BeforeEach
    void setUp(){
        testAnimal = new Animal(1L,"Reks","Pies",false);
        testEmployee = new Employee(5L,"Jan","Kowalski");
        testAnimal.setEmployees(Collections.singletonList(testEmployee));


    }
    @Test
    void showAddAnimal_ShouldReturnAddAnimalView()throws Exception{
        mockMvc.perform(get("/add-animal"))
                .andExpect(status().isOk())
                .andExpect(view().name("add-animal"))
                .andExpect(model().attributeExists("animal"));
    }
    @Test
    void searchAnimals_NoFilters_ShouldReturnAllAnimals() throws Exception{
        List<Animal> allAnimals = List.of(testAnimal);
        when(animalService.getAllAnimals()).thenReturn(allAnimals);
        when(employeeService.getAllEmployees()).thenReturn(List.of(testEmployee));
        mockMvc.perform(get("/animals"))
                .andExpect(status().isOk())
                .andExpect(view().name("animals"))
                .andExpect(model().attribute("animals",allAnimals));
        verify(animalService,times(1)).getAllAnimals();
        verify(animalService,never()).searchAnimal(any(),any(),any(),any(),any());
    }
    @Test
    void searchAnimals_WithFilter_ShouldCallSearchAnimal() throws Exception{
        String filterName = "Reks";
        List<Animal> foundAnimals = List.of(testAnimal);
        when(employeeService.getAllEmployees()).thenReturn(List.of(testEmployee));
        when(animalService.searchAnimal(eq(filterName),isNull(),isNull(),isNull(),isNull()))
                .thenReturn(foundAnimals);
        mockMvc.perform(get("/animals")
                .param("name",filterName)
                .param("species","")
                .param("employeeName","")
                        .param("employeeSurname",""))
                .andExpect(status().isOk())
                .andExpect(view().name("animals"))
                        .andExpect(model().attributeExists("animals"))
                                .andExpect(model().attribute("animals",instanceOf(List.class)))
                                        .andExpect(model().attribute("animals",hasSize(1)));
        verify(animalService,times(1)).searchAnimal(eq(filterName),isNull(),isNull(),isNull(),isNull()  );
        verify(animalService,never()).getAllAnimals();
    }
    @Test
void viewAnimal_ShouldReturnAnimalDetailsView() throws Exception{
        when(animalService.findById(1L)).thenReturn(Optional.of(testAnimal));
        mockMvc.perform(get("/animals/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("animal-details"))
                .andExpect(model().attribute("animal",testAnimal));

    }
    @Test
    void viewAnimal_NotFound_ShouldReturn404() throws Exception{
        when(animalService.findById(99L)).thenReturn(Optional.empty());
        mockMvc.perform(get("/animals/99"))
                .andExpect(status().isNotFound());
    }
    @Test
    void addAnimal_WithoutImage_ShouldCallServiceAndShowSuccess() throws Exception{
        when(animalService.addAnimal(any(Animal.class))).thenReturn(testAnimal);
        MockMultipartFile emptyFile = new MockMultipartFile("image","filename.txt","text/plain",new byte[0]);
        mockMvc.perform(multipart("/add-animal")
                .file(emptyFile)
                .flashAttr("animal",new Animal(1L,"Reks","Pies",false)))
                .andExpect(status().isOk())
                .andExpect(view().name("add-animal"))
                .andExpect(model().attribute("success",true));
        verify(animalService,times(1)).addAnimalWithImage(any(Animal.class),any());
    }
    @Test
    void deleteAnimalsById_ShouldCallServiceAndDeleteAndRedirect() throws Exception{
        mockMvc.perform(post("/animals/delete/{id}",1L))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/animals"));
        verify(animalService,times(1)).deleteAnimalById(1L);
    }
    @Test
    void setVaccinationAnimal_ShouldCallServiceAndRedirect() throws Exception{
        mockMvc.perform(post("/animals/vaccination/{id}",1L)
                .param("status","true"))
                .andExpect(redirectedUrl("/animals"));
        verify(animalService,times(1)).toggleVaccination(1L,true);
    }
    @Test
    void assignEmployeeToAnimal_ShouldCallServiceAndRedirect() throws Exception{
        long animalId = 1;
        long employeeId = 5;
        mockMvc.perform(post("/animals/assign/{animalId}",animalId)
                .param("employeeId",String.valueOf(employeeId)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/animals"));
        verify(animalService,times(1)).assignEmployeeToAnimal(eq(animalId),eq(employeeId));
    }
}
