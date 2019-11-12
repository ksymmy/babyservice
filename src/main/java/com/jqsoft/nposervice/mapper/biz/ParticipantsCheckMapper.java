package com.jqsoft.nposervice.mapper.biz;

import com.jqsoft.nposervice.entity.biz.ParticipantsCheck;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface ParticipantsCheckMapper {
    int deleteByPrimaryKey(String id);

    int insert(ParticipantsCheck record);

    int insertSelective(ParticipantsCheck record);

    ParticipantsCheck selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(ParticipantsCheck record);

    int updateByPrimaryKey(ParticipantsCheck record);

    List<ParticipantsCheck> participantsList(String meetingId);

    List<Map> participantsBadges(String meetingId);

    List<Map> getHadRegParticipants(String meetingId);

    int updateStateByProcessInstanceId(Map params);

    int deleteByProcessInstanceId(String processInstanceId);
}