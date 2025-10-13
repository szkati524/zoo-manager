package com.zooManager.zooManager.controller;

import com.zooManager.zooManager.Employee;
import com.zooManager.zooManager.Task;
import com.zooManager.zooManager.exception.TaskNotFoundException;
import com.zooManager.zooManager.service.EmployeeService;
import com.zooManager.zooManager.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@WebMvcTest(TaskController.class)
public class TaskControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private TaskService taskService;
    @MockBean
    private EmployeeService employeeService;

    private Employee testEmployee;
    private Task testTask;

@BeforeEach
void setUp(){
    testEmployee = new Employee(5L,"Anna","Nowak");
    testTask = new Task(1L,"nakarm lwa","tresc...",testEmployee,false);
    when(employeeService.getAllEmployees()).thenReturn(Collections.singletonList(testEmployee));

}
@Test
void showTasks_NoFilter_ShouldCallGetTasks()throws Exception{
    List<Task> allTasks = Collections.singletonList(testTask);
    when(taskService.getAllTasks()).thenReturn(allTasks);
    mockMvc.perform(get("/tasks"))
            .andExpect(status().isOk())
            .andExpect(view().name("tasks"))
            .andExpect(model().attribute("tasks",hasSize(1)));
    verify(taskService,times(1)).getAllTasks();
}
@Test
    void showTasks_WithEmployeeFilter_ShouldCallGetTasksForExample() throws Exception {
    List<Task> tasksForEmployee = Collections.singletonList(testTask);
    when(employeeService.findById(5L)).thenReturn(Optional.of(testEmployee));
    when(taskService.getTasksForEmployee(testEmployee)).thenReturn(tasksForEmployee);
    mockMvc.perform(get("/tasks")
            .param("employeeId","5"))
            .andExpect(status().isOk())
            .andExpect(view().name("tasks"))
            .andExpect(model().attribute("tasks",hasSize(1)));
    verify(employeeService,times(1)).findById(5L);
    verify(taskService,times(1)).getTasksForEmployee(testEmployee);
    verify(taskService,never()).getAllTasks();
}
@Test
    void showTasks_WithInvalidEmployeeFilter_ShouldReturn404()throws Exception{
    when(employeeService.findById(99L)).thenReturn(Optional.empty());
    mockMvc.perform(get("/tasks")
            .param("employeeId","99"))
            .andExpect(status().isNotFound())
            .andExpect(result -> {
                verify(employeeService,times(1)).findById(99L);
            });
}
@Test
    void addTasks_Success_ShouldCallServiceAndRedirect()throws Exception{
    when(employeeService.findById(5L)).thenReturn(Optional.of(testEmployee));
    mockMvc.perform(post("/tasks/add")
            .param("title","nowe zadanie")
            .param("description","Opis")
            .param("employeeId","5"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/tasks"));
    verify(employeeService,times(1)).findById(5L);
    verify(taskService,times(1)).addTask(any(Task.class     ));
}
@Test
    void addTask_InvalidTitle_ShouldReturn404() throws Exception{
    when(employeeService.findById(5L)).thenReturn(Optional.of(testEmployee));
    mockMvc.perform(post("/tasks/add")
            .param("title"," ")
            .param("description","Opis")
            .param("employeeId","5"))
            .andExpect(status().isBadRequest());
    verify(taskService,never()).addTask(any(Task.class));
}
@Test
        void addTask_InvalidEmployee_ShouldThrowTaskNotFoundException()throws Exception{
    when(employeeService.findById(99L)).thenReturn(Optional.empty());
    mockMvc.perform(post("/tasks/add")
            .param("title","Test")
            .param("description","Opis")
            .param("employeeId","99"))
            .andExpect(status().isNotFound());
}
@Test
     void completeTask_Success_ShouldCallServiceAndRedirect() throws Exception{
    Long taskId = 1L;

    mockMvc.perform(post("/tasks/complete/{id}",taskId))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/tasks"));
    verify(taskService,times(1)).markAsCompleted(taskId);
}
@Test
    void completeTask_NotFound_ShouldPropagateException() throws Exception{
    Long taskId = 99L;
    doThrow(new TaskNotFoundException(taskId)).when(taskService).markAsCompleted(taskId);
    mockMvc.perform(post("/tasks/complete/{id}",taskId))
            .andExpect(status().isNotFound());
}
@Test
    void deleteTask_Success_ShouldCallAndRedirect()throws Exception{
    Long taskId = 1L;
    mockMvc.perform(post("/tasks/delete/{id}",taskId))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/tasks"));
    verify(taskService,times(1)).deleteTask(taskId);

}
@Test
    void deleteTask_NotFound_ShouldPropagateException() throws Exception{
    Long taskId = 99L;
    doThrow(new TaskNotFoundException(taskId)).when(taskService).deleteTask(taskId);
    mockMvc.perform(post("/tasks/delete/{id}",taskId)).andExpect(status().isNotFound());
}

}
