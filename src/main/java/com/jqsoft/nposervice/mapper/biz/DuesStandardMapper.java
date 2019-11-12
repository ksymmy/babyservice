package com.jqsoft.nposervice.mapper.biz;

import com.jqsoft.nposervice.entity.biz.DuesStandard;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface DuesStandardMapper {
    int deleteByPrimaryKey(String id);

    int insert(DuesStandard record);

    int insertSelective(DuesStandard record);

    DuesStandard selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(DuesStandard record);

    int updateByPrimaryKey(DuesStandard record);

    List<DuesStandard> selectList(Map<String, Object> params);
}