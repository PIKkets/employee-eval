package com.evaluation.employee_eval.service;

import com.evaluation.employee_eval.domain.EvaluationElement;
import com.evaluation.employee_eval.domain.EvaluationScore;
import com.evaluation.employee_eval.domain.EvaluatorMapping;
import com.evaluation.employee_eval.mapper.EvaluationElementMapper;
import com.evaluation.employee_eval.mapper.EvaluationScoreMapper;
import com.evaluation.employee_eval.mapper.EvaluatorMappingMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EvaluationService {

    private final EvaluatorMappingMapper mappingMapper;
    private final EvaluationElementMapper elementMapper;
    private final EvaluationScoreMapper scoreMapper;

    public List<EvaluatorMapping> getMyTasks(Long evaluatorId, String evalType) {
        return mappingMapper.findByEvaluatorAndType(evaluatorId, evalType);
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
}
