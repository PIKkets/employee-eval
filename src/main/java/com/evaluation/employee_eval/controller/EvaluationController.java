package com.evaluation.employee_eval.controller;

import com.evaluation.employee_eval.domain.*;
import com.evaluation.employee_eval.security.CustomUserDetails;
import com.evaluation.employee_eval.service.EvaluationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/evaluation")
@RequiredArgsConstructor
public class EvaluationController {

    private final EvaluationService evaluationService;

    @GetMapping("/{evalType}")
    public String evaluationList(@PathVariable String evalType, 
                                 @AuthenticationPrincipal CustomUserDetails user, 
                                 Model model) {
        String type = evalType.toUpperCase();
        List<EvaluatorMapping> tasks = evaluationService.getMyTasks(user.getEmployee().getId(), type);
        
        // For 2-step evaluations: populate selfEvalStatus for manager tasks
        boolean isTwoStepList = "PERFORMANCE".equals(type) || "COMPETENCY".equals(type);
        if (isTwoStepList) {
            tasks.forEach(task -> {
                boolean isSelf = task.getEvaluateeId().equals(task.getEvaluatorId());
                if (!isSelf) {
                    // Manager task: check if evaluatee has submitted self-evaluation
                    List<EvaluationScore> selfScores = evaluationService.getSelfExistingScores(task.getEvaluateeId(), type);
                    task.setSelfEvalStatus((selfScores != null && !selfScores.isEmpty()) ? "DONE" : "PENDING");
                }
            });
        }
        
        model.addAttribute("tasks", tasks);
        model.addAttribute("evalType", type);
        model.addAttribute("evalTypeLower", evalType);
        
        String title = "";
        switch(type) {
            case "PERFORMANCE": title = "성과 평가"; break;
            case "COMPETENCY": title = "역량 평가"; break;
            case "PEER": title = "다면 평가"; break;
            case "INTERVIEW": title = "면담 평가"; break;
            default: title = "평가 대상";
        }
        model.addAttribute("pageTitle", title);
        
        return "evaluation/list";
    }

    @GetMapping("/{evalType}/{mappingId}")
    public String evaluationForm(@PathVariable String evalType, 
                                 @PathVariable Long mappingId, 
                                 @AuthenticationPrincipal CustomUserDetails user, 
                                 Model model) {
        EvaluatorMapping mapping = evaluationService.getMapping(mappingId);
        if(mapping == null || !mapping.getEvaluatorId().equals(user.getEmployee().getId())) {
            return "redirect:/";
        }

        List<EvaluationElement> elements = evaluationService.getElementsByType(evalType.toUpperCase());
        List<EvaluationScore> existingScores = evaluationService.getExistingScores(mappingId);
        
        boolean isSelf = mapping.getEvaluatorId().equals(mapping.getEvaluateeId());
        boolean isManager = !isSelf;
        boolean isLocked = false;
        
        boolean isTwoStep = "PERFORMANCE".equalsIgnoreCase(evalType) || "COMPETENCY".equalsIgnoreCase(evalType);
        
        if (isTwoStep && isManager) {
            List<EvaluationScore> selfScores = evaluationService.getSelfExistingScores(mapping.getEvaluateeId(), evalType.toUpperCase());
            if (selfScores == null || selfScores.isEmpty()) {
                isLocked = true;
            } else {
                model.addAttribute("selfScores", selfScores);
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
                
            if (isManager && isTwoStep && !isLocked) {
                List<EvaluationScore> selfScores = (List<EvaluationScore>) model.getAttribute("selfScores");
                if (selfScores != null) {
                    selfScores.stream()
                        .filter(s -> s.getElementId().equals(element.getId()))
                        .findFirst()
                        .ifPresent(s -> score.setSelfComment(s.getComment()));
                }
            }
                
            scores.add(score);
        }
        formDto.setScores(scores);
        
        model.addAttribute("mapping", mapping);
        model.addAttribute("formDto", formDto);
        model.addAttribute("evalTypeLower", evalType);
        model.addAttribute("isSelf", isSelf);
        model.addAttribute("isLocked", isLocked);
        
        return "evaluation/form";
    }

    @PostMapping("/submit")
    public String submitForm(@ModelAttribute EvaluationFormDto formDto, @RequestParam("evalTypeLower") String evalTypeLower) {
        evaluationService.submitEvaluation(formDto.getMappingId(), formDto.getScores());
        return "redirect:/evaluation/" + evalTypeLower + "?success";
    }
}
