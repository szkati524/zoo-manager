package com.zooManager.zooManager.repository;

import com.zooManager.zooManager.Animal;

import static org.assertj.core.api.Assertions.assertThat;

import com.zooManager.zooManager.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@DataJpaTest
public class AnimalRepositoryIntegrationTest {
    @Autowired
    private AnimalRepository animalRepository;
@Autowired
private TestEntityManager entityManager;
    @Test
    @DisplayName("Should find animal when searching by name")
    void shouldFindByName() {

        Animal animal = new Animal();
        animal.setName("Reks");
        animal.setSpecies("Pies");
        animal.setCurrentVaccination(true);


        entityManager.persist(animal);


        entityManager.flush();
        entityManager.clear();


        List<Animal> result = animalRepository.searchAnimals("Reks", null, null, null, null);


        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Reks");
    }
    @Test
    @DisplayName("Should return all animals when all parameters are null")
    void shouldReturnAllWhenParametersAreNull(){
        Animal a1 = new Animal(); a1.setName("Reks");
        Animal a2 = new Animal(); a2.setName("Mruczek");
        entityManager.persist(a1);
        entityManager.persist(a2);
        entityManager.flush();
        entityManager.clear();
        List<Animal> result = animalRepository.searchAnimals(null,null,null,null,null);
        assertThat(result).hasSize(2);
    }
    @Test
    @DisplayName("Should find animal by assigned employee name")
    void shouldFindByEmployeeName(){
        Employee emp= new Employee();
        emp.setName("Jan");
        emp.setSurname("Kowalski");
        emp.setUsername("jan123");
        emp.setPassword("password");
        entityManager.persist(emp);
        Animal animal = new Animal();
        animal.setName("Reks");
        animal.setEmployees(new ArrayList<>(Set.of(emp)));
        entityManager.persist(animal);
        entityManager.flush();
        List<Animal> result = animalRepository.searchAnimals(null,null,null,"Jan",null);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Reks");

    }

}
