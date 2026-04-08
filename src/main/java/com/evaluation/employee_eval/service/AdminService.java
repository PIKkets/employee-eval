package com.evaluation.employee_eval.service;

import com.evaluation.employee_eval.domain.*;
import com.evaluation.employee_eval.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final DepartmentMapper departmentMapper;
    private final EmployeeMapper employeeMapper;
    private final EvaluationElementMapper elementMapper;
    private final EvaluatorMappingMapper mappingMapper;

    public List<Department> getAllDepartments() { return departmentMapper.findAll(); }
    public void addDepartment(Department d) { departmentMapper.insert(d); }
    public void deleteDepartment(Long id) { departmentMapper.delete(id); }

    public List<Employee> getAllEmployees() { return employeeMapper.findAll(); }
    public void addEmployee(Employee e) { employeeMapper.insert(e); }
    public void deleteEmployee(Long id) { employeeMapper.delete(id); }

    public List<EvaluationElement> getAllElements() { return elementMapper.findAll(); }
    public void addElement(EvaluationElement e) { elementMapper.insert(e); }
    public void deleteElement(Long id) { elementMapper.delete(id); }

    public List<EvaluatorMapping> getAllMappings() { return mappingMapper.findAll(); }
    
    @org.springframework.transaction.annotation.Transactional
    public void addMapping(EvaluatorMapping m) { 
        mappingMapper.insert(m); 
        // Auto-provision self-evaluation for PERFORMANCE if it doesn't exist
        if ("PERFORMANCE".equalsIgnoreCase(m.getEvalType()) && !m.getEvaluateeId().equals(m.getEvaluatorId())) {
            EvaluatorMapping selfMapping = mappingMapper.findByEvaluateeAndEvaluatorAndType(m.getEvaluateeId(), m.getEvaluateeId(), "PERFORMANCE");
            if (selfMapping == null) {
                selfMapping = new EvaluatorMapping();
                selfMapping.setEvaluateeId(m.getEvaluateeId());
                selfMapping.setEvaluatorId(m.getEvaluateeId());
                selfMapping.setEvalType("PERFORMANCE");
                mappingMapper.insert(selfMapping);
            }
        }
    }
    public void deleteMapping(Long id) { mappingMapper.delete(id); }
}
