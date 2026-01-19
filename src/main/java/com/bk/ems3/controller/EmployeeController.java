package com.bk.ems3.controller;

import com.bk.ems3.dto.ReportResponseDTO;
import com.bk.ems3.dto.SubmitReportDTO;
import com.bk.ems3.model.Leave;
import com.bk.ems3.model.Report;
import com.bk.ems3.model.Task;
import com.bk.ems3.model.User;
import com.bk.ems3.repository.LeaveRepository;
import com.bk.ems3.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/employee")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping("/my-leaves")
    public List<Leave> myLeaves(Authentication authentication){
        User user = (User) authentication.getPrincipal();
        System.out.println("Principal name = " + user.getEmail());
        return employeeService.getMyLeaves(user);
    }

    @PostMapping("/apply-leave")
    public ResponseEntity<?> applyLeave(
            @RequestBody Leave leave,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        employeeService.applyLeave(user.getEmail(), leave);
        return ResponseEntity.ok("Leave applied successfully");
    }

    @GetMapping("/my-tasks")
    public List<Task> myTasks(Authentication authentication){
        User user = (User) authentication.getPrincipal();
        return employeeService.getMyTasks(user.getEmail());
    }

    @PutMapping("/accept-task/{id}")
    public ResponseEntity<?> acceptTask(
            @PathVariable Long id,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        employeeService.acceptTask(id, user.getEmail());
        return ResponseEntity.ok("Task accepted");
    }

    @GetMapping("/my-reports")
    public List<ReportResponseDTO> myReport(Authentication authentication){
        User user = (User) authentication.getPrincipal();
        return employeeService.getMyReport(user.getEmail());
    }

    @GetMapping("/in-progress-tasks")
    public List<Task> inProgressReport(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return employeeService.getInProgressReport(user.getEmail());
    }

    @PostMapping("/submit-report")
    public ResponseEntity<?> submitReport(
            @RequestBody SubmitReportDTO request,
            Authentication authentication
    ) {
        System.out.println("🔍 Task ID: " + request.getTaskId());
        System.out.println("🔍 Description: " + request.getDescription());

        User user = (User) authentication.getPrincipal();

        employeeService.submitReport(user.getEmail(), request);
        return ResponseEntity.ok("Report submitted successfully");
    }
}
