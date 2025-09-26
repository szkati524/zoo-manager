package com.zooManager.zooManager.repository;

import com.zooManager.zooManager.Employee;
import com.zooManager.zooManager.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface TaskRepository extends JpaRepository<Task,Long> {
    List<Task> findAllByAssignedTo(Employee employee);
}
