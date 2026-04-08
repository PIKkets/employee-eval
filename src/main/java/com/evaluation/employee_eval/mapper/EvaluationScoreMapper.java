package com.evaluation.employee_eval.mapper;

import com.evaluation.employee_eval.domain.EvaluationScore;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface EvaluationScoreMapper {
    List<EvaluationScore> findByMappingId(Long mappingId);
    void insert(EvaluationScore score);
    void deleteByMappingId(Long mappingId);
}
