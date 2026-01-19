package com.bk.ems3.controller;

import com.bk.ems3.dto.*;
import com.bk.ems3.model.*;
import com.bk.ems3.repository.UserRepository;
import com.bk.ems3.service.ManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/manager")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class ManagerController {

    private final ManagerService managerService;
    private final UserRepository userRepository;

    // ✅ Active users under manager
    @GetMapping("/active-users")
    public List<User> getActiveUsers() {
        return managerService.getActiveEmployee();
    }

    // ✅ Pending tasks under manager
    @GetMapping("/pending-tasks")
    public List<Task> getPendingTasks(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return managerService.getPendingTasks(user.getEmail());
    }

    // ✅ Pending leave requests
    @GetMapping("/pending")
    public List<Leave> getPendingLeaves(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return managerService.getPendingLeaves(user.getEmail());
    }

    // ✅ Pending reports (MOST IMPORTANT)
    @GetMapping("/pending-reports")
    public List<ManagerReportResponseDTO> getPendingReports(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return managerService.getPendingReports(user.getEmail());
    }

    // ✅ Approve report
    @PutMapping("/approve-report/{reportId}")
    public String approveReport(@PathVariable Long reportId) {
        managerService.approveReport(reportId);
        return "Report approved successfully";
    }

    // ✅ Reject report
    @PutMapping("/reject-report/{reportId}")
    public String rejectReport(@PathVariable Long reportId) {
        managerService.rejectReport(reportId);
        return "Report rejected successfully";
    }

    @GetMapping("/view-employees")
    public ResponseEntity<List<ManagerUserResponseDTO>> viewEmployees() {
        return ResponseEntity.ok(
                managerService.getAllEmployeesAndManagers()
        );
    }

    // 🔹 BLOCK / UNBLOCK
    @PutMapping("/employee/status/{id}")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long id,
            @RequestParam UserStatus userStatus
    ) {
        managerService.updateUserStatus(id, userStatus);
        return ResponseEntity.ok("Status updated");
    }

    // 🔹 GET USER (EDIT)
    @GetMapping("/edit-user/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(managerService.getUserById(id));
    }

    // 🔹 UPDATE USER
    @PutMapping("/edit-user/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable Long id,
            @RequestBody User user
    ) {
        managerService.updateUser(id, user);
        return ResponseEntity.ok("User updated");
    }

    @PostMapping("/add-employee")
    public ResponseEntity<?> addEmployee(
            @RequestBody AddEmployeeRequestDTO request
    ) {
        managerService.addEmployee(request);
        return ResponseEntity.ok("User created successfully");
    }

    // ✏️ GET EMPLOYEE (EDIT)
    @GetMapping("/employee/{id}")
    public ResponseEntity<User> getEmployee(@PathVariable Long id) {
        return ResponseEntity.ok(managerService.getEmployeeById(id));
    }

    // ✏️ UPDATE EMPLOYEE
    @PutMapping("/update-employee/{id}")
    public ResponseEntity<?> updateEmployee(
            @PathVariable Long id,
            @RequestBody UpdateEmployeeDTO request
    ) {
        managerService.updateEmployee(id, request);
        return ResponseEntity.ok("Employee updated successfully");
    }

    private User currentUser() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            throw new RuntimeException("Authentication is null");
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof User)) {
            throw new RuntimeException(
                    "Principal is not User, found: " + principal.getClass()
            );
        }

        return (User) principal;
    }


    // USERS
    @GetMapping("/pending-users")
    public List<User> getPendingUsers() {
        return managerService.pendingUsers();
    }

    @PutMapping("/approve/{id}")
    public void approveUser(@PathVariable Long id) {
        managerService.approveUser(id);
    }

    @PutMapping("/reject/{id}")
    public void rejectUser(@PathVariable Long id) {
        managerService.rejectUser(id);
    }

    @PutMapping("/leave/approve/{id}")
    public void approveLeave(@PathVariable Long id) {
        managerService.approveLeave(id);
    }

    @PutMapping("/leave/reject/{id}")
    public void rejectLeave(@PathVariable Long id) {
        managerService.rejectLeave(id);
    }

    @PostMapping("/apply-leave")
    public void applyLeave(@RequestBody Leave leave) {
        managerService.applyLeave(currentUser(), leave);
    }

    @GetMapping("/my-leaves")
    public List<Leave> myLeaves() {
        return managerService.myLeaves(currentUser());
    }

    // TASKS
    @PostMapping("/assign-task")
    public void assignTask(@RequestBody Map<String, String> req) {
        Task t = new Task();
        t.setTitle(req.get("title"));
        t.setDescription(req.get("description"));
        t.setDeadline(LocalDate.parse(req.get("deadline")));
        managerService.assignTask(currentUser(), Long.parseLong(req.get("employeeId")), t);
    }

    @GetMapping("/my-assigned-tasks")
    public List<Task> myAssignedTasks() {
        return managerService.myAssignedTasks(currentUser());
    }

    @GetMapping("/my-tasks")
    public List<Task> myTasks() {
        return managerService.myTasks(currentUser());
    }

    @PutMapping("/accept-task/{id}")
    public void acceptTask(@PathVariable Long id) {
        managerService.acceptTask(id);
    }

    @GetMapping("/in-progress-tasks")
    public List<Task> inProgressTasks() {
        return managerService.inProgressTasks(currentUser());
    }

    // REPORTS
    @PostMapping("/submit-report")
    public void submitReport(@RequestBody Map<String, Object> req) {
        managerService.submitReport(
                currentUser(),
                Long.valueOf(req.get("taskId").toString()),
                req.get("description").toString()
        );
    }

    @GetMapping("/my-reports")
    public List<ReportResponseDTO> myReports() {
        return managerService.myReports(currentUser());
    }
}
