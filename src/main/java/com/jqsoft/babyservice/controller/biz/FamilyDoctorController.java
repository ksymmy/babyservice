package com.jqsoft.babyservice.controller.biz;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.request.OapiGetJsapiTicketRequest;
import com.dingtalk.api.response.OapiGetJsapiTicketResponse;
import com.jqsoft.babyservice.commons.utils.CommUtils;
import com.jqsoft.babyservice.controller.system.BaseController;
import com.jqsoft.babyservice.service.biz.HospitalService;
import com.jqsoft.babyservice.service.biz.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.util.Date;

@Slf4j
@Controller
@RequestMapping("/fd")
public class FamilyDoctorController extends BaseController {

    @Autowired
    private LoginService loginService;
    @Resource
    private HospitalService hospitalService;

    @GetMapping("/list")
    public String selectList(ModelMap model) {
        String accessToken = loginService.getAccessToken(this.getDdCorpid());
        System.out.println("accessToken=" + accessToken);
        DefaultDingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/get_jsapi_ticket");
        OapiGetJsapiTicketRequest req = new OapiGetJsapiTicketRequest();
        req.setTopHttpMethod("GET");
        try {
            OapiGetJsapiTicketResponse execute = client.execute(req, accessToken);
            System.out.println("getTicket=" + execute.getTicket());
            long timeStamp = new Date().getTime();
            System.out.println("timeStamp==" + timeStamp);
            String nonceStr = CommUtils.getRandomNumber(10);
            model.put("agentId", hospitalService.selectBycorpid(this.getDdCorpid()).getAgentId());
            model.put("corpId", "dinge3e211ad45c983df35c2f4657eb6378f");
            model.put("timeStamp", "1574908431");
            model.put("nonceStr", nonceStr);
            model.put("signature", CommUtils.sign(execute.getTicket(), nonceStr, timeStamp / 1000, "http://zuowhat.vaiwan.com/fd/list"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "payInsure";
    }
}
