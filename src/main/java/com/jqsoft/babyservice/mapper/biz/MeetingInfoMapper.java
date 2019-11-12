package com.jqsoft.babyservice.mapper.biz;

import com.jqsoft.babyservice.entity.biz.MeetingInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface MeetingInfoMapper {
    int deleteByPrimaryKey(String id);

    int insert(MeetingInfo record);

    int insertSelective(MeetingInfo record);

    MeetingInfo selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(MeetingInfo record);

    int updateByPrimaryKey(MeetingInfo record);

    List<MeetingInfo> queryMeetingList(Map<String, Object> params);

    int insertCanParticipate(MeetingInfo meetingInfo);

    int updateCanParticipate(MeetingInfo meetingInfo);

    Map selectCanParticipants(String meetingId);
}