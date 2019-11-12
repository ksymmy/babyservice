package com.jqsoft.nposervice.service.biz;

import com.baomidou.mybatisplus.toolkit.MapUtils;
import com.google.common.collect.Lists;
import com.jqsoft.nposervice.commons.constant.RedisKey;
import com.jqsoft.nposervice.commons.utils.RedisUtils;
import com.jqsoft.nposervice.commons.vo.RestVo;
import com.jqsoft.nposervice.entity.biz.MeetingInfo;
import com.jqsoft.nposervice.mapper.biz.MeetingInfoMapper;
import com.jqsoft.nposervice.mapper.biz.ParticipantsCheckMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author: created by ksymmy@163.com at 2019/9/16 10:56
 * @desc:
 */
@Service
public class MeetingService {

    @Resource
    private MeetingInfoMapper meetingInfoMapper;
    @Resource
    private ParticipantsCheckMapper participantsCheckMapper;
    @Resource
    private RedisUtils redisUtils;

    public RestVo queryMeetingList(Map<String, Object> params) {
        return RestVo.SUCCESS(meetingInfoMapper.queryMeetingList(params));
    }

    public RestVo addMeeting(MeetingInfo meetingInfo) {
        meetingInfoMapper.insert(meetingInfo);
        meetingInfoMapper.insertCanParticipate(meetingInfo);
        return RestVo.SUCCESS();
    }

    public RestVo updateMeeting(MeetingInfo meetingInfo) {
        meetingInfoMapper.updateByPrimaryKeySelective(meetingInfo);
        if (StringUtils.isNotBlank(meetingInfo.getCanParticipateIds()) && StringUtils.isNotBlank(meetingInfo.getCanParticipateNames()))
            meetingInfoMapper.updateCanParticipate(meetingInfo);
        redisUtils.remove(RedisKey.MEETING_INFO.getKey(meetingInfo.getId()));
        return RestVo.SUCCESS();
    }

    public RestVo get(String meetingid) {
        String key = RedisKey.MEETING_INFO.getKey(meetingid);
        MeetingInfo meetingInfo = (MeetingInfo) redisUtils.get(key);
        if (null == meetingInfo) {
            meetingInfo = meetingInfoMapper.selectByPrimaryKey(meetingid);
            Map map = meetingInfoMapper.selectCanParticipants(meetingid);
            if (MapUtils.isNotEmpty(map)) {
                meetingInfo.setCanParticipateIds((String) map.get("canParticipateIds"));
                meetingInfo.setCanParticipateNames((String) map.get("canParticipateNames"));
            }
            redisUtils.add(key, meetingInfo);
        }
        return RestVo.SUCCESS(meetingInfo);
    }

    public void setParticipants(MeetingInfo meetingInfo) {
        List<Map> list = participantsCheckMapper.getHadRegParticipants(meetingInfo.getId());
        List<String> names = Lists.newLinkedList();
        list.forEach(map -> names.add((String) map.get("name")));
        meetingInfo.setParticipantNum(list.size());
        meetingInfo.setParticipantNames(names);
    }

    public RestVo delete(String meetingid) {
        int delete = meetingInfoMapper.deleteByPrimaryKey(meetingid);
        if (1 == delete) {
            redisUtils.remove(RedisKey.MEETING_INFO.getKey(meetingid));
        }
        return RestVo.SUCCESS(delete);
    }
}
