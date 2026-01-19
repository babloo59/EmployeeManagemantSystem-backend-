package com.bk.ems3.repository;

import com.bk.ems3.model.Task;
import com.bk.ems3.model.TaskStatus;
import com.bk.ems3.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    // Employee ke tasks
    List<Task> findByEmployee(User employee);

    List<Task> findByManager(User manager);

    // Pending tasks (Admin / Manager dashboard)
    List<Task> findByStatus(TaskStatus status);

    List<Task> findByEmployeeAndStatus(User employee, TaskStatus status);

    List<Task> findByManagerAndStatus(User manager, TaskStatus status);

}
