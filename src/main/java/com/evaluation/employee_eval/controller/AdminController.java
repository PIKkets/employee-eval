package com.evaluation.employee_eval.controller;

import com.evaluation.employee_eval.domain.Department;
import com.evaluation.employee_eval.domain.Employee;
import com.evaluation.employee_eval.domain.EvaluationElement;
import com.evaluation.employee_eval.domain.EvaluatorMapping;
import com.evaluation.employee_eval.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    
    private final AdminService adminService;
    
    @GetMapping("/departments")
    public String departments(Model model) {
        model.addAttribute("departments", adminService.getAllDepartments());
        return "admin/departments";
    }
    
    @PostMapping("/departments/add")
    public String addDepartment(Department dept) {
        adminService.addDepartment(dept);
        return "redirect:/admin/departments";
    }

    @PostMapping("/departments/delete")
    public String deleteDepartment(@RequestParam Long id) {
        adminService.deleteDepartment(id);
        return "redirect:/admin/departments";
    }

    @GetMapping("/employees")
    public String employees(Model model) {
        model.addAttribute("employees", adminService.getAllEmployees());
        model.addAttribute("departments", adminService.getAllDepartments()); // For dropdown
        return "admin/employees";
    }
    
    @PostMapping("/employees/add")
    public String addEmployee(Employee emp) {
        emp.setPassword("{noop}1234"); // BCrypt 미적용 환경용 평문 패스워드
        if (emp.getRole() == null) emp.setRole("USER");
        adminService.addEmployee(emp);
        return "redirect:/admin/employees";
    }

    @PostMapping("/employees/delete")
    public String deleteEmployee(@RequestParam Long id) {
        adminService.deleteEmployee(id);
        return "redirect:/admin/employees";
    }
    
    @GetMapping("/elements")
    public String elements(Model model) {
        model.addAttribute("elements", adminService.getAllElements());
        return "admin/elements";
    }

    @PostMapping("/elements/add")
    public String addElement(EvaluationElement element) {
        adminService.addElement(element);
        return "redirect:/admin/elements";
    }

    @PostMapping("/elements/delete")
    public String deleteElement(@RequestParam Long id) {
        adminService.deleteElement(id);
        return "redirect:/admin/elements";
    }

    @GetMapping("/evaluators")
    public String evaluators(Model model) {
        model.addAttribute("mappings", adminService.getAllMappings());
        model.addAttribute("employees", adminService.getAllEmployees());
        return "admin/evaluators";
    }

    @PostMapping("/evaluators/add")
    public String addEvaluator(EvaluatorMapping mapping) {
        adminService.addMapping(mapping);
        return "redirect:/admin/evaluators";
    }

    @PostMapping("/evaluators/delete")
    public String deleteEvaluator(@RequestParam Long id) {
        adminService.deleteMapping(id);
        return "redirect:/admin/evaluators";
    }
}
