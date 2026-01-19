package com.bk.ems3.dto;

import com.bk.ems3.model.Role;
import lombok.Data;

@Data
public class AddEmployeeDTO {

    private String email;
    private String fullName;
    private String password;
    private String department;
    private String designation;
    private Role role;
}
