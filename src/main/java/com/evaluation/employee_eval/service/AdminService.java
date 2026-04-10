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
    private final EvaluationScoreMapper scoreMapper;
    private final EvaluationTypeWeightMapper weightMapper;

    // ---- 부서 ----
    public List<Department> getAllDepartments()      { return departmentMapper.findAllWithDetails(); }
    public void addDepartment(Department d) {
        if (d.getIsActive() == null) d.setIsActive(true);
        departmentMapper.insert(d); 
    }
    public void updateDepartment(Department d)       { departmentMapper.update(d); }
    public void deleteDepartment(Long id)            { departmentMapper.delete(id); }

    // ---- 사원 ----
    public List<Employee> getAllEmployees()           { return employeeMapper.findAll(); }
    public void addEmployee(Employee e)              { employeeMapper.insert(e); }
    @Transactional
    public void deleteEmployee(Long id) {
        scoreMapper.deleteScoresByEmployeeId(id);
        mappingMapper.deleteByEmployeeId(id);
        employeeMapper.delete(id);
    }
    public void resetEmployeeAccount(Long id)        { employeeMapper.resetAccount(id); }

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

    // ---- 측정 가중치 ----
    public List<EvaluationTypeWeight> getAllWeights() { return weightMapper.findAll(); }
    public void updateWeight(EvaluationTypeWeight w)  { weightMapper.insertOrUpdate(w); }

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

    // ---- 평가자 일괄 자동 생성 ----
    @Transactional
    public void autoGenerateMappings() {
        List<Employee> employees = employeeMapper.findAll();
        List<Department> departments = departmentMapper.findAll();

        for (Employee emp : employees) {
            if ("RETIRED".equals(emp.getStatus())) continue;

            // 1. Self Evaluation (본인 평가)
            provisionSelfMapping(emp.getId(), "PERFORMANCE");
            provisionSelfMapping(emp.getId(), "COMPETENCY");

            // 2. Department Head Evaluation (부서장 평가) & Interview (면담)
            Department dept = departments.stream()
                    .filter(d -> d.getId().equals(emp.getDepartmentId()))
                    .findFirst().orElse(null);
            
            if (dept != null && dept.getHeadId() != null) {
                Long evaluatorId = dept.getHeadId();
                if (emp.getId().equals(evaluatorId)) {
                    if (dept.getParentId() != null) {
                        Department parentDept = departments.stream()
                            .filter(d -> d.getId().equals(dept.getParentId()))
                            .findFirst().orElse(null);
                        if (parentDept != null && parentDept.getHeadId() != null) {
                            evaluatorId = parentDept.getHeadId();
                        }
                    }
                }
                
                if (!emp.getId().equals(evaluatorId)) {
                    addMappingIfNotExists(emp.getId(), evaluatorId, "PERFORMANCE", false);
                    addMappingIfNotExists(emp.getId(), evaluatorId, "COMPETENCY", false);
                    addMappingIfNotExists(emp.getId(), evaluatorId, "INTERVIEW", false);
                }
            }

            // 3. Peer Evaluation (다면 평가)
            if (dept != null) {
                List<Employee> peers = employees.stream()
                        .filter(e -> dept.getId().equals(e.getDepartmentId()) && !"RETIRED".equals(e.getStatus()) && !e.getId().equals(emp.getId()))
                        .toList();
                for (Employee peer : peers) {
                    addMappingIfNotExists(emp.getId(), peer.getId(), "PEER", true);
                }
            }
        }
    }

    private void addMappingIfNotExists(Long evaluateeId, Long evaluatorId, String evalType, boolean isAnonymous) {
        if (mappingMapper.findByEvaluateeAndEvaluatorAndType(evaluateeId, evaluatorId, evalType) == null) {
            EvaluatorMapping mapping = new EvaluatorMapping();
            mapping.setEvaluateeId(evaluateeId);
            mapping.setEvaluatorId(evaluatorId);
            mapping.setEvalType(evalType);
            mapping.setIsAnonymous(isAnonymous);
            mappingMapper.insert(mapping);
        }
    }
}
