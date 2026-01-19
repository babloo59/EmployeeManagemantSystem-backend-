package com.bk.ems3.service;

import com.bk.ems3.dto.*;
import com.bk.ems3.exception.BusinessException;
import com.bk.ems3.model.*;
import com.bk.ems3.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ManagerService {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final LeaveRepository leaveRepository;
    private final ReportRepository reportRepository;
    private final PasswordEncoder passwordEncoder;

    // ================= HELPER =================
    private User getManager(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Manager not found"));
    }

    // ================= USERS =================
    public List<User> getActiveEmployee() {
        return userRepository.findByRoleAndStatus(Role.EMPLOYEE, UserStatus.ACTIVE);
    }

    // ================= TASKS =================
    public List<Task> getPendingTasks(String managerEmail) {
        User manager = getManager(managerEmail);
        return taskRepository.findByManagerAndStatus(
                manager,
                TaskStatus.PENDING
        );
    }

    // ================= LEAVES =================
    public List<Leave> getPendingLeaves(String managerEmail) {

        User manager = getManager(managerEmail);

        return leaveRepository.findByEmployee_ManagerAndStatus(
                manager,
                LeaveStatus.PENDING
        );
    }

    // ================= REPORTS =================
    public List<ManagerReportResponseDTO> getPendingReports(String managerEmail) {

        User manager = getManager(managerEmail);

        return reportRepository
                .findByEmployee_ManagerAndStatus(manager, ReportStatus.PENDING)
                .stream()
                .map(r -> new ManagerReportResponseDTO(
                        r.getId(),
                        r.getEmployee().getFullName(),
                        r.getTask().getTitle(),
                        r.getDescription(),
                        r.getStatus()
                ))
                .toList();
    }
//
//    // ================= APPROVE REPORT =================
    public void approveReport(Long reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        Task task = report.getTask();

        report.setStatus(ReportStatus.APPROVED);
        task.setStatus(TaskStatus.COMPLETED);

        reportRepository.save(report);
        taskRepository.save(task);
    }

    // ================= REJECT REPORT =================
    public void rejectReport(Long reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new BusinessException("Report not found"));

        Task task = report.getTask();

        if (task.getStatus() == TaskStatus.COMPLETED) {
            throw new BusinessException("Task already completed");
        }

        report.setStatus(ReportStatus.REJECTED);
        task.setStatus(TaskStatus.IN_PROGRESS);

        reportRepository.save(report);
        taskRepository.save(task);
    }

    public List<ManagerUserResponseDTO> getAllEmployeesAndManagers() {

        List<User> users = userRepository.findByRoleIn(
                List.of(Role.EMPLOYEE)
        );

        return users.stream()
                .map(u -> new ManagerUserResponseDTO(
                        u.getId(),
                        u.getFullName(),
                        u.getEmail(),
                        u.getRole(),
                        u.getStatus()
                ))
                .toList();
    }

    // 🔹 Block / Unblock
    public void updateUserStatus(Long userId, UserStatus status) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setStatus(status);
        userRepository.save(user);
    }

    // 🔹 Get user for edit
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // 🔹 Update user
    public void updateUser(Long id, User updatedUser) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setFullName(updatedUser.getFullName());
        user.setEmail(updatedUser.getEmail());
        user.setRole(updatedUser.getRole());
        user.setDepartment(updatedUser.getDepartment());
        user.setDesignation(updatedUser.getDesignation());

        userRepository.save(user);
    }

    public void addEmployee(AddEmployeeRequestDTO dto) {

        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setRole(Role.EMPLOYEE);
        user.setDepartment(dto.getDepartment());
        user.setDesignation(dto.getDesignation());

        // 🔐 default password
        user.setPassword(passwordEncoder.encode("1234"));

        // 🔒 default status
        user.setStatus(UserStatus.PENDING);
        user.setFirstLogin(true);

        userRepository.save(user);
    }

    // 🔹 GET EMPLOYEE BY ID (EDIT PAGE)
    public User getEmployeeById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
    }

    // 🔹 UPDATE EMPLOYEE
    public void updateEmployee(Long id, UpdateEmployeeDTO dto) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setDepartment(dto.getDepartment());
        user.setDesignation(dto.getDesignation());
        user.setStatus(dto.getStatus());

        userRepository.save(user);
    }

    public List<User> pendingUsers() {
        return userRepository.findByStatus(UserStatus.PENDING);
    }

    public void approveUser(Long id) {
        User u = userRepository.findById(id).orElseThrow();
        u.setStatus(UserStatus.ACTIVE);
    }

    public void rejectUser(Long id) {
        User u = userRepository.findById(id).orElseThrow();
        u.setStatus(UserStatus.REJECTED);
    }

    // ================== LEAVES ==================
    public List<Leave> pendingLeaves() {
        return leaveRepository.findByStatus(LeaveStatus.PENDING);
    }

    public void approveLeave(Long id) {
        Leave l = leaveRepository.findById(id).orElseThrow();
        l.setStatus(LeaveStatus.APPROVED);
    }

    public void rejectLeave(Long id) {
        Leave l = leaveRepository.findById(id).orElseThrow();
        l.setStatus(LeaveStatus.REJECTED);
    }

    public void applyLeave(User user, Leave leave) {
        leave.setEmployee(user);
        leave.setStatus(LeaveStatus.PENDING);
        leaveRepository.save(leave);
    }

    public List<Leave> myLeaves(User user) {
        return leaveRepository.findByEmployee(user);
    }

    // ================== TASKS ==================
    public void assignTask(User manager, Long empId, Task task) {
        User emp = userRepository.findById(empId).orElseThrow();
        task.setEmployee(emp);
        task.setManager(manager);
        task.setStatus(TaskStatus.PENDING);
        taskRepository.save(task);
    }

    public List<Task> myAssignedTasks(User manager) {
        return taskRepository.findByManager(manager);
    }

    public List<Task> myTasks(User user) {
        return taskRepository.findByEmployee(user);
    }

    public void acceptTask(Long taskId) {
        Task t = taskRepository.findById(taskId).orElseThrow();
        t.setStatus(TaskStatus.IN_PROGRESS);
    }

    public List<Task> inProgressTasks(User user) {
        return taskRepository.findByEmployeeAndStatus(user, TaskStatus.IN_PROGRESS);
    }

    // ================== REPORTS ==================
    public void submitReport(User user, Long taskId, String desc) {
        Task task = taskRepository.findById(taskId).orElseThrow();

        Report r = new Report();
        r.setTask(task);
        r.setEmployee(user);
        r.setDescription(desc);
        r.setStatus(ReportStatus.PENDING);

        reportRepository.save(r);
    }

    public List<Report> pendingReports() {
        return reportRepository.findByStatus(ReportStatus.PENDING);
    }

    public List<ReportResponseDTO> myReports(User user) {

        return reportRepository.findByEmployee(user)
                .stream()
                .map(r -> new ReportResponseDTO(
                        r.getId(),
                        r.getTask().getTitle(),
                        r.getDescription(),
                        r.getStatus()
                ))
                .toList();
    }

}
