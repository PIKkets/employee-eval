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
    
    // Account Management 
    void updateLoginFailure(String loginId);
    void lockAccount(String loginId);
    void resetLoginFailure(String loginId);
    void resetAccount(Long id);
    void updatePassword(@org.apache.ibatis.annotations.Param("id") Long id, @org.apache.ibatis.annotations.Param("password") String password);
}
