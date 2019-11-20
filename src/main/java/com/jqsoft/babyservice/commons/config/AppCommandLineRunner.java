package com.jqsoft.babyservice.commons.config;

import com.alibaba.fastjson.JSON;
import com.dingtalk.api.response.OapiCallBackRegisterCallBackResponse;
import com.jqsoft.babyservice.controller.biz.EventReceiveController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author wangjie
 * 项目启动后 会执行这个方法，类似spring中的监听器
 * 这是个示例，你要不需要项目启动后进行额外的初始化，这个类可以删掉
 */
@Slf4j
@Component
public class AppCommandLineRunner implements CommandLineRunner {
    @Resource
    private EventReceiveController eventReceiveController;

    @Override
    public void run(String... args) {
        OapiCallBackRegisterCallBackResponse response = eventReceiveController.registerCallBack();
        log.info("注册业务事件回调接口结果:{}", JSON.toJSONString(response));
    }
}