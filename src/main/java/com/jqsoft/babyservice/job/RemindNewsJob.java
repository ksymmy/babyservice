package com.jqsoft.babyservice.job;

import com.jqsoft.babyservice.commons.utils.DateUtil;
import com.jqsoft.babyservice.commons.utils.DdMessage;
import com.jqsoft.babyservice.commons.utils.DdUtils;
import com.jqsoft.babyservice.entity.biz.ExaminationInfo;
import com.jqsoft.babyservice.entity.biz.RemindNews;
import com.jqsoft.babyservice.mapper.biz.ExaminationInfoMapper;
import com.jqsoft.babyservice.mapper.biz.RemindNewsMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 定时提醒任务
 */
@Slf4j
@Component
public class RemindNewsJob {

    @Autowired(required = false)
    public ExaminationInfoMapper examinationInfoMapper;

    @Autowired(required = false)
    public DdUtils ddUtils;

    @Autowired(required = false)
    public RemindNewsMapper remindNewsMapper;

    @Value("${remind.template0}")
    private String remindTemplate0;

    @Value("${remind.template1}")
    private String remindTemplate1;

    @Value("${remind.template2}")
    private String remindTemplate2;

    @Scheduled(cron = "${job.cron.remindNewsJob}")
    public void remindNewsJob() {
        log.info("定时提醒任务 开始");
        Date now = new Date();
        // 按100/批处理
        int size = 100;
        int page = 1;
        List<ExaminationInfo> examinationInfos = examinationInfoMapper.getNeedRemindExaminationInfoList((page - 1) * size,size);
        while (examinationInfos.size() > 0) {
            int start = (page - 1) * size + 1;
            int end = start + size - 1;
            log.info("定时提醒任务 开始处理{}-{}条", start, end);
            try{
                Map<Byte,String> age_userid_map0 = new HashMap<>();
                Map<Byte,String> age_userid_map2 = new HashMap<>();
                List<RemindNews> remindNewsList = new ArrayList<>();
                for (ExaminationInfo info : examinationInfos) {
                    RemindNews news = new RemindNews();
                    news.setExaminationId(info.getId());
                    news.setCreateTime(now);
                    news.setUpdateTime(now);
                    String age = info.getExaminationType() == 1 ? "满月" : info.getExaminationType() + "月龄";
                    if (DateUtils.isSameDay(now, info.getExaminationDate())) {
                        // 当天 只记录数据库
                        news.setTitle(StringUtils.join(info.getBabyName()," - ", age,"儿童健康体检签到"));
                        news.setContext(String.format(remindTemplate1, age));
                        news.setType((byte)1);
                    } else if (now.before(info.getExaminationDate())){
                        // 提前 发送钉钉工作通知，并记录数据库
                        news.setTitle(StringUtils.join(info.getBabyName()," - ", age,"儿童健康体检通知"));
                        news.setContext(String.format(remindTemplate0, age, age));
                        news.setType((byte)0);

                        String userid0 = age_userid_map0.get(info.getExaminationType());
                        if (StringUtils.isBlank(userid0)) {
                            age_userid_map0.put(info.getExaminationType(), info.getUserid());
                        } else {
                            age_userid_map0.put(info.getExaminationType(), userid0 + "," + info.getUserid());
                        }
                    } else if (now.after(info.getExaminationDate())){
                        // 逾期 发送钉钉工作通知，并记录数据库
                        String addDays = DateUtil.differentDaysByDate(info.getExaminationDate(),now).toString();
                        addDays = "0".equals(addDays) ? "" : "+" + addDays + "天";
                        news.setTitle(StringUtils.join(info.getBabyName()," - ", age,"儿童健康体检签到"));
                        news.setContext(String.format(remindTemplate2, age + addDays, age));
                        news.setType((byte)2);

                        String userid2 = age_userid_map2.get(info.getExaminationType());
                        if (StringUtils.isBlank(userid2)) {
                            age_userid_map2.put(info.getExaminationType(), info.getUserid());
                        } else {
                            age_userid_map2.put(info.getExaminationType(), userid2 + "," + info.getUserid());
                        }
                    }
                    remindNewsList.add(news);
                }
                remindNewsMapper.batchInsert(remindNewsList);

                // 批量发送钉钉提醒消息
                // 1.提前通知
                List<DdMessage> ddMessageList = new ArrayList<>();
                for (Map.Entry<Byte, String> entry : age_userid_map0.entrySet()) {
                    DdMessage message = new DdMessage();
                    String age = entry.getKey() == 1 ? "满月" : entry.getKey() + "月龄";
                    message.setTitle(StringUtils.join(age,"儿童健康体检通知"));
                    message.setContext(String.format(remindTemplate0, age, age));
                    message.setUserid(entry.getValue());
                    ddMessageList.add(message);
                }
                for (Map.Entry<Byte, String> entry : age_userid_map2.entrySet()) {
                    DdMessage message = new DdMessage();
                    String age = entry.getKey() == 1 ? "满月" : entry.getKey() + "月龄";
                    message.setTitle(StringUtils.join(age,"儿童健康体检通知"));
                    message.setContext(String.format(remindTemplate2, age, age));
                    message.setUserid(entry.getValue());
                    ddMessageList.add(message);
                }
                ddUtils.batchSendDdMessage(ddMessageList);
                log.info("定时提醒任务 成功处理{}-{}条", start, end);
            } catch (Exception e){
                log.error("定时提醒任务 {}-{}条出现异常",start, end, e);
            }finally {
                examinationInfos = examinationInfoMapper.getNeedRemindExaminationInfoList((++page - 1) * size,size);
            }
        }
        log.info("定时提醒任务 结束");
    }
}
