package com.jqsoft.babyservice.commons.crontab;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@Configuration
@Slf4j
public class CronTab {

    //	@Scheduled(cron = "0/30 * * * * ?")
//	@Scheduled(fixedRate=300000)
    private void syncData() {
        log.info("开始同步数据...");
        //从钉钉云数据库查询最近更新的数据并做相应处理
        log.info("同步数据完成...");
    }
}
