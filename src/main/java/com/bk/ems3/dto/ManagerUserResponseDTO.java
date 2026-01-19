package com.bk.ems3.dto;

import com.bk.ems3.model.Role;
import com.bk.ems3.model.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ManagerUserResponseDTO {
    private Long id;
    private String fullName;
    private String email;
    private Role role;
    private UserStatus status;
}
