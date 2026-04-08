package com.evaluation.employee_eval.domain;

import lombok.Data;

@Data
public class EvaluationElement {
    private Long id;
    private String evalType;
    private String name;
    private Integer weight;
}
