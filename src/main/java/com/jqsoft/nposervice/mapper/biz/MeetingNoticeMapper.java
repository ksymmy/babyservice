package com.jqsoft.nposervice.mapper.biz;

import com.jqsoft.nposervice.entity.biz.MeetingNotice;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface MeetingNoticeMapper {
    int deleteByPrimaryKey(String id);

    int insert(MeetingNotice record);

    int insertSelective(MeetingNotice record);

    MeetingNotice selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(MeetingNotice record);

    int updateByPrimaryKey(MeetingNotice record);

    List meetingNoticeList(Map<String, Object> params);
}