package com.jqsoft.babyservice.mapper.biz;

import com.jqsoft.babyservice.entity.biz.HospitalInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface HospitalInfoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(HospitalInfo record);

    HospitalInfo selectByPrimaryKey(Long id);

    List<HospitalInfo> selectAll();

    int updateByPrimaryKey(HospitalInfo record);

    HospitalInfo selectBycorpid(String corpid);
}