package com.jqsoft.babyservice.mapper.biz;

import com.jqsoft.babyservice.entity.biz.ExaminationInfo;

public interface ExaminationInfoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ExaminationInfo record);

    int insertSelective(ExaminationInfo record);

    ExaminationInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ExaminationInfo record);

    int updateByPrimaryKey(ExaminationInfo record);
}