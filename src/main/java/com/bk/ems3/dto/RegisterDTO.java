package com.bk.ems3.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterDTO {
    private String fullName;
    private String email;
    private String password;
}
