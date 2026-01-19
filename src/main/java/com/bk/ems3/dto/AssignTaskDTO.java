package com.bk.ems3.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class AssignTaskDTO {
    private Long employeeId;
    private String title;
    private String description;
    private LocalDate deadline;
    private String role;
}
