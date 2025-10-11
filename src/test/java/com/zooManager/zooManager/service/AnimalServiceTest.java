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
    private Animal testAnimal2;

    @BeforeEach
    void setUp() {
        testAnimal = new Animal(1L, "Reks", "Pies", true);
        testEmployee = new Employee(5L, "Jan", "Kowalski");
        testAnimal2 = new Animal(2L,"Mruczek","Kot",true);
    }

    @Test
    void addAnimal_ShouldReturnSavedAnimal() {
        when(animalRepository.save(any(Animal.class))).thenReturn(testAnimal);

        Animal result = animalService.addAnimal(new Animal());

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(animalRepository, times(1)).save(any(Animal.class));
    }

    @Test
    void addAnimal_ShouldThrowExceptionWhenAnimalIsNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            animalService.addAnimal(null);
        });
        verify(animalRepository, never()).save(any(Animal.class));
    }

    @Test
    void getAllAnimals_ShouldReturnAllAnimals() {
        List<Animal> animals = Arrays.asList(testAnimal, new Animal(2L, "Burek", "Pies", true));
        when(animalRepository.findAll()).thenReturn(animals);

        List<Animal> result = animalService.getAllAnimals();
        assertFalse(result.isEmpty());
        assertEquals(2, result.size());
        verify(animalRepository, times(1)).findAll();
    }

    @Test
    void getAllAnimals_ShouldReturnEmptyListWhenAnimalsNoExist() {
        when(animalRepository.findAll()).thenReturn(List.of());
        List<Animal> animals = animalService.getAllAnimals();
        assertTrue(animals.isEmpty());
        verify(animalRepository, times(1)).findAll();

    }

    @Test
    void findById_ShouldReturnAnimalWhenFound() {
        when(animalRepository.findById(1L)).thenReturn(Optional.of(testAnimal));
        Optional<Animal> result = animalService.findById(1L);
        assertTrue(result.isPresent());
        assertEquals("Reks", result.get().getName());
    }

    @Test
    void findById_ShouldReturnEmptyOptionalWhenNotFound() {
        when(animalRepository.findById(99L)).thenReturn(Optional.empty());
        Optional<Animal> result = animalService.findById(99L);
        assertFalse(result.isPresent());
    }

    @Test
    void deleteAnimalById_ShouldDeleteAnimalAndRemoveFromEmployees() {
        testAnimal.getEmployees().add(testEmployee);
        testEmployee.getAnimals().add(testAnimal);
        when(animalRepository.findById(1L)).thenReturn(Optional.of(testAnimal));
        animalService.deleteAnimalById(1L);
        assertFalse(testEmployee.getAnimals().contains(testAnimal));
        assertTrue(testAnimal.getEmployees().isEmpty());
        verify(animalRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteAnimalById_ShouldThrowExceptionWhenAnimalNotFound() {
        when(animalRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> animalService.deleteAnimalById(99L));
        verify(animalRepository, never()).deleteById(anyLong());

    }

    @Test
    void toggleVaccination_ShouldSetStatusToTrue() {
        testAnimal.setCurrentVaccination(false);
        when(animalRepository.findById(1L)).thenReturn(Optional.of(testAnimal));
        when(animalRepository.save(any(Animal.class))).thenReturn(testAnimal);
        animalService.toggleVaccination(1L, true);
        assertTrue(testAnimal.getCurrentVaccination());
        verify(animalRepository, times(1)).save(testAnimal);
    }

    @Test
    void toggleVaccination_ShouldThrowExceptionWhenAnimalNotFound() {
        when(animalRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> animalService.toggleVaccination(99L, true));
        verify(animalRepository, never()).save(any(Animal.class));
    }

    @Test
    void assignEmployeeToAnimal_ShouldAssignNewEmployee() {
        when(animalRepository.findById(1L)).thenReturn(Optional.of(testAnimal));
        when(employeeRepository.findById(5L)).thenReturn(Optional.of(testEmployee));
        assertTrue(testAnimal.getEmployees().isEmpty());
        assertTrue(testEmployee.getAnimals().isEmpty());
        animalService.assignEmployeeToAnimal(1L, 5L);
        assertTrue(testAnimal.getEmployees().contains(testEmployee));
        assertTrue(testEmployee.getAnimals().contains(testAnimal));
        verify(animalRepository, times(1)).save(testAnimal);
    }

    @Test
    void assignEmployeeToAnimal_ShouldDoNothingWhenEmployeeAlreadyAssigned() {
        testAnimal.getEmployees().add(testEmployee);
        testEmployee.getAnimals().add(testAnimal);
        when(animalRepository.findById(1L)).thenReturn(Optional.of(testAnimal));
        when(employeeRepository.findById(5l)).thenReturn(Optional.of(testEmployee));
        animalService.assignEmployeeToAnimal(1L, 5L);
        assertEquals(1, testAnimal.getEmployees().size());
        verify(animalRepository, times(1)).save(testAnimal);
    }

    @Test
    void assignEmployeeToAnimal_ShouldThrowExceptionWhenAnimalNotFound() {
        when(animalRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> animalService.assignEmployeeToAnimal(99L, 5L));
        verify(employeeRepository, never()).findById(anyLong());
        verify(animalRepository, never()).save(any(Animal.class));
    }

    @Test
    void assignEmployeeToAnimal_ShouldThrowExceptionWhenEmployeeNotFound() {
        when(animalRepository.findById(1L)).thenReturn(Optional.of(testAnimal));
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> animalService.assignEmployeeToAnimal(1L, 99L));
        verify(animalRepository, never()).save(any(Animal.class));
    }

    @Test
    void searchAnimal_ShouldSearchOnlyByName() {
        String name = "Reks";
        List<Animal> expectedList = List.of(testAnimal);
        when(animalRepository.searchAnimals(eq(name), isNull(), isNull(), isNull(), isNull())).thenReturn(expectedList);
        List<Animal> results = animalService.searchAnimal(name, null, null, null, null);
        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
        assertEquals("Reks", results.get(0).getName());
        verify(animalRepository, times(1)).searchAnimals(name, null, null, null, null);
    }

    @Test
    void searchAnimal_ShouldSearchOnlyBySpecies() {
        String species = "Pies";
        List<Animal> expectedList = List.of(testAnimal);
        when(animalRepository.searchAnimals(isNull(), eq(species), isNull(), isNull(), isNull())).thenReturn(expectedList);
        List<Animal> results = animalService.searchAnimal(null, species, null, null, null);
        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
        assertEquals("Pies", results.get(0).getSpecies());
        verify(animalRepository, times(1)).searchAnimals(null, species, null, null, null);
    }

    @Test
    void searchAnimal_ShouldSearchOnlyByVaccination() {
        boolean currentVaccination = true;
        List<Animal> expectedList = List.of(testAnimal);
        when(animalRepository.searchAnimals(isNull(), isNull(), eq(currentVaccination), isNull(), isNull())).thenReturn(expectedList);
        List<Animal> results = animalService.searchAnimal(null, null, currentVaccination, null, null);
        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
        assertEquals(true, results.get(0).getCurrentVaccination());
        verify(animalRepository, times(1)).searchAnimals(null, null, currentVaccination, null, null);
    }


@Test
    void searchAnimal_ShouldSearchOnlyByEmployeeName(){
        String employeeName = "Jan";
        List<Animal> expectedList = List.of(testAnimal);
       when(animalRepository.searchAnimals(isNull(),isNull(),isNull(),eq(employeeName),isNull()))
               .thenReturn(expectedList);
       List<Animal> results = animalService.searchAnimal(null,null,null,employeeName,null);
       assertFalse(results.isEmpty());
       assertEquals(1,results.size());
       verify(animalRepository,times(1)).searchAnimals(null,null,null,employeeName,null);


}
@Test
    void searchAnimal_ShouldReturnAllAnimalsWhenNoParametersAreProvided(){

    List<Animal> allAnimals = Arrays.asList(testAnimal,testAnimal2);
    when(animalRepository.searchAnimals(isNull(),isNull(),isNull(),isNull(),isNull())).thenReturn(allAnimals);
    List<Animal> results = animalService.searchAnimal(null,null,null,null,null);
    assertFalse(results.isEmpty());
    assertEquals("Reks",results.get(0).getName());
    assertEquals("Mruczek",results.get(1).getName());
    verify(animalRepository,times(1)).searchAnimals(isNull(),isNull(),isNull(),isNull(),isNull());

}
@Test
    void findAllByIds_ShouldReturnListOfAnimals(){
        List<Long> ids = Arrays.asList(1L,2L);
        List<Animal> expectedList  = Arrays.asList(testAnimal,testAnimal2);
        when(animalRepository.findAllById(ids)).thenReturn(expectedList);
        List<Animal> result = animalService.findAllByIds(ids);
        assertEquals(2,result.size());
        verify(animalRepository,times(1)).findAllById(ids);
}
    }


