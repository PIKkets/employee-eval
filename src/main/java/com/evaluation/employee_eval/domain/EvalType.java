package com.evaluation.employee_eval.domain;

/**
 * 평가 유형을 나타내는 상수 클래스.
 * 문자열 리터럴 대신 이 상수를 사용하여 오타를 방지합니다.
 */
public final class EvalType {

    public static final String PERFORMANCE = "PERFORMANCE";
    public static final String COMPETENCY  = "COMPETENCY";
    public static final String PEER        = "PEER";
    public static final String INTERVIEW   = "INTERVIEW";

    /** 평가 유형 → 화면 제목 매핑 */
    public static final java.util.Map<String, String> TITLE_MAP =
            java.util.Map.of(
                    PERFORMANCE, "성과 평가",
                    COMPETENCY,  "역량 평가",
                    PEER,        "다면 평가",
                    INTERVIEW,   "면담 평가"
            );

    /** 2단계(자가→관리자) 평가 유형 여부 */
    public static boolean isTwoStep(String type) {
        return PERFORMANCE.equalsIgnoreCase(type) || COMPETENCY.equalsIgnoreCase(type);
    }

    private EvalType() {}
}
