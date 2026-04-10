package com.evaluation.employee_eval.mapper;

import com.evaluation.employee_eval.domain.EvaluationTypeWeight;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface EvaluationTypeWeightMapper {
    List<EvaluationTypeWeight> findAll();
    EvaluationTypeWeight findByType(String evalType);
    void insertOrUpdate(EvaluationTypeWeight weight);
}
