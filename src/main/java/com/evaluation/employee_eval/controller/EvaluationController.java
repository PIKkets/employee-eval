package com.evaluation.employee_eval.controller;

import com.evaluation.employee_eval.domain.EvalType;
import com.evaluation.employee_eval.domain.EvaluationFormData;
import com.evaluation.employee_eval.domain.EvaluationFormDto;
import com.evaluation.employee_eval.domain.EvaluatorMapping;
import com.evaluation.employee_eval.security.CustomUserDetails;
import com.evaluation.employee_eval.service.EvaluationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

        if (EvalType.INTERVIEW.equals(type)) {
            evaluationService.generateInterviewMappings(user.getEmployee());
        }

        List<EvaluatorMapping> tasks = evaluationService.getMyTasksWithStatus(user.getEmployee().getId(), type);
        model.addAttribute("tasks", tasks);
        model.addAttribute("evalType", type);
        model.addAttribute("evalTypeLower", evalType);
        model.addAttribute("pageTitle", EvalType.TITLE_MAP.getOrDefault(type, "평가 대상"));

        if (EvalType.PEER.equals(type)) {
            model.addAttribute("peerCandidates",
                    evaluationService.getAllEmployeesExcept(user.getEmployee().getId()));
        }

        return "evaluation/list";
    }

    @PostMapping("/peer/add")
    public String addPeerEvaluation(@RequestParam Long evaluateeId,
                                    @AuthenticationPrincipal CustomUserDetails user) {
        evaluationService.addPeerMapping(user.getEmployee().getId(), evaluateeId);
        return "redirect:/evaluation/peer?success_add=true";
    }

    @GetMapping("/{evalType}/{mappingId}")
    public String evaluationForm(@PathVariable String evalType,
                                 @PathVariable Long mappingId,
                                 @AuthenticationPrincipal CustomUserDetails user,
                                 Model model) {
        EvaluationFormData formData = evaluationService.getEvaluationFormData(
                mappingId, evalType, user.getEmployee().getId());

        if (formData == null) {
            return "redirect:/";
        }

        model.addAttribute("mapping",      formData.getMapping());
        model.addAttribute("formDto",      formData.getFormDto());
        model.addAttribute("evalTypeLower", formData.getEvalTypeLower());
        model.addAttribute("isSelf",       formData.isSelf());
        model.addAttribute("isLocked",     formData.isLocked());
        model.addAttribute("historyData",  formData.getHistoryData());

        return "evaluation/form";
    }

    @PostMapping("/submit")
    public String submitForm(@ModelAttribute EvaluationFormDto formDto,
                             @RequestParam("evalTypeLower") String evalTypeLower) {
        evaluationService.submitEvaluation(formDto.getMappingId(), formDto.getScores());
        return "redirect:/evaluation/" + evalTypeLower + "?success";
    }
}
