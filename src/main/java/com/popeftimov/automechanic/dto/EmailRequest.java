package com.popeftimov.automechanic.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;


@AllArgsConstructor
@Getter
@Setter
public class EmailRequest {
    private String to;
    private String subject;
    private String templateName;
    private Map<String,Object> contextValues;
}
