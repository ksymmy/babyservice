package com.jqsoft.babyservice.mapper.biz;

import com.jqsoft.babyservice.entity.biz.BabyInfo;

public interface BabyInfoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(BabyInfo record);

    int insertSelective(BabyInfo record);

    BabyInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(BabyInfo record);

    int updateByPrimaryKey(BabyInfo record);
}