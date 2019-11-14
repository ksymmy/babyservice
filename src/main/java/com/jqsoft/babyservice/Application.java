package com.jqsoft.babyservice;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiCallBackRegisterCallBackRequest;
import com.dingtalk.api.response.OapiCallBackRegisterCallBackResponse;
import com.jqsoft.babyservice.service.biz.LoginService;
import com.taobao.api.ApiException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Arrays;

/**
 * 启动程序
 *
 * @author wangjie
 * @EnableScheduling 启用定时任务
 * @EnableCaching 启用缓存
 * @EnableTransactionManagement 开启事务支持，然后在需要事务的Service方法上添加注解 @Transactional便可
 */
@SpringBootApplication
@EnableScheduling
@EnableTransactionManagement
public class Application {
    @Resource
    private LoginService loginService;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    //    @PostConstruct
    private void registerCallBack() {
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/call_back/register_call_back");
        OapiCallBackRegisterCallBackRequest request = new OapiCallBackRegisterCallBackRequest();
        request.setUrl("http://ksymmy.vaiwan.com/eventreceive");
        request.setAesKey("xxxxxxxxlvdhntotr3x9qhlbytb18zyz5zxxxxxxxxx");
        request.setToken("123456");
        request.setCallBackTag(Arrays.asList("user_add_org", "user_modify_org", "user_leave_org",
                "user_active_org", "org_admin_add", "org_admin_remove"));
        try {
            OapiCallBackRegisterCallBackResponse response = client.execute(request, loginService.getAccessToken());
            System.out.println(response);
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }
}