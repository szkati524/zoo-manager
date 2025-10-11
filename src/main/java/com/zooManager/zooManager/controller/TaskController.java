package com.zooManager.zooManager.controller;

import com.zooManager.zooManager.Employee;
import com.zooManager.zooManager.Task;
import com.zooManager.zooManager.service.EmployeeService;
import com.zooManager.zooManager.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Controller
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;
    private final EmployeeService employeeService;

    public TaskController(TaskService taskService, EmployeeService employeeService) {
        this.taskService = taskService;
        this.employeeService = employeeService;
    }
    @GetMapping
    public String showTasks(Model model,@RequestParam(required = false) Long employeeId){
        List<Task> tasks;
        if (employeeId != null){
            Employee emp = employeeService.findById(employeeId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Employee not found"));
            tasks = taskService.getTasksForEmployee(emp);
        } else {
            tasks = taskService.getAllTasks();
        }
        model.addAttribute("tasks",tasks);
        model.addAttribute("employees",employeeService.getAllEmployees());
        return "tasks";
    }

    @PostMapping("/add")
    public String addTask(@RequestParam String title,
                          @RequestParam String description,
                          @RequestParam Long employeeId) {
        Employee emp = employeeService.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        Task task = new Task(title,description,emp);
        taskService.addTask(task);
        return "redirect:/tasks";
    }
    @PostMapping("/complete/{id}")
    public String completeTask(@PathVariable Long id){
        taskService.markAsCompleted(id);
        return "redirect:/tasks";
    }
    @PostMapping("/delete/{id}")
    public String deleteTask(@PathVariable Long id){
        taskService.deleteTask(id);
        return "redirect:/tasks";
    }

}
