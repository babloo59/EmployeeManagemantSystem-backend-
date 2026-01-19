package com.bk.ems3.dto;

import com.bk.ems3.model.ReportStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class ReportResponseDTO {

    private Long id;
    private String taskTitle;
    private String description;
    private ReportStatus status;
}
