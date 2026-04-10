package com.evaluation.employee_eval.controller;

import com.evaluation.employee_eval.service.AdminService;
import com.evaluation.employee_eval.service.GradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/grades")
@RequiredArgsConstructor
public class GradeController {

    private final AdminService adminService;
    private final GradeService gradeService;

    @GetMapping
    public String gradesList(Model model) {
        model.addAttribute("employees", adminService.getEvaluatableEmployees());
        return "admin/grades";
    }

    @PostMapping("/calculate")
    public String calculateGrades() {
        gradeService.calculateFinalGrades();
        return "redirect:/admin/grades?success";
    }
}
