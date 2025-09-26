package com.zooManager.zooManager.service;

import com.zooManager.zooManager.Employee;
import com.zooManager.zooManager.Task;
import com.zooManager.zooManager.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }
    public List<Task> getAllTasks(){
        return taskRepository.findAll();
    }
    public List<Task> getTasksForEmployee(Employee employee){
        return taskRepository.findAllByAssignedTo(employee);
    }
    public Task addTask(Task task){
        return taskRepository.save(task);
    }
    public void markAsCompleted(Long taskId){
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        task.setCompleted(true);
        taskRepository.save(task);
    }
    public void deleteTask(Long taskId){
        taskRepository.deleteById(taskId);
    }
}
