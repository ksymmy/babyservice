package com.jqsoft.babyservice.service.biz;

import com.jqsoft.babyservice.commons.constant.RedisKey;
import com.jqsoft.babyservice.commons.utils.RedisUtils;
import com.jqsoft.babyservice.entity.biz.WorkTime;
import com.jqsoft.babyservice.mapper.biz.WorkTimeMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class WorkTimeService {

    @Resource
    private WorkTimeMapper workTimeMapper;

    @Resource
    private RedisUtils redisUtils;


    public WorkTime getWorkTimeByCorpid(String corpid){
        String key = RedisKey.WORK_TIME.getKey(corpid);
        WorkTime workTime = (WorkTime)redisUtils.get(key);
        if (null == workTime) {
            workTime = workTimeMapper.getWorkTimeByCorpid(corpid);
            if (null != workTime) {
                redisUtils.add(key, workTime, RedisUtils.valueExpire);
            }
        }
        return workTime;
    }
}
