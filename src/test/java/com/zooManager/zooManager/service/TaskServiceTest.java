package com.zooManager.zooManager.service;

import com.zooManager.zooManager.Employee;
import com.zooManager.zooManager.Task;
import com.zooManager.zooManager.exception.TaskNotFoundException;
import com.zooManager.zooManager.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {
    @Mock
    private TaskRepository taskRepository;
    @InjectMocks
    private TaskService taskService;

    private Employee testEmployee;
    private Task testTask;
    private Task testTask2;
    @BeforeEach
    void setUp(){
        testEmployee = new Employee(1L,"Anna","Nowak");
        testTask = new Task(10L,"Nakarm lwa","nakarm lwa białym mięsem",testEmployee,false);
        testTask2 = new Task(11L,"Wyczyść akwarium","Użyj nowej pompy",testEmployee,false   );

    }
    @Test
    void getAlLTasks_ShouldReturnAllTasks(){
        List<Task> expectedTasks = Arrays.asList(testTask,testTask2);
        when(taskRepository.findAll()).thenReturn(expectedTasks);
        List<Task> result = taskService.getAllTasks();
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Task::getTitle).containsExactlyInAnyOrder("Nakarm lwa","Wyczyść akwarium");
        verify(taskRepository,times(1)).findAll();
    }
    @Test
    void getTasksForEmployee_ShouldReturnTasksForSpecificationEmployee(){
        List<Task> expectedTasks = Arrays.asList(testTask,testTask2);
        when(taskRepository.findAllByAssignedTo(eq(testEmployee))).thenReturn(expectedTasks);
        List<Task> result = taskService.getTasksForEmployee(testEmployee);
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getAssignedTo()).isEqualTo(testEmployee);
        verify(taskRepository,times(1)).findAllByAssignedTo(eq(testEmployee));

    }
    @Test
    void getTaskById_ShouldReturnTask_WhenFound(){
        when(taskRepository.findById(10L)).thenReturn(Optional.of(testTask));
        Task result = taskService.getTaskById(10L);
        assertThat(result.getTitle()).isEqualTo("Nakarm lwa");
        verify(taskRepository,times(1)).findById(10l);

    }
   @Test
    void getTaskById_ShouldThrowException_WhenNotFound(){
        Long nonExistId = 99L;
        when(taskRepository.findById(nonExistId)).thenReturn(Optional.empty());
        assertThrows(TaskNotFoundException.class,  () -> {
        taskService.getTaskById(nonExistId);

       });
        verify(taskRepository,times(1)).findById(nonExistId);

   }
   @Test
    void addTask_Success_ShouldSaveAndReturnTask(){
        Task newTask = new Task(null,"doCiebie","Przyjdz do biura",testEmployee,false    );
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);
        Task result = taskService.addTask(newTask);
        assertThat(result.getId()).isEqualTo(10L);
        verify(taskRepository,times(1)).save(newTask);
    }
    @Test
    void addTask_ShouldThrowException_WhenTitleIsEmpty(){
        Task taskWithEmptyTitle = new Task(null," ","tresc...",testEmployee,false   );
        assertThrows(IllegalArgumentException.class,() -> {
            taskService.addTask(taskWithEmptyTitle);
        });
        verify(taskRepository,never()).save(any(Task.class));
    }
    @Test
    void addTask_ShouldThrowException_WhenAssignedToIsNull(){
        Task taskWithNoAssignedTo = new Task(null,"ttt","tresc...",null,false);
        assertThrows(IllegalArgumentException.class,() -> {
            taskService.addTask(taskWithNoAssignedTo);
        });
        verify(taskRepository,never()).save(any(Task.class));
    }
    @Test
    void markAsCompleted_ShouldSetCompletedToTrue(){
        when(taskRepository.findById(10L)).thenReturn(Optional.of(testTask));
        testTask.setCompleted(false);
        taskService.markAsCompleted(10L);
        assertTrue(testTask.isCompleted());
        verify(taskRepository,times(1)).findById(10L);
        verify(taskRepository,never()).save(any(Task.class));
    }
    @Test
    void markAsCompleted_ShouldThrowException_WhenTaskNotFound(){
        Long nonExistId = 99L;
        when(taskRepository.findById(nonExistId)).thenReturn(Optional.empty());
        assertThrows(TaskNotFoundException.class,() -> {
            taskService.markAsCompleted(nonExistId);
        });
        verify(taskRepository,times(1)).findById(nonExistId);

    }
    @Test
    void deleteTask_ShouldDeleteTask_WhenExist(){
        Long taskId = 10L;
        when(taskRepository.existsById(10L)).thenReturn(true);
        taskService.deleteTask(taskId);
        verify(taskRepository,times(1)).existsById(taskId);
        verify(taskRepository,times(1)).deleteById(taskId);
    }
    @Test
    void deleteTask_ShouldThrowException_WhenNotExist(){
        Long taskId = 99L;
        when(taskRepository.existsById(99L)).thenReturn(false);
        assertThrows(TaskNotFoundException.class,() -> {
            taskService.deleteTask(taskId);
        });
        verify(taskRepository,times(1)).existsById(taskId);
        verify(taskRepository,never()).deleteById(anyLong());
    }

}

