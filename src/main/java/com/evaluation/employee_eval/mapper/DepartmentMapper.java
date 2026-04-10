package com.evaluation.employee_eval.mapper;

import com.evaluation.employee_eval.domain.Department;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface DepartmentMapper {
    List<Department> findAllWithDetails();
    List<Department> findAll();
    Department findById(Long id);
    void insert(Department department);
    void update(Department department);
    void delete(Long id);
}
