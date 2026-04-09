package com.evaluation.employee_eval.service;

import com.evaluation.employee_eval.domain.EvaluationElement;
import com.evaluation.employee_eval.domain.EvaluationScore;
import com.evaluation.employee_eval.domain.EvaluatorMapping;
import com.evaluation.employee_eval.mapper.EvaluationElementMapper;
import com.evaluation.employee_eval.mapper.EvaluationScoreMapper;
import com.evaluation.employee_eval.mapper.EmployeeMapper;
import com.evaluation.employee_eval.mapper.EvaluatorMappingMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.evaluation.employee_eval.domain.EvaluationFormData;
import com.evaluation.employee_eval.domain.EvaluationFormDto;
import com.evaluation.employee_eval.domain.Employee;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EvaluationService {

    private final EvaluatorMappingMapper mappingMapper;
    private final EvaluationElementMapper elementMapper;
    private final EvaluationScoreMapper scoreMapper;
    private final EmployeeMapper employeeMapper;

    public List<EvaluatorMapping> getMyTasks(Long evaluatorId, String evalType) {
        return mappingMapper.findByEvaluatorAndType(evaluatorId, evalType);
    }

    public List<EvaluatorMapping> getMyTasksWithStatus(Long evaluatorId, String evalType) {
        List<EvaluatorMapping> tasks = getMyTasks(evaluatorId, evalType);
        boolean isTwoStepList = "PERFORMANCE".equals(evalType) || "COMPETENCY".equals(evalType);
        
        if (isTwoStepList) {
            tasks.forEach(task -> {
                boolean isSelf = task.getEvaluateeId().equals(task.getEvaluatorId());
                if (!isSelf) {
                    List<EvaluationScore> selfScores = getSelfExistingScores(task.getEvaluateeId(), evalType);
                    task.setSelfEvalStatus((selfScores != null && !selfScores.isEmpty()) ? "DONE" : "PENDING");
                }
            });
        }
        return tasks;
    }
    
    public List<Employee> getAllEmployeesExcept(Long currentId) {
        return employeeMapper.findAll().stream()
                .filter(emp -> !emp.getId().equals(currentId))
                .collect(Collectors.toList());
    }

    @Transactional
    public void addPeerMapping(Long evaluatorId, Long evaluateeId) {
        // Prevent duplicate mapping
        EvaluatorMapping existing = mappingMapper.findByEvaluateeAndEvaluatorAndType(evaluateeId, evaluatorId, "PEER");
        if (existing == null) {
            EvaluatorMapping mapping = new EvaluatorMapping();
            mapping.setEvaluatorId(evaluatorId);
            mapping.setEvaluateeId(evaluateeId);
            mapping.setEvalType("PEER");
            mappingMapper.insert(mapping);
        }
    }

    @Transactional
    public void generateInterviewMappings(Employee currentUser) {
        if (Boolean.TRUE.equals(currentUser.getIsLeader())) {
            // Leader evaluates all team members
            List<Employee> teamMembers = employeeMapper.findAll().stream()
                    .filter(e -> currentUser.getDepartmentId().equals(e.getDepartmentId()) && !e.getId().equals(currentUser.getId()))
                    .collect(Collectors.toList());
            for (Employee member : teamMembers) {
                addInterviewMapping(currentUser.getId(), member.getId());
            }
        } else {
            // Member evaluates their leader
            Employee leader = employeeMapper.findAll().stream()
                    .filter(e -> currentUser.getDepartmentId().equals(e.getDepartmentId()) && Boolean.TRUE.equals(e.getIsLeader()))
                    .findFirst().orElse(null);
            if (leader != null) {
                addInterviewMapping(currentUser.getId(), leader.getId());
            }
        }
    }

    private void addInterviewMapping(Long evaluatorId, Long evaluateeId) {
        EvaluatorMapping existing = mappingMapper.findByEvaluateeAndEvaluatorAndType(evaluateeId, evaluatorId, "INTERVIEW");
        if (existing == null) {
            EvaluatorMapping mapping = new EvaluatorMapping();
            mapping.setEvaluatorId(evaluatorId);
            mapping.setEvaluateeId(evaluateeId);
            mapping.setEvalType("INTERVIEW");
            mappingMapper.insert(mapping);
        }
    }
    
    public EvaluatorMapping getMapping(Long mappingId) {
        return mappingMapper.findById(mappingId);
    }

    public List<EvaluationElement> getElementsByType(String evalType) {
        return elementMapper.findByType(evalType);
    }
    
    public List<EvaluationScore> getExistingScores(Long mappingId) {
        return scoreMapper.findByMappingId(mappingId);
    }
    public List<EvaluationScore> getSelfExistingScores(Long evaluateeId, String evalType) {
        EvaluatorMapping selfMapping = mappingMapper.findByEvaluateeAndEvaluatorAndType(evaluateeId, evaluateeId, evalType);
        if (selfMapping == null) return null;
        return scoreMapper.findByMappingId(selfMapping.getId());
    }

    @Transactional
    public EvaluatorMapping getOrCreateSelfMapping(Long evaluateeId, String evalType) {
        EvaluatorMapping selfMapping = mappingMapper.findByEvaluateeAndEvaluatorAndType(evaluateeId, evaluateeId, evalType);
        if (selfMapping == null) {
            selfMapping = new EvaluatorMapping();
            selfMapping.setEvaluateeId(evaluateeId);
            selfMapping.setEvaluatorId(evaluateeId);
            selfMapping.setEvalType(evalType);
            mappingMapper.insert(selfMapping);
        }
        return selfMapping;
    }

    @Transactional
    public void submitEvaluation(Long mappingId, List<EvaluationScore> scores) {
        // Clear previous scores
        scoreMapper.deleteByMappingId(mappingId);
        // Insert new scores
        for (EvaluationScore score : scores) {
            score.setMappingId(mappingId);
            scoreMapper.insert(score);
        }
    }

    public EvaluationFormData getEvaluationFormData(Long mappingId, String evalType, Long currentUserId) {
        EvaluatorMapping mapping = getMapping(mappingId);
        if (mapping == null || !mapping.getEvaluatorId().equals(currentUserId)) {
            return null; // Signals unauthorized or not found
        }

        List<EvaluationElement> elements = getElementsByType(evalType.toUpperCase());
        List<EvaluationScore> existingScores = getExistingScores(mappingId);
        
        boolean isSelf = mapping.getEvaluatorId().equals(mapping.getEvaluateeId());
        boolean isManager = !isSelf;
        boolean isLocked = false;
        
        boolean isTwoStep = "PERFORMANCE".equalsIgnoreCase(evalType) || "COMPETENCY".equalsIgnoreCase(evalType);
        List<EvaluationScore> selfScores = null;
        
        if (isTwoStep && isManager) {
            selfScores = getSelfExistingScores(mapping.getEvaluateeId(), evalType.toUpperCase());
            if (selfScores == null || selfScores.isEmpty()) {
                isLocked = true;
            }
        }
        
        EvaluationFormDto formDto = new EvaluationFormDto();
        formDto.setMappingId(mappingId);
        List<EvaluationScore> scores = new ArrayList<>();
        
        for (EvaluationElement element : elements) {
            EvaluationScore score = new EvaluationScore();
            score.setElementId(element.getId());
            score.setElementName(element.getName());
            score.setElementWeight(element.getWeight());
            
            existingScores.stream()
                .filter(s -> s.getElementId().equals(element.getId()))
                .findFirst()
                .ifPresent(s -> {
                    score.setScore(s.getScore());
                    score.setComment(s.getComment());
                });
            
            if (score.getScore() == null) {
                score.setScore(isSelf ? 0 : null);
            }
                
            if (isManager && isTwoStep && !isLocked && selfScores != null) {
                selfScores.stream()
                    .filter(s -> s.getElementId().equals(element.getId()))
                    .findFirst()
                    .ifPresent(s -> score.setSelfComment(s.getComment()));
            }
                
            scores.add(score);
        }
        formDto.setScores(scores);
        
        java.util.Map<String, List<EvaluationScore>> historyData = new java.util.HashMap<>();
        if ("INTERVIEW".equalsIgnoreCase(evalType)) {
            Long evaluateeId = mapping.getEvaluateeId();
            List<EvaluatorMapping> historyMappings = mappingMapper.findAll().stream()
                .filter(m -> m.getEvaluateeId().equals(evaluateeId) && !"INTERVIEW".equalsIgnoreCase(m.getEvalType()))
                .collect(Collectors.toList());
            
            for (EvaluatorMapping hm : historyMappings) {
                List<EvaluationScore> hmScores = scoreMapper.findByMappingId(hm.getId());
                if (!hmScores.isEmpty()) {
                    boolean isSelfHistory = hm.getEvaluatorId().equals(hm.getEvaluateeId());
                    for (EvaluationScore s : hmScores) {
                        String prefix = isSelfHistory ? "[본인] " : (hm.getEvalType().equals("PEER") ? "[동료] " : "[팀장] ");
                        s.setElementName(prefix + s.getElementName());
                    }
                    historyData.computeIfAbsent(hm.getEvalType(), k -> new ArrayList<>()).addAll(hmScores);
                }
            }
        }
        
        EvaluationFormData formData = new EvaluationFormData();
        formData.setMapping(mapping);
        formData.setFormDto(formDto);
        formData.setSelf(isSelf);
        formData.setManager(isManager);
        formData.setLocked(isLocked);
        formData.setEvalTypeLower(evalType.toLowerCase());
        formData.setHistoryData(historyData);
        
        return formData;
    }
}
