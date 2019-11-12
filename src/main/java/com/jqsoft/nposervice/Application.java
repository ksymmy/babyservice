package com.jqsoft.nposervice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 
 * 启动程序
 * @author wangjie
 * 
 * @EnableScheduling 启用定时任务
 * @EnableCaching 启用缓存
 * @EnableTransactionManagement 开启事务支持，然后在需要事务的Service方法上添加注解 @Transactional便可
 * 
 */
@EnableAsync
@EnableCaching
@SpringBootApplication
@EnableTransactionManagement
public class Application {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}