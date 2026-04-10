package com.evaluation.employee_eval.domain;

import lombok.Data;
import java.util.List;
import java.util.Map;

/**
 * 평가 폼 렌더링에 필요한 모든 데이터를 담는 VO.
 * isSelf == true  → 자가 평가
 * isSelf == false → 관리자/동료/면담 평가
 */
@Data
public class EvaluationFormData {
    private EvaluatorMapping mapping;
    private EvaluationFormDto formDto;
    private boolean isSelf;
    private boolean isLocked;
    private String evalTypeLower;
    /** 면담 평가 시 피평가자의 이전 평가 이력 (evalType → 점수 목록) */
    private Map<String, List<EvaluationScore>> historyData;
}
