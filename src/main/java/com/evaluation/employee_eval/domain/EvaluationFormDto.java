package com.evaluation.employee_eval.domain;

import lombok.Data;
import java.util.List;
import java.util.ArrayList;

@Data
public class EvaluationFormDto {
    private Long mappingId;
    private List<EvaluationScore> scores = new ArrayList<>();
}
