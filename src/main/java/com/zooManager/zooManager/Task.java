package com.zooManager.zooManager;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    @Lob
    private String description;
    @ManyToOne
    @JoinColumn(name = "employee_id",nullable = false)
    private Employee assignedTo;
    private boolean completed = false;
    @CreationTimestamp
    private LocalDateTime createdAt;

    public Task(){

    }

    public Task(String title, String description, Employee assignedTo) {
        this.title = title;
        this.description = description;
        this.assignedTo = assignedTo;
        this.completed = false;
    }

    public Task(Long id, String title, String description, Employee assignedTo, boolean completed) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.assignedTo = assignedTo;
        this.completed = completed;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Employee getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(Employee assignedTo) {
        this.assignedTo = assignedTo;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    protected void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return completed == task.completed && Objects.equals(id, task.id) && Objects.equals(title, task.title) && Objects.equals(description, task.description) && Objects.equals(assignedTo, task.assignedTo) && Objects.equals(createdAt, task.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, assignedTo, completed, createdAt);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", assignedTo=" + assignedTo +
                ", completed=" + completed +
                ", createdAt=" + createdAt +
                '}';
    }
}

