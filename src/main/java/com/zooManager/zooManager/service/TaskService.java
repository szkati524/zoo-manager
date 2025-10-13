package com.zooManager.zooManager.service;

import com.zooManager.zooManager.Employee;
import com.zooManager.zooManager.Task;
import com.zooManager.zooManager.exception.TaskNotFoundException;
import com.zooManager.zooManager.repository.TaskRepository;
import jakarta.transaction.Transactional;
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
    public Task getTaskById(Long id){
        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
    }
    public Task addTask(Task task){
        if (task.getTitle() == null || task.getTitle().isBlank()){
            throw new IllegalArgumentException("Task title cannot be empty");
        }
        if (task.getAssignedTo() == null){
            throw new IllegalArgumentException("Task must be assigned to an employee");
        }
        return taskRepository.save(task);
    }
    @Transactional
    public void markAsCompleted(Long taskId){
        Task task = getTaskById(taskId);
        task.setCompleted(true);
    }
    public void deleteTask(Long taskId){
        if (!taskRepository.existsById(taskId)){
            throw new TaskNotFoundException(taskId);
        }
        taskRepository.deleteById(taskId);
    }
}
