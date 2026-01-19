package com.bk.ems3.dto;

import com.bk.ems3.model.UserStatus;
import lombok.Data;

@Data
public class UpdateEmployeeDTO {
    private String email;
    private String fullName;
    private String department;
    private String designation;
    private UserStatus status;
}
