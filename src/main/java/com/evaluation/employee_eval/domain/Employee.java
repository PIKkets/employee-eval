package com.evaluation.employee_eval.domain;

import lombok.Data;

@Data
public class Employee {
    private Long id;
    private Long departmentId;
    private String loginId;
    private String password;
    private String name;
    private String email;
    private String position;
    private Boolean isLeader;
    private String role;
    private Double totalScore;
    private String finalGrade;
    
    // Virtual mapping Data
    private String departmentName;
}
