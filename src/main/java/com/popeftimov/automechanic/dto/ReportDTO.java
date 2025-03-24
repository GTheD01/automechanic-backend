package com.popeftimov.automechanic.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportDTO {

    private Long id;
    private String description;
    private String answer;
    private String reportType;

    @JsonFormat(pattern = "HH:mm / dd.MM.yyyy")
    private LocalDateTime createdAt;

    private UserResponse user;

}
