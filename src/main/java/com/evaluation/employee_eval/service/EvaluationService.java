package com.evaluation.employee_eval.service;

import com.evaluation.employee_eval.domain.*;
import com.evaluation.employee_eval.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EvaluationService {

    private final EvaluatorMappingMapper mappingMapper;
    private final EvaluationElementMapper elementMapper;
    private final EvaluationScoreMapper scoreMapper;
    private final EmployeeMapper employeeMapper;

    // -------------------------------------------------------------------------
    // 평가 목록 조회
    // -------------------------------------------------------------------------

    public List<EvaluatorMapping> getMyTasksWithStatus(Long evaluatorId, String evalType) {
        List<EvaluatorMapping> tasks = mappingMapper.findByEvaluatorAndType(evaluatorId, evalType);

        if (EvalType.isTwoStep(evalType)) {
            tasks.forEach(task -> {
                boolean isSelf = task.getEvaluateeId().equals(task.getEvaluatorId());
                if (!isSelf) {
                    boolean selfDone = isSelfEvaluationDone(task.getEvaluateeId(), evalType);
                    task.setSelfEvalStatus(selfDone ? "DONE" : "PENDING");
                }
            });
        }
        return tasks;
    }

    public List<Employee> getAllEmployeesExcept(Long excludeId) {
        return employeeMapper.findAll().stream()
                .filter(emp -> !emp.getId().equals(excludeId))
                .collect(Collectors.toList());
    }

    // -------------------------------------------------------------------------
    // 매핑 관리
    // -------------------------------------------------------------------------

    @Transactional
    public void addPeerMapping(Long evaluatorId, Long evaluateeId) {
        if (mappingMapper.findByEvaluateeAndEvaluatorAndType(evaluateeId, evaluatorId, EvalType.PEER) == null) {
            mappingMapper.insert(buildMapping(evaluatorId, evaluateeId, EvalType.PEER));
        }
    }

    @Transactional
    public void generateInterviewMappings(Employee currentUser) {
        if (Boolean.TRUE.equals(currentUser.getIsLeader())) {
            // 팀장: 같은 부서의 팀원 전체를 평가 대상으로 등록
            employeeMapper.findAll().stream()
                    .filter(e -> currentUser.getDepartmentId().equals(e.getDepartmentId())
                            && !e.getId().equals(currentUser.getId()))
                    .forEach(member -> addInterviewMappingIfAbsent(currentUser.getId(), member.getId()));
        } else {
            // 팀원: 자신의 팀장을 평가 대상으로 등록
            employeeMapper.findAll().stream()
                    .filter(e -> currentUser.getDepartmentId().equals(e.getDepartmentId())
                            && Boolean.TRUE.equals(e.getIsLeader()))
                    .findFirst()
                    .ifPresent(leader -> addInterviewMappingIfAbsent(currentUser.getId(), leader.getId()));
        }
    }

    // -------------------------------------------------------------------------
    // 평가 폼 데이터 조회
    // -------------------------------------------------------------------------

    public EvaluationFormData getEvaluationFormData(Long mappingId, String evalType, Long currentUserId) {
        EvaluatorMapping mapping = mappingMapper.findById(mappingId);
        if (mapping == null || !mapping.getEvaluatorId().equals(currentUserId)) {
            return null; // 권한 없음 또는 존재하지 않음
        }

        boolean isSelf   = mapping.getEvaluatorId().equals(mapping.getEvaluateeId());
        boolean isTwoStep = EvalType.isTwoStep(evalType);
        boolean isLocked = false;
        List<EvaluationScore> selfScores = null;

        if (isTwoStep && !isSelf) {
            selfScores = getSelfScores(mapping.getEvaluateeId(), evalType.toUpperCase());
            isLocked = selfScores == null || selfScores.isEmpty();
        }

        EvaluationFormDto formDto = buildFormDto(mappingId, evalType, isSelf, isLocked, selfScores);

        EvaluationFormData formData = new EvaluationFormData();
        formData.setMapping(mapping);
        formData.setFormDto(formDto);
        formData.setSelf(isSelf);
        formData.setLocked(isLocked);
        formData.setEvalTypeLower(evalType.toLowerCase());
        formData.setHistoryData(buildHistoryData(evalType, mapping));
        return formData;
    }

    // -------------------------------------------------------------------------
    // 평가 제출
    // -------------------------------------------------------------------------

    @Transactional
    public void submitEvaluation(Long mappingId, List<EvaluationScore> scores) {
        scoreMapper.deleteByMappingId(mappingId);
        scores.forEach(score -> {
            score.setMappingId(mappingId);
            scoreMapper.insert(score);
        });
    }

    // -------------------------------------------------------------------------
    // private 헬퍼
    // -------------------------------------------------------------------------

    private boolean isSelfEvaluationDone(Long evaluateeId, String evalType) {
        List<EvaluationScore> scores = getSelfScores(evaluateeId, evalType);
        return scores != null && !scores.isEmpty();
    }

    private List<EvaluationScore> getSelfScores(Long evaluateeId, String evalType) {
        EvaluatorMapping selfMapping = mappingMapper.findByEvaluateeAndEvaluatorAndType(
                evaluateeId, evaluateeId, evalType.toUpperCase());
        return selfMapping == null ? null : scoreMapper.findByMappingId(selfMapping.getId());
    }

    private void addInterviewMappingIfAbsent(Long evaluatorId, Long evaluateeId) {
        if (mappingMapper.findByEvaluateeAndEvaluatorAndType(evaluateeId, evaluatorId, EvalType.INTERVIEW) == null) {
            mappingMapper.insert(buildMapping(evaluatorId, evaluateeId, EvalType.INTERVIEW));
        }
    }

    private EvaluatorMapping buildMapping(Long evaluatorId, Long evaluateeId, String evalType) {
        EvaluatorMapping mapping = new EvaluatorMapping();
        mapping.setEvaluatorId(evaluatorId);
        mapping.setEvaluateeId(evaluateeId);
        mapping.setEvalType(evalType);
        return mapping;
    }

    private EvaluationFormDto buildFormDto(Long mappingId, String evalType,
                                           boolean isSelf, boolean isLocked,
                                           List<EvaluationScore> selfScores) {
        List<EvaluationElement> elements = elementMapper.findByType(evalType.toUpperCase());
        List<EvaluationScore> existingScores = scoreMapper.findByMappingId(mappingId);

        List<EvaluationScore> scores = elements.stream()
                .map(element -> toScoreRow(element, existingScores, isSelf, isLocked, selfScores))
                .collect(Collectors.toList());

        EvaluationFormDto formDto = new EvaluationFormDto();
        formDto.setMappingId(mappingId);
        formDto.setScores(scores);
        return formDto;
    }

    private EvaluationScore toScoreRow(EvaluationElement element,
                                       List<EvaluationScore> existingScores,
                                       boolean isSelf, boolean isLocked,
                                       List<EvaluationScore> selfScores) {
        EvaluationScore score = new EvaluationScore();
        score.setElementId(element.getId());
        score.setElementName(element.getName());
        score.setElementWeight(element.getWeight());

        // 기존 점수 복원
        existingScores.stream()
                .filter(s -> s.getElementId().equals(element.getId()))
                .findFirst()
                .ifPresent(s -> {
                    score.setScore(s.getScore());
                    score.setComment(s.getComment());
                });

        // 점수 미입력 시 기본값
        if (score.getScore() == null) {
            score.setScore(isSelf ? 0 : null);
        }

        // 관리자 뷰: 자가 평가 코멘트 첨부
        boolean isManager = !isSelf;
        if (isManager && !isLocked && selfScores != null) {
            selfScores.stream()
                    .filter(s -> s.getElementId().equals(element.getId()))
                    .findFirst()
                    .ifPresent(s -> score.setSelfComment(s.getComment()));
        }

        return score;
    }

    private Map<String, List<EvaluationScore>> buildHistoryData(String evalType, EvaluatorMapping mapping) {
        if (!EvalType.INTERVIEW.equalsIgnoreCase(evalType)) {
            return new HashMap<>();
        }

        Map<String, List<EvaluationScore>> historyData = new HashMap<>();
        Long evaluateeId = mapping.getEvaluateeId();

        mappingMapper.findAll().stream()
                .filter(m -> m.getEvaluateeId().equals(evaluateeId)
                        && !EvalType.INTERVIEW.equalsIgnoreCase(m.getEvalType()))
                .forEach(hm -> {
                    List<EvaluationScore> hmScores = scoreMapper.findByMappingId(hm.getId());
                    if (!hmScores.isEmpty()) {
                        String prefix = resolveHistoryPrefix(hm);
                        hmScores.forEach(s -> s.setElementName(prefix + s.getElementName()));
                        historyData.computeIfAbsent(hm.getEvalType(), k -> new ArrayList<>())
                                .addAll(hmScores);
                    }
                });

        return historyData;
    }

    private String resolveHistoryPrefix(EvaluatorMapping hm) {
        if (hm.getEvaluatorId().equals(hm.getEvaluateeId())) return "[본인] ";
        if (EvalType.PEER.equals(hm.getEvalType()))          return "[동료] ";
        return "[팀장] ";
    }
}
