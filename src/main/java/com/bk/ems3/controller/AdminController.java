package com.bk.ems3.controller;

import java.util.List;
import java.util.Optional;

import com.bk.ems3.dto.AddEmployeeDTO;
import com.bk.ems3.dto.AdminReportResponseDTO;
import com.bk.ems3.dto.AssignTaskDTO;
import com.bk.ems3.dto.UpdateEmployeeDTO;
import com.bk.ems3.model.*;
import com.bk.ems3.repository.LeaveRepository;
import com.bk.ems3.repository.TaskRepository;
import com.bk.ems3.repository.UserRepository;
import com.bk.ems3.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.bk.ems3.service.AdminService;

import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final UserRepository userRepository;
    private final LeaveRepository leaveRepository;
    private final TaskService taskService;
    private final TaskRepository taskRepository;

    @GetMapping("/pending-users")
    public List<User> getPendingUsers() {
        return adminService.getPendingUsers();
    }

    @PutMapping("/approve/{id}")
    public String approveUser(@PathVariable Long id) {
        adminService.approveUser(id);
        return "User approved successfully";
    }

    @PutMapping("/reject/{id}")
    public String rejectUser(@PathVariable Long id) {
        adminService.rejectUser(id);
        return "User rejected";
    }

    @GetMapping("/view-employees")
    public List<User> getEmployees(){
        return adminService.getEmployeesAndManagers();
    }

    @PutMapping("/employee/status/{id}")
    public ResponseEntity<String> updateEmployeeStatus(@PathVariable Long id, @RequestParam UserStatus userStatus){
        adminService.updateEmployeeStatus(id, userStatus);
        return ResponseEntity.ok("Employee status updated to "+userStatus);
    }

    @PostMapping("/add-employee")
    public ResponseEntity<String> addEmployee(@RequestBody AddEmployeeDTO dto){
        adminService.addEmployee(dto);
        return ResponseEntity.ok("Employee is added by the admin");
    }

    @GetMapping("/active-users")
    public List<User> getActiveEmployee(){
        return adminService.getActiveEmployee();
    }
    
    @GetMapping("/employee/{id}")
    public User getEmployeeById(@PathVariable Long id){
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }

    @PutMapping("/update-employee/{id}")
    public ResponseEntity<String> updateEmployee(
            @PathVariable Long id,
            @RequestBody UpdateEmployeeDTO dto
    ){
        adminService.updateEmployee(id, dto);
        return ResponseEntity.ok("Employee updated successfully");
    }

    @GetMapping("/pending")
    public List<Leave> getPendingLeaves() {
        return leaveRepository.findByStatus(LeaveStatus.PENDING);
    }

    @PutMapping("/leave/approve/{id}")
    public ResponseEntity<?> approveLeave(@PathVariable Long id) {
        Leave leave = leaveRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Leave not found"));

        leave.setStatus(LeaveStatus.APPROVED);
        leaveRepository.save(leave);

        return ResponseEntity.ok("Leave approved");
    }

    @PutMapping("/leave/reject/{id}")
    public ResponseEntity<?> rejectLeave(@PathVariable Long id) {
        Leave leave = leaveRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Leave not found"));

        leave.setStatus(LeaveStatus.REJECTED);
        leaveRepository.save(leave);

        return ResponseEntity.ok("Leave rejected");
    }

    @PostMapping("/assign-task")
    public ResponseEntity<?> assignTask(
            @RequestBody AssignTaskDTO dto,
            Authentication auth
    ) {
        User admin = (User) auth.getPrincipal();

        User assignedTo = userRepository.findById(dto.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Task task = new Task();
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setDeadline(dto.getDeadline());
        task.setEmployee(assignedTo);
        task.setManager(admin); // ADMIN is assigning
        task.setStatus(TaskStatus.PENDING);

        taskRepository.save(task);
        return ResponseEntity.ok("Task assigned");
    }

    @GetMapping("/my-assigned-tasks")
    public List<Task> getAllTask(){
        return taskRepository.findAll();
    }

    @GetMapping("/pending-tasks")
    public List<Task> getAllPendingTask(){
        return taskRepository.findByStatus(TaskStatus.PENDING);
    }

    @GetMapping("/pending-reports")
    public List<AdminReportResponseDTO> pendingReports() {
        return adminService.getPendingReports();
    }

    @PutMapping("/approve-report/{id}")
    public ResponseEntity<?> approve(@PathVariable Long id) {
        adminService.approveReport(id);
        return ResponseEntity.ok("Report approved successfully");
    }

    @PutMapping("/reject-report/{id}")
    public ResponseEntity<?> reject(@PathVariable Long id) {
        adminService.rejectReport(id);
        return ResponseEntity.ok("Report rejected successfully");
    }
}
