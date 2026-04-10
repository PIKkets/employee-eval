package com.evaluation.employee_eval.controller;

import com.evaluation.employee_eval.domain.Employee;
import com.evaluation.employee_eval.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class IndexController {

    private final AdminService adminService;

    @GetMapping("/")
    public String index(Model model) {
        List<Employee> employees = adminService.getEvaluatableEmployees();

        long totalCount     = employees.size();
        long evaluatedCount = employees.stream().filter(e -> e.getTotalScore() != null).count();

        // 등급 분포
        Map<String, Long> gradeDist = employees.stream()
                .filter(e -> e.getFinalGrade() != null && !"-".equals(e.getFinalGrade()))
                .collect(Collectors.groupingBy(Employee::getFinalGrade, Collectors.counting()));

        // 부서별 평균 점수
        Map<String, Double> deptAverages = employees.stream()
                .filter(e -> e.getTotalScore() != null && e.getDepartmentName() != null)
                .collect(Collectors.groupingBy(
                        Employee::getDepartmentName,
                        Collectors.averagingDouble(Employee::getTotalScore)
                ));

        // 상위 5명 Top Talent
        List<Employee> topTalents = employees.stream()
                .filter(e -> e.getTotalScore() != null)
                .sorted(Comparator.comparingDouble(Employee::getTotalScore).reversed())
                .limit(5)
                .collect(Collectors.toList());

        model.addAttribute("totalCount",     totalCount);
        model.addAttribute("evaluatedCount", evaluatedCount);
        model.addAttribute("gradeDist",      gradeDist);
        model.addAttribute("deptAverages",   deptAverages);
        model.addAttribute("topTalents",     topTalents);

        return "index";
    }
}
