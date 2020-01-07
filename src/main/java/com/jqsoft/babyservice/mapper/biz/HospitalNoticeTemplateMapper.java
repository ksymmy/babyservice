package com.jqsoft.babyservice.mapper.biz;

import com.jqsoft.babyservice.entity.biz.HospitalNoticeTemplate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface HospitalNoticeTemplateMapper {
    int deleteByPrimaryKey(@Param("id") Long id);

    int insert(HospitalNoticeTemplate record);

    HospitalNoticeTemplate selectByPrimaryKey(@Param("id") Long id);

    HospitalNoticeTemplate selectByCorpid(@Param("corpid") String corpid);

    List<HospitalNoticeTemplate> selectAll();

    int updateByPrimaryKey(HospitalNoticeTemplate record);

    int updateByCorpid(@Param("corpid") String corpid,
                       @Param("et") String et,
                       @Param("text") String text);
}