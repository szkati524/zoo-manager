package com.zooManager.zooManager.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
@ResponseStatus(value = HttpStatus.NOT_FOUND,reason = "Task not found")
public class TaskNotFoundException extends RuntimeException{

    public TaskNotFoundException(Long id){
        super("Task with ID " + id + " not found");
    }

}
