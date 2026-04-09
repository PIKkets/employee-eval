package com.evaluation.employee_eval.domain;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class EvaluationFormData {
    private EvaluatorMapping mapping;
    private EvaluationFormDto formDto;
    private boolean isSelf;
    private boolean isManager;
    private boolean isLocked;
    private String evalTypeLower;
    private Map<String, List<EvaluationScore>> historyData;
}
