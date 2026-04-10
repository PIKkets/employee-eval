package com.evaluation.employee_eval.service;

import com.evaluation.employee_eval.domain.*;
import com.evaluation.employee_eval.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final DepartmentMapper departmentMapper;
    private final EmployeeMapper employeeMapper;
    private final EvaluationElementMapper elementMapper;
    private final EvaluatorMappingMapper mappingMapper;

    // ---- 부서 ----
    public List<Department> getAllDepartments()      { return departmentMapper.findAll(); }
    public void addDepartment(Department d)          { departmentMapper.insert(d); }
    public void deleteDepartment(Long id)            { departmentMapper.delete(id); }

    // ---- 사원 ----
    public List<Employee> getAllEmployees()           { return employeeMapper.findAll(); }
    public void addEmployee(Employee e)              { employeeMapper.insert(e); }
    public void deleteEmployee(Long id)              { employeeMapper.delete(id); }

    /** ADMIN 역할을 제외한 평가 대상 사원 목록 */
    public List<Employee> getEvaluatableEmployees() {
        return employeeMapper.findAll().stream()
                .filter(e -> !"ADMIN".equals(e.getRole()))
                .collect(Collectors.toList());
    }

    // ---- 평가 항목 ----
    public List<EvaluationElement> getAllElements()  { return elementMapper.findAll(); }
    public void addElement(EvaluationElement e)      { elementMapper.insert(e); }
    public void deleteElement(Long id)               { elementMapper.delete(id); }

    // ---- 평가자 매핑 ----
    public List<EvaluatorMapping> getAllMappings()   { return mappingMapper.findAll(); }
    public void deleteMapping(Long id)               { mappingMapper.delete(id); }

    @Transactional
    public void addMapping(EvaluatorMapping m) {
        mappingMapper.insert(m);
        // PERFORMANCE 매핑 추가 시 피평가자의 자가 평가 매핑을 자동 생성
        if (EvalType.PERFORMANCE.equalsIgnoreCase(m.getEvalType())
                && !m.getEvaluateeId().equals(m.getEvaluatorId())) {
            provisionSelfMapping(m.getEvaluateeId(), EvalType.PERFORMANCE);
        }
    }

    /** 자가 평가 매핑이 없으면 새로 생성 */
    private void provisionSelfMapping(Long employeeId, String evalType) {
        if (mappingMapper.findByEvaluateeAndEvaluatorAndType(employeeId, employeeId, evalType) == null) {
            EvaluatorMapping self = new EvaluatorMapping();
            self.setEvaluateeId(employeeId);
            self.setEvaluatorId(employeeId);
            self.setEvalType(evalType);
            mappingMapper.insert(self);
        }
    }
}
