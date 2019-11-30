package com.jqsoft.babyservice.job;

import com.jqsoft.babyservice.service.biz.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
@Configuration      //1.主要用于标记配置类，兼备Component的效果。
public class DynamicScheduleTask implements SchedulingConfigurer {

    @Value("${job.cron.accessTokenJob}")
    private String cron;

    @Resource
    private LoginService loginService;

    /**
     * 执行定时任务.
     */
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {

        taskRegistrar.addTriggerTask(
                //1.添加任务内容(Runnable)
                () -> {
                    log.info("执行动态定时任务: 更新全部AccessToken");
                    loginService.setAccessTokens();
                },
                //2.设置执行周期(Trigger)
                triggerContext -> {
                    if (StringUtils.isEmpty(cron)) {
                        // Omitted Code ..
                        cron = "0 0 * * * ?";
                    }
                    //2.3 返回执行周期(Date)
                    return new CronTrigger(cron).nextExecutionTime(triggerContext);
                }
        );
    }
}