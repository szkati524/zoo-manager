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


;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AnimalServiceTest {
    @Mock
    private AnimalRepository animalRepository;
    @Mock
    private EmployeeRepository employeeRepository;
    @InjectMocks
    private AnimalService animalService;

    private Animal testAnimal;
    private Employee testEmployee;

    @BeforeEach
    void setUp(){
        testAnimal = new Animal(1L,"Reks","Pies",false);
        testEmployee = new Employee(5L,"Jan","Kowalski");
    }
    @Test
    void addAnimal_ShouldReturnSavedAnimal(){
        when(animalRepository.save(any(Animal.class))).thenReturn(testAnimal);

        Animal result = animalService.addAnimal(new Animal());

        assertNotNull(result);
        assertEquals(1L,result.getId());
        verify(animalRepository,times(1)).save(any(Animal.class));
    }
    @Test
    void addAnimal_ShouldThrowExceptionWhenAnimalIsNull(){
        assertThrows(IllegalArgumentException.class,()-> {
            animalService.addAnimal(null);
        });
        verify(animalRepository,never()).save(any(Animal.class));
    }
    @Test
    void getAllAnimals_ShouldReturnAllAnimals(){
        List<Animal> animals = Arrays.asList(testAnimal,new Animal(2L,"Burek","Pies",true));
        when(animalRepository.findAll()).thenReturn(animals);

        List<Animal> result = animalService.getAllAnimals();
        assertFalse(result.isEmpty());
        assertEquals(2,result.size());
        verify(animalRepository,times(1)).findAll();
    }
    @Test
    void getAllAnimals_ShouldReturnEmptyListWhenAnimalsNoExist(){
        when(animalRepository.findAll()).thenReturn(List.of());
        List<Animal> animals = animalService.getAllAnimals();
        assertTrue(animals.isEmpty());
        verify(animalRepository,times(1)).findAll();

    }
    @Test
    void findById_ShouldReturnAnimalWhenFound(){
        when(animalRepository.findById(1L)).thenReturn(Optional.of(testAnimal));
        Optional<Animal> result = animalService.findById(1L);
        assertTrue(result.isPresent());
        assertEquals("Reks",result.get().getName());
    }
    @Test
    void findById_ShouldReturnEmptyOptionalWhenNotFound(){
        when(animalRepository.findById(99L)).thenReturn(Optional.empty());
        Optional<Animal> result = animalService.findById(99L);
        assertFalse(result.isPresent());
    }
    @Test
    void deleteAnimalById_ShouldDeleteAnimalAndRemoveFromEmployees(){
        testAnimal.getEmployees().add(testEmployee);
        testEmployee.getAnimals().add(testAnimal);
        when(animalRepository.findById(1L)).thenReturn(Optional.of(testAnimal));
        animalService.deleteAnimalById(1L);
        assertFalse(testEmployee.getAnimals().contains(testAnimal));
        assertTrue(testAnimal.getEmployees().isEmpty());
        verify(animalRepository,times(1)).deleteById(1L);
    }
    @Test
    void deleteAnimalById_ShouldThrowExceptionWhenAnimalNotFound(){
        when(animalRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class,() -> animalService.deleteAnimalById(99L));
        verify(animalRepository,never()).deleteById(anyLong());

        }
        
    }


