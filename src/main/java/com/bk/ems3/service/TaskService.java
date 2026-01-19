package com.bk.ems3.service;

import com.bk.ems3.dto.AssignTaskDTO;
import com.bk.ems3.model.Task;
import com.bk.ems3.model.TaskStatus;
import com.bk.ems3.model.User;
import com.bk.ems3.repository.TaskRepository;
import com.bk.ems3.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public void assignTask(AssignTaskDTO dto, String managerEmail) {

        User manager = userRepository.findByEmail(managerEmail)
                .orElseThrow(() -> new RuntimeException("Manager not found"));

        User employee = userRepository.findById(dto.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        Task task = new Task();
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setDeadline(dto.getDeadline());
        task.setStatus(TaskStatus.PENDING);
        task.setEmployee(employee);
        task.setManager(manager);

        taskRepository.save(task);

        System.out.println("✅ Task assigned to " + employee.getEmail());
    }
}
