package com.zooManager.zooManager.repository;

import com.zooManager.zooManager.Employee;
import com.zooManager.zooManager.Task;
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
public class TaskRepositoryTest {
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private EmployeeRepository employeeRepository;

    private Employee anna;
    private Employee marek;
    private Task annaTask;
    private Task annaTask2;
    private Task marekTask;
    @BeforeEach
    void setUp(){
        taskRepository.deleteAll();
        employeeRepository.deleteAll();
        anna = new Employee(null,"Anna","Nowak");
        anna.setUsername("anna");
        anna.setPassword("1234");
        anna = employeeRepository.save(anna);
        marek = new Employee(null,"Marek","Nowakowski");
        marek.setUsername("marek");
        marek.setPassword("1234");
        marek = employeeRepository.save(marek);
        annaTask = new Task(null,"Nakarm lwa","Opis A",anna,false);
        annaTask2 = new Task(null,"nakarm pingwina","Opis B",anna,false);
        marekTask = new Task(null,"wyczyść","Opis c",marek,false);
        annaTask = taskRepository.save(annaTask);
        annaTask2 = taskRepository.save(annaTask2);
        marekTask = taskRepository.save(marekTask);

    }
    @Test
    void save_ShouldPersistTask(){
        Task newTask = new Task(null,"nowy test","test",marek,false);
        Task savedTask = taskRepository.save(newTask);
        assertThat(savedTask).isNotNull();
        assertThat(savedTask.getId()).isNotNull();
        assertThat(savedTask.getAssignedTo().getName()).isEqualTo("Marek");
    }
    @Test
    void findById_ShouldFindSavedTask(){
        Optional<Task> found = taskRepository.findById(annaTask.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Nakarm lwa");

    }
    @Test
    void deleteById_ShouldRemoveTask(){
        Long taskIdToDelete = annaTask.getId();
        taskRepository.deleteById(taskIdToDelete);
        assertThat(taskRepository.findById(taskIdToDelete)).isNotPresent();
        assertThat(taskRepository.count()).isEqualTo(2);
    }
    @Test
    void findAllByAssignedTo_ShouldReturnOnlyTasksForGivenEmployee(){
        List<Task> annaTasks = taskRepository.findAllByAssignedTo(anna);
        assertThat(annaTasks).hasSize(2);
        assertThat(annaTasks).extracting(Task::getTitle).containsExactlyInAnyOrder("Nakarm lwa","nakarm pingwina");
        assertThat(annaTasks).extracting(Task::getTitle).doesNotContain("wyczyść");

    }
    @Test
    void findAllByAssignedTo_ShouldReturnEmptyList_WhenNoTasksFound(){
        Employee nonAssignedEmployee = new Employee(null,"Kasia","Jarząbek");
        nonAssignedEmployee.setUsername("none");
        nonAssignedEmployee.setPassword("1234");
        nonAssignedEmployee = employeeRepository.save(nonAssignedEmployee);
        List<Task> kasiaTasks = taskRepository.findAllByAssignedTo(nonAssignedEmployee);
        assertThat(kasiaTasks).isEmpty();
    }

}
