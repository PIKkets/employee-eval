package com.evaluation.employee_eval.service;

import com.evaluation.employee_eval.domain.Employee;
import com.evaluation.employee_eval.domain.EvaluationScore;
import com.evaluation.employee_eval.domain.EvaluatorMapping;
import com.evaluation.employee_eval.mapper.EmployeeMapper;
import com.evaluation.employee_eval.mapper.EvaluationScoreMapper;
import com.evaluation.employee_eval.mapper.EvaluatorMappingMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GradeService {

    private final EmployeeMapper employeeMapper;
    private final EvaluatorMappingMapper mappingMapper;
    private final EvaluationScoreMapper scoreMapper;

    @Transactional
    public void calculateFinalGrades() {
        List<Employee> allEmployees = employeeMapper.findAll();
        List<EvaluatorMapping> allMappings = mappingMapper.findAll();

        for (Employee emp : allEmployees) {
            // Skip ADMINs from getting grades
            if ("ADMIN".equals(emp.getRole())) continue;

            List<EvaluatorMapping> myEvaluations = allMappings.stream()
                .filter(m -> m.getEvaluateeId().equals(emp.getId()))
                .collect(Collectors.toList());

            double totalScore = calculateTotalScore(myEvaluations);
            String finalGrade = getGradeFromScore(totalScore);

            emp.setTotalScore(totalScore);
            emp.setFinalGrade(finalGrade);
            employeeMapper.updateGrade(emp);
        }
    }

    private double calculateTotalScore(List<EvaluatorMapping> mappings) {
        double perfTotal = 0; int perfCount = 0;
        double compTotal = 0; int compCount = 0;
        double peerTotal = 0; int peerCount = 0;
        double interviewTotal = 0; int interviewCount = 0;

        for (EvaluatorMapping mapping : mappings) {
            List<EvaluationScore> scores = scoreMapper.findByMappingId(mapping.getId());
            double currentEvalScore = calculateEvalScore(scores);
            
            switch (mapping.getEvalType()) {
                case "PERFORMANCE": perfTotal += currentEvalScore; perfCount++; break;
                case "COMPETENCY": compTotal += currentEvalScore; compCount++; break;
                case "PEER": peerTotal += currentEvalScore; peerCount++; break;
                case "INTERVIEW": interviewTotal += currentEvalScore; interviewCount++; break;
            }
        }

        double finalScore = 0.0;
        double totalWeight = 0;

        if (perfCount > 0) { finalScore += (perfTotal / perfCount) * 0.40; totalWeight += 0.40; }
        if (compCount > 0) { finalScore += (compTotal / compCount) * 0.30; totalWeight += 0.30; }
        if (peerCount > 0) { finalScore += (peerTotal / peerCount) * 0.15; totalWeight += 0.15; }
        if (interviewCount > 0) { finalScore += (interviewTotal / interviewCount) * 0.15; totalWeight += 0.15; }
        
        if (totalWeight > 0) {
            finalScore = finalScore / totalWeight;
        }

        return Math.round(finalScore * 100.0) / 100.0;
    }

    private double calculateEvalScore(List<EvaluationScore> scores) {
        if (scores == null || scores.isEmpty()) return 0;
        double sum = 0;
        for (EvaluationScore score : scores) {
            sum += score.getScore() * (score.getElementWeight() / 100.0);
        }
        return sum;
    }

    private String getGradeFromScore(double score) {
        if (score == 0) return "-";
        if (score >= 95) return "S";
        if (score >= 85) return "A";
        if (score >= 75) return "B";
        if (score >= 60) return "C";
        return "D";
    }
}
