package com.evaluation.employee_eval.controller;

import com.evaluation.employee_eval.domain.Employee;
import com.evaluation.employee_eval.service.AdminService;
import com.evaluation.employee_eval.service.GradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/grades")
@RequiredArgsConstructor
public class GradeController {

    private final AdminService adminService;
    private final GradeService gradeService;

    @GetMapping
    public String gradesList(Model model) {
        // Exclude ADMIN users from the list as they don't get evaluated
        model.addAttribute("employees", adminService.getAllEmployees().stream()
                .filter(e -> !"ADMIN".equals(e.getRole()))
                .collect(Collectors.toList()));
        return "admin/grades";
    }

    @PostMapping("/calculate")
    public String calculateGrades() {
        gradeService.calculateFinalGrades();
        return "redirect:/admin/grades?success";
    }
}
