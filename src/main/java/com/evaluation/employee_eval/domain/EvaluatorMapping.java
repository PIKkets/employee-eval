package com.evaluation.employee_eval.domain;

import lombok.Data;

@Data
public class EvaluatorMapping {
    private Long id;
    private Long evaluateeId;
    private Long evaluatorId;
    private String evalType;
    private Boolean isAnonymous;
    
    // Join Data
    private String evaluateeName;
    private String evaluatorName;
    private String evaluateeDeptName;
    private String evaluatorDeptName;
    
    // UI Status Fields
    private String selfEvalStatus; // "DONE" or "PENDING" - only for PERFORMANCE manager view
}
