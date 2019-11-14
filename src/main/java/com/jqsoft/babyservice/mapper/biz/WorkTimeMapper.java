package com.jqsoft.babyservice.mapper.biz;

import com.jqsoft.babyservice.entity.biz.WorkTime;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface WorkTimeMapper {
    int deleteByPrimaryKey(Long id);

    int insert(WorkTime record);

    int insertSelective(WorkTime record);

    WorkTime selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(WorkTime record);

    int updateByPrimaryKey(WorkTime record);
}