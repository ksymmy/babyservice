package com.jqsoft.babyservice.service.biz;

import com.jqsoft.babyservice.commons.constant.RedisKey;
import com.jqsoft.babyservice.commons.utils.DateUtil;
import com.jqsoft.babyservice.commons.utils.RedisUtils;
import com.jqsoft.babyservice.entity.biz.WorkTime;
import com.jqsoft.babyservice.mapper.biz.WorkTimeMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

@Slf4j
@Service
public class WorkTimeService {

    @Resource
    private WorkTimeMapper workTimeMapper;

    @Resource
    private RedisUtils redisUtils;


    /**
     * 获取工作时间
     * @param corpid
     * @return
     */
    public WorkTime getWorkTimeByCorpid(String corpid){
        String key = RedisKey.WORK_TIME.getKey(corpid);
        WorkTime workTime = (WorkTime)redisUtils.get(key);
        if (null == workTime) {
            workTime = workTimeMapper.getWorkTimeByCorpid(corpid);
            // 如果没设置工作时间获取7天全部为不上班，则默认1-5上班 6，7不上班
            if (null == workTime
                    || (workTime.getMonday() == 0
                    && workTime.getTuesday() == 0
                    && workTime.getWednesday() == 0
                    && workTime.getThursday() == 0
                    && workTime.getFriday() == 0
                    && workTime.getSaturday() == 0
                    && workTime.getSunday() == 0)) {
                workTime = new WorkTime();
                workTime.setMonday((byte) 1);
                workTime.setTuesday((byte) 1);
                workTime.setWednesday((byte) 1);
                workTime.setThursday((byte) 1);
                workTime.setFriday((byte) 1);
                workTime.setSaturday((byte) 0);
                workTime.setSunday((byte) 0);
            }
            redisUtils.add(key, workTime, RedisUtils.valueExpire);
        }
        return workTime;
    }

    /**
     * 判断是否在工作时间内
     * @param corpid
     * @param date
     * @return
     */
    public boolean isWorkTime(String corpid, Date date){
        WorkTime workTime = this.getWorkTimeByCorpid(corpid);
        // 计算日期是星期几
        int day = DateUtil.getDayOfWeek(date);
        if ((day == 1 && workTime.getMonday() == 1)
                || (day == 2 && workTime.getTuesday() == 1)
                || (day == 3 && workTime.getWednesday() == 1)
                || (day == 4 && workTime.getThursday() == 1)
                || (day == 5 && workTime.getFriday() == 1)
                || (day == 6 && workTime.getSaturday() == 1)
                || (day == 7 && workTime.getSunday() == 1)) {
            return true;
        }
        return false;
    }
}
