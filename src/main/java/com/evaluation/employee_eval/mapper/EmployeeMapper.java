package com.evaluation.employee_eval.mapper;

import com.evaluation.employee_eval.domain.Employee;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface EmployeeMapper {
    List<Employee> findAll();
    Employee findById(Long id);
    Employee findByLoginId(String loginId);
    void insert(Employee employee);
    void update(Employee employee);
    void updateGrade(Employee employee);
    void delete(Long id);
}
