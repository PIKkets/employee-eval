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
        model.addAttribute("employees", adminService.getAllEmployees());
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

    @PostMapping("/departments/update")
    public String updateDepartment(Department dept) {
        if (dept.getIsActive() == null) {
            dept.setIsActive(false); // HTML checkbox empty means false
        }
        // Assuming adminService.updateDepartment exists - wait I need to add it to AdminService!
        adminService.updateDepartment(dept);
        return "redirect:/admin/departments";
    }

    @GetMapping("/employees")
    public String employees(Model model) {
        model.addAttribute("employees", adminService.getAllEmployees());
        model.addAttribute("departments", adminService.getAllDepartments()); // For dropdown
        return "admin/employees";
    }
    
    @PostMapping("/employees/add")
    public String addEmployee(@ModelAttribute Employee emp) {
        emp.setPassword("{noop}1234");
        if (emp.getIsLeader() == null) {
            emp.setIsLeader(false);
        }
        if (emp.getRole() == null) emp.setRole("USER");
        adminService.addEmployee(emp);
        return "redirect:/admin/employees";
    }

    @PostMapping("/employees/delete")
    public String deleteEmployee(@RequestParam Long id) {
        adminService.deleteEmployee(id);
        return "redirect:/admin/employees";
    }

    @PostMapping("/employees/reset")
    public String resetEmployee(@RequestParam Long id) {
        adminService.resetEmployeeAccount(id);
        return "redirect:/admin/employees";
    }
    
    @GetMapping("/elements")
    public String elements(Model model) {
        model.addAttribute("elements", adminService.getAllElements());
        model.addAttribute("weights", adminService.getAllWeights());
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

    @PostMapping("/elements/weight/update")
    public String updateWeight(@RequestParam String evalType, @RequestParam Integer weight) {
        com.evaluation.employee_eval.domain.EvaluationTypeWeight w = new com.evaluation.employee_eval.domain.EvaluationTypeWeight();
        w.setEvalType(evalType);
        w.setWeight(weight);
        adminService.updateWeight(w);
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
        if (mapping.getIsAnonymous() == null) {
            mapping.setIsAnonymous(false);
        }
        adminService.addMapping(mapping);
        return "redirect:/admin/evaluators";
    }

    @PostMapping("/evaluators/delete")
    public String deleteEvaluator(@RequestParam Long id) {
        adminService.deleteMapping(id);
        return "redirect:/admin/evaluators";
    }

    @PostMapping("/evaluators/auto-generate")
    public String autoGenerateEvaluators() {
        adminService.autoGenerateMappings();
        return "redirect:/admin/evaluators";
    }
}
