package com.zooManager.zooManager.repository;

import com.zooManager.zooManager.Animal;
import com.zooManager.zooManager.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class AnimalRepositoryTest {
    @Autowired
    private AnimalRepository animalRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
    private Animal reks;
    private Animal mruczek;
    private Employee anna;
    @BeforeEach
    void setUp(){
        animalRepository.deleteAll();
        employeeRepository.deleteAll();
        anna = new Employee(null,"Anna","Nowak");
        anna.setEmail("anna@zoo.com");
        anna.setUsername("anna");
        anna.setPassword("password");
        anna = employeeRepository.save(anna);
        reks = new Animal(null,"Reks","Pies",true   );
        reks.setEmployees(new java.util.ArrayList<>(Collections.singletonList(anna)));
        mruczek = new Animal(null,"Mruczek","Kot",false);
        reks = animalRepository.save(reks);
        mruczek = animalRepository.save(mruczek);
        anna.setAnimals(new java.util.ArrayList<>(Collections.singletonList(reks)));
        employeeRepository.save(anna);
    }
    @Test
            void searchAnimals_NoFilters_ShouldReturnAllAnimals(){
        List<Animal> result = animalRepository.searchAnimals(
                null,null,null,null,null
        );
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Animal::getName).containsExactlyInAnyOrder("Reks","Mruczek");

    }
    @Test
    void searchAnimals_FilterByName_ShouldReturnReks(){
        List<Animal> result = animalRepository.searchAnimals(
                "Reks",null,null,null,null
        );
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Reks");

    }
    @Test
    void searchAnimals_FilterByVaccination_ShouldReturnOnlyVaccinated(){
        List<Animal> result = animalRepository.searchAnimals(
                null,null,true,null,null
        );
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Reks");
    }
    @Test
    void searchAnimals_FiltersEmployeeName_ShouldReturnOnlyReks(){
        List<Animal> result = animalRepository.searchAnimals(
                null,null,null,"anna",null
        );
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Reks");
    }
    @Test
    void searchAnimals_FiltersEmployeeSurname_ShouldReturnOnlyReks(){
        List<Animal> result = animalRepository.searchAnimals(
                null,null,null,null,"nowak"
        );
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Reks");
    }
    @Test
            void save_ShouldPersistAnimal(){
        Animal animal2 = new Animal(null,"Jakub","Pingwin",true );
        Animal savedAnimal = animalRepository.save(animal2);
       assertThat(savedAnimal).isNotNull();
       assertThat(savedAnimal.getId()).isNotNull();
       assertThat(savedAnimal.getName()).isEqualTo("Jakub");



    }
    @Test
            void findAll_ShouldReturnAllAnimals(){
  List<Animal> animals = animalRepository.findAll();
  assertThat(animals).isNotNull();
  assertThat(animals).hasSize(2);
  assertThat(animals).extracting(Animal::getName)
          .containsExactlyInAnyOrder("Reks","Mruczek");
  Animal reksFromDb = animals.stream()
          .filter(a -> a.getName().equals("Reks")).findFirst().orElseThrow();
  assertThat(reksFromDb.getEmployees()).hasSize(1);

    }
    @Test
    void findById_ShouldFindSavedAnimal(){
Long reksId = reks.getId();

        Optional<Animal> foundAnimal = animalRepository.findById(reksId);
        assertThat(foundAnimal).isPresent();
        assertThat(foundAnimal.get().getName()).isEqualTo("Reks");
        assertThat(foundAnimal.get().getEmployees()).hasSize(1);
    }
    @Test
    void deleteById_ShouldDeleteSavedAnimal(){
        Long reksId = reks.getId();
        long employeesBefore = employeeRepository.count();
        assertThat(employeesBefore).isEqualTo(1);
        animalRepository.deleteById(reksId);
        Optional<Animal> deletedAnimal = animalRepository.findById(reksId);
        assertThat(deletedAnimal).isNotPresent();
        assertThat(employeeRepository.count()).isEqualTo(employeesBefore);
        assertThat(animalRepository.count()).isEqualTo(1);


    }



}
