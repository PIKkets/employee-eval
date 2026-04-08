package com.evaluation.employee_eval.mapper;

import com.evaluation.employee_eval.domain.EvaluationElement;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface EvaluationElementMapper {
    List<EvaluationElement> findAll();
    List<EvaluationElement> findByType(String evalType);
    EvaluationElement findById(Long id);
    void insert(EvaluationElement element);
    void update(EvaluationElement element);
    void delete(Long id);
}
