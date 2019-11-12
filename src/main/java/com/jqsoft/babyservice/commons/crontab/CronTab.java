package com.jqsoft.babyservice.commons.crontab;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.jqsoft.babyservice.service.system.DdService;

import lombok.extern.slf4j.Slf4j;

@Component
@EnableScheduling
@Configuration
@Slf4j
public class CronTab {
	@Autowired
	DdService ddServcie;
	/**
	 * TODO 从钉钉云数据库同步数据到本地数据库
	 * @author	李健平2018072
	 * @date 2019年10月22日 上午11:47:57
	 */
//	@Scheduled(cron = "0/30 * * * * ?")
//	@Scheduled(fixedRate=300000)
	private void syncData () {
		log.info("开始同步数据...");
//		Map<String,Object> params = new HashMap<String, Object>();
//		long time = 60*1000;//60秒
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//格式化输出日期
//		log.info("beginTime:{}",sdf.format(new Date(new Date().getTime()-time)));
//		log.info("endTime:{}",DateUtil.getDateTime());
//		params.put("beginTime", sdf.format(new Date(new Date().getTime()-time)));
//		params.put("endTime", DateUtil.getDateTime());
		
		//从钉钉云数据库查询最近更新的数据并做相应处理
		ddServcie.syncDdCloudData();
		log.info("同步数据完成...");
	}
}
