package com.jqsoft.babyservice.controller.biz;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.request.OapiGetJsapiTicketRequest;
import com.dingtalk.api.response.OapiGetJsapiTicketResponse;
import com.jqsoft.babyservice.commons.interceptor.AdminCheck;
import com.jqsoft.babyservice.commons.utils.CommUtils;
import com.jqsoft.babyservice.commons.utils.DateUtil;
import com.jqsoft.babyservice.commons.vo.RestVo;
import com.jqsoft.babyservice.controller.system.BaseController;
import com.jqsoft.babyservice.service.biz.LoginService;
import com.jqsoft.babyservice.service.biz.UserService;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;

@Slf4j
@Controller
@RequestMapping("/fd")
public class FamilyDoctorController extends BaseController {

    @Autowired
    private LoginService loginService;

    @Value("${dd.app.agentId}")
    public String agentId;

    @GetMapping("/list")
    public String selectList(ModelMap model) {
        String accessToken = loginService.getAccessToken();
        System.out.println("accessToken=" + accessToken);
        DefaultDingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/get_jsapi_ticket");
        OapiGetJsapiTicketRequest req = new OapiGetJsapiTicketRequest();
        req.setTopHttpMethod("GET");
        try {
            OapiGetJsapiTicketResponse execute = client.execute(req, accessToken);
            System.out.println("getTicket=" + execute.getTicket());
            Long timeStamp = new Date().getTime();
            System.out.println("timeStamp==" + timeStamp);
            String nonceStr = CommUtils.getRandomNumber(10);
            model.put("agentId", agentId);
            model.put("corpId", "dinge3e211ad45c983df35c2f4657eb6378f");
            model.put("timeStamp", "1574908431");
            model.put("nonceStr", nonceStr);
            model.put("signature", CommUtils.sign(execute.getTicket(), nonceStr, 1574908431, "http://zuowhat.vaiwan.com/fd/list"));


        } catch (Exception e) {
            e.printStackTrace();
        }
        return "payInsure";
    }


}
