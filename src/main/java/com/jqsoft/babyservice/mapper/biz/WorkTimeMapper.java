package com.jqsoft.babyservice.mapper.biz;

import com.jqsoft.babyservice.entity.biz.WorkTime;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface WorkTimeMapper {
    int deleteByPrimaryKey(@Param("id") Long id);

    int insert(WorkTime record);

    int insertSelective(WorkTime record);

    WorkTime selectByPrimaryKey(@Param("id") Long id);

    int updateByPrimaryKeySelective(WorkTime record);

    int updateByPrimaryKey(WorkTime record);

    WorkTime getWorkTimeByCorpid(@Param("corpid") String corpid);
}