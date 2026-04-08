package com.evaluation.employee_eval.domain;

import lombok.Data;

@Data
public class EvaluationScore {
    private Long id;
    private Long mappingId;
    private Long elementId;
    private Integer score;
    private String comment;
    
    // Fields for UI rendering
    private String elementName;
    private Integer elementWeight;
    private String selfComment; // Stores the self evaluation comment for manager view
}
