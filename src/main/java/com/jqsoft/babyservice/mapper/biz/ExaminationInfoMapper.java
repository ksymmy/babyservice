package com.jqsoft.babyservice.mapper.biz;

import com.jqsoft.babyservice.entity.biz.ExaminationInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ExaminationInfoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ExaminationInfo record);

    int insertSelective(ExaminationInfo record);

    ExaminationInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ExaminationInfo record);

    int updateByPrimaryKey(ExaminationInfo record);

    List<ExaminationInfo> myExaminationInfo(@Param("babyId") Long babyId);

    void batchInsert(List<ExaminationInfo> infos);
}