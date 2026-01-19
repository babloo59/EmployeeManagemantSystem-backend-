package com.bk.ems3.service;

import com.bk.ems3.dto.ReportResponseDTO;
import com.bk.ems3.dto.SubmitReportDTO;
import com.bk.ems3.model.*;
import com.bk.ems3.repository.LeaveRepository;
import com.bk.ems3.repository.ReportRepository;
import com.bk.ems3.repository.TaskRepository;
import com.bk.ems3.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final UserRepository userRepository;
    private final LeaveRepository leaveRepository;
    private final TaskRepository taskRepository;
    private final ReportRepository reportRepository;

    public List<Leave> getMyLeaves(User employee) {
        return leaveRepository.findByEmployee(employee);
    }

    public void applyLeave(String employeeEmail, Leave leave) {

        User employee = userRepository
                .findByEmail(employeeEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        leave.setEmployee(employee);
        leave.setStatus(LeaveStatus.PENDING);

        leaveRepository.save(leave);
    }

    public List<Task> getMyTasks(String employeeEmail){

        User user = userRepository
                .findByEmail(employeeEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return taskRepository.findByEmployee(user);
    }

    public void acceptTask(Long taskId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (!task.getEmployee().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        task.setStatus(TaskStatus.IN_PROGRESS);
        taskRepository.save(task);
    }

    public List<ReportResponseDTO> getMyReport(String employeeEmail){
        User user = userRepository
                .findByEmail(employeeEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return reportRepository.findByEmployee(user)
                .stream()
                .map(report -> new ReportResponseDTO(
                        report.getId(),
                        report.getTask().getTitle(),
                        report.getDescription(),
                        report.getStatus()
                ))
                .toList();
    }

    public List<Task> getInProgressReport(String employeeEmail) {

        User employee = userRepository
                .findByEmail(employeeEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return taskRepository.findByEmployeeAndStatus(
                employee,
                TaskStatus.IN_PROGRESS
        );
    }

    public void submitReport(String email, SubmitReportDTO dto) {

        User employee = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Task task = taskRepository.findById(dto.getTaskId())
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (task.getStatus() != TaskStatus.IN_PROGRESS) {
            throw new RuntimeException("Task is not in progress");
        }

        Report report = new Report();
        report.setEmployee(employee);
        report.setTask(task);
        report.setDescription(dto.getDescription());
        report.setStatus(ReportStatus.PENDING);
        report.setSubmittedAt(LocalDateTime.now());

        reportRepository.save(report);
    }
}
