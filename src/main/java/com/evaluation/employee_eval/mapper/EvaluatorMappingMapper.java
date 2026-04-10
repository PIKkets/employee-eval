package com.evaluation.employee_eval.mapper;

import com.evaluation.employee_eval.domain.EvaluatorMapping;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface EvaluatorMappingMapper {
    List<EvaluatorMapping> findAll();
    List<EvaluatorMapping> findByEvaluatorAndType(@Param("evaluatorId") Long evaluatorId, @Param("evalType") String evalType);
    EvaluatorMapping findByEvaluateeAndEvaluatorAndType(@Param("evaluateeId") Long evaluateeId, @Param("evaluatorId") Long evaluatorId, @Param("evalType") String evalType);
    List<EvaluatorMapping> findByEvaluateeId(Long evaluateeId);
    EvaluatorMapping findById(Long id);
    void insert(EvaluatorMapping mapping);
    void delete(Long id);
    void deleteByEmployeeId(Long employeeId);
}
