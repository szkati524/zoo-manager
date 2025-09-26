package com.zooManager.zooManager.controller;

import com.zooManager.zooManager.Employee;
import com.zooManager.zooManager.Task;
import com.zooManager.zooManager.service.EmployeeService;
import com.zooManager.zooManager.service.TaskService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
                    .orElseThrow(() -> new RuntimeException("Employee not found"));
            tasks = taskService.getTasksForEmployee(emp);
        } else {
            tasks = taskService.getAllTasks();
        }
        model.addAttribute("tasks",tasks);
        return "tasks";
    }

    @GetMapping("/add")
    public String addTask(@RequestParam String title,
                          @RequestParam String description,
                          @RequestParam Long employeeId) {
        Employee emp = employeeService.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        Task task = new Task(title,description,emp);
        taskService.addTask(task);
        return "redirect:/tasks";
    }

}
