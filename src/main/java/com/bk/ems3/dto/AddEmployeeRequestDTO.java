package com.bk.ems3.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class AddEmployeeRequestDTO {
    private String fullName;
    private String email;
    private String department;
    private String designation;
}
