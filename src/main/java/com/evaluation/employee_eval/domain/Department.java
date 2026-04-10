package com.evaluation.employee_eval.domain;

import lombok.Data;

@Data
public class Department {
    private Long id;
    private String name;
    private Long parentId;
    private Long headId;
    private Boolean isActive;
    
    // For UI Display
    private String parentName;
    private String headName;
}
