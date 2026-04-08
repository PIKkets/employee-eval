package com.evaluation.employee_eval.controller;

import com.evaluation.employee_eval.domain.Employee;
import com.evaluation.employee_eval.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class IndexController {

    private final AdminService adminService;

    @GetMapping("/")
    public String index(Model model) {
        List<Employee> allEmployees = adminService.getAllEmployees().stream()
                .filter(e -> !"ADMIN".equals(e.getRole()))
                .collect(Collectors.toList());

        long totalCount = allEmployees.size();
        long evaluatedCount = allEmployees.stream().filter(e -> e.getTotalScore() != null).count();

        // Grade Distribution
        Map<String, Long> gradeDist = allEmployees.stream()
                .filter(e -> e.getFinalGrade() != null && !"-".equals(e.getFinalGrade()))
                .collect(Collectors.groupingBy(Employee::getFinalGrade, Collectors.counting()));

        // Dept Averages
        Map<String, Double> deptAverages = allEmployees.stream()
                .filter(e -> e.getTotalScore() != null && e.getDepartmentName() != null)
                .collect(Collectors.groupingBy(
                        Employee::getDepartmentName,
                        Collectors.averagingDouble(Employee::getTotalScore)
                ));

        // Top 5 Talents
        List<Employee> topTalents = allEmployees.stream()
                .filter(e -> e.getTotalScore() != null)
                .sorted((e1, e2) -> Double.compare(e2.getTotalScore(), e1.getTotalScore()))
                .limit(5)
                .collect(Collectors.toList());

        model.addAttribute("totalCount", totalCount);
        model.addAttribute("evaluatedCount", evaluatedCount);
        model.addAttribute("gradeDist", gradeDist);
        model.addAttribute("deptAverages", deptAverages);
        model.addAttribute("topTalents", topTalents);

        return "index";
    }
}
