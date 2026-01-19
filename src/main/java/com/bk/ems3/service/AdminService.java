package com.bk.ems3.service;

import com.bk.ems3.dto.AddEmployeeDTO;
import com.bk.ems3.dto.AdminReportResponseDTO;
import com.bk.ems3.dto.ReportResponseDTO;
import com.bk.ems3.dto.UpdateEmployeeDTO;
import com.bk.ems3.exception.BusinessException;
import com.bk.ems3.model.*;
import com.bk.ems3.repository.ReportRepository;
import com.bk.ems3.repository.TaskRepository;
import com.bk.ems3.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ReportRepository reportRepository;
    private final TaskRepository taskRepository;

    public List<User> getPendingUsers() {
        return userRepository.findByStatus(UserStatus.PENDING);
    }

    public void approveUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);
    }

    public void rejectUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setStatus(UserStatus.REJECTED);
        userRepository.save(user);
    }

    public List<User> getEmployeesAndManagers() {
        return userRepository.findByRoleIn(
                List.of(Role.EMPLOYEE, Role.MANAGER)
        );
    }

    public List<User> getActiveEmployee(){
        return userRepository.findByStatus(UserStatus.ACTIVE);
    }

    @Transactional
    public void updateEmployeeStatus(Long userId, UserStatus userStatus) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserStatus newStatus =
                (user.getStatus() == UserStatus.ACTIVE)
                        ? UserStatus.INACTIVE
                        : UserStatus.ACTIVE;

        if (user.getStatus() == newStatus) {
            throw new IllegalStateException(
                    "User status already " + newStatus);
        }

        int rows = userRepository.updateStatus(userId, newStatus);

        if (rows == 0) {
            throw new RuntimeException("Status update failed");
        }

        System.out.println("Status updated in DB to: " + newStatus);
    }

    @Transactional
    public void addEmployee(AddEmployeeDTO dto){
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalStateException("Email already exists");
        }

        User user = new User();
        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode("1234"));
        user.setRole(dto.getRole());
        user.setDepartment(dto.getDepartment());
        user.setDesignation(dto.getDesignation());
        user.setManager(null);
        if(dto.getRole().equals(Role.EMPLOYEE)) {
            user.setStatus(UserStatus.PENDING);
            user.setFirstLogin(true);
        }else {
            user.setStatus(UserStatus.ACTIVE);
            user.setFirstLogin(false);
        }

        userRepository.save(user);
        System.out.println("Employee created with status "+user.getStatus()+" : " + user.getEmail());
    }

    @Transactional
    public void updateEmployee(Long id, UpdateEmployeeDTO dto){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setDepartment(dto.getDepartment());
        user.setDesignation(dto.getDesignation());
        user.setStatus(dto.getStatus());

        userRepository.save(user);
    }

    public List<AdminReportResponseDTO> getPendingReports() {

        return reportRepository.findByStatus(ReportStatus.PENDING)
                .stream()
                .map(r -> new AdminReportResponseDTO(
                        r.getId(),
                        r.getEmployee().getFullName(),
                        r.getTask().getTitle(),
                        r.getDescription(),
                        r.getStatus()
                ))
                .toList();
    }

    public void approveReport(Long reportId) {

        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        Task task = report.getTask();

        report.setStatus(ReportStatus.APPROVED);
        task.setStatus(TaskStatus.COMPLETED);

        reportRepository.save(report);
        taskRepository.save(task);
    }

    public void rejectReport(Long reportId) {

        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new BusinessException("Report not found"));

        Task task = report.getTask();

        if (task.getStatus() == TaskStatus.COMPLETED) {
            throw new BusinessException("Task already completed. Cannot reject report.");
        }

        report.setStatus(ReportStatus.REJECTED);
        task.setStatus(TaskStatus.IN_PROGRESS);

        reportRepository.save(report);
        taskRepository.save(task);
    }

}
