package com.jqsoft.nposervice.mapper.biz;

import com.jqsoft.nposervice.entity.biz.MeetingNoticeTemplate;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface MeetingNoticeTemplateMapper {
    int deleteByPrimaryKey(String id);

    int insert(MeetingNoticeTemplate record);

    int insertSelective(MeetingNoticeTemplate record);

    MeetingNoticeTemplate selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(MeetingNoticeTemplate record);

    int updateByPrimaryKey(MeetingNoticeTemplate record);

    List selectList(String orgId);

    int queryUnique(Map<String, Object> params);
}