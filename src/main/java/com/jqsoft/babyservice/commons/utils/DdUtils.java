package com.jqsoft.babyservice.commons.utils;

import com.alibaba.fastjson.JSONObject;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiMessageCorpconversationAsyncsendV2Request;
import com.dingtalk.api.response.OapiMessageCorpconversationAsyncsendV2Response;
import com.jqsoft.babyservice.service.biz.HospitalService;
import com.jqsoft.babyservice.service.biz.LoginService;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class DdUtils {

//    @Value("${dd.app.agentId}")
//    private Long agentId;

    @Autowired
    public LoginService loginService;

    @Resource
    private HospitalService hospitalService;


    /**
     * 批量发送钉钉通知消息
     *
     * @param ddMessageList
     */
    public void batchSendDdMessage(List<DdMessage> ddMessageList) {
        if (CollectionUtils.isNotEmpty(ddMessageList)) {
            for (DdMessage message : ddMessageList) {
                this.sendDdMessage(message.getTitle(), message.getContext(), message.getUserid(), message.getCorpid());
            }
        }
    }

    /**
     * 发送钉钉通知消息
     *
     * @param title
     * @param context
     * @param userid
     * @param corpid
     */
    public void sendDdMessage(String title, String context, String userid, String corpid) {
        if (StringUtils.isBlank(userid)) {
            return;
        }
        List<String> useridList = new ArrayList<>(Arrays.asList(userid.split(",")));
        List<String> batchUseridList = new ArrayList<>();
        int useridSize = useridList.size();
        if (useridSize <= 100) {
            batchUseridList.add(userid);
        } else {
            int batch = useridSize / 100;
            batch = useridSize % 100 == 0 ? batch : (batch + 1);
            for (int i = 0; i < batch; i++) {
                int start = i * 100;
                int end = start + 100;
                if (i == (batch - 1)) {
                    end = useridSize;
                }
                batchUseridList.add(StringUtils.join(useridList.subList(start, end).toArray(), ","));
            }
        }

        for (String batchUserid : batchUseridList) {
            DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/message/corpconversation/asyncsend_v2");

            OapiMessageCorpconversationAsyncsendV2Request request = new OapiMessageCorpconversationAsyncsendV2Request();
            request.setUseridList(batchUserid);
            request.setAgentId(Long.parseLong(hospitalService.selectBycorpid(corpid).getAgentId()));
            request.setToAllUser(false);

            OapiMessageCorpconversationAsyncsendV2Request.Msg msg = new OapiMessageCorpconversationAsyncsendV2Request.Msg();

            msg.setMsgtype("action_card");
            msg.setActionCard(new OapiMessageCorpconversationAsyncsendV2Request.ActionCard());
            msg.getActionCard().setTitle(title);
            String markdownContext = "##### <font size=3 font color=#2673CC font face='黑体'>" + StringUtils.substring(title, 0, 20) + "</font> \n " + context;
            msg.getActionCard().setMarkdown(markdownContext);
            msg.getActionCard().setBtnOrientation("1");
            List<OapiMessageCorpconversationAsyncsendV2Request.BtnJsonList> btnJsonList = new ArrayList<>();
            OapiMessageCorpconversationAsyncsendV2Request.BtnJsonList btnJson1 = new OapiMessageCorpconversationAsyncsendV2Request.BtnJsonList();
            btnJson1.setTitle("查看详情");
            btnJson1.setActionUrl("eapp://pages/customer/index/index");
            btnJsonList.add(btnJson1);
            msg.getActionCard().setBtnJsonList(btnJsonList);

            request.setMsg(msg);

            try {
                OapiMessageCorpconversationAsyncsendV2Response response = client.execute(request, loginService.getAccessToken(corpid));
                log.info("发送钉钉工作通知消息-结束 {}", JSONObject.toJSONString(response));
            } catch (ApiException e) {
                log.error("发送钉钉工作通知消息-失败", e);
            }
        }
    }


    public static void main(String[] args) {
        /*DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/message/corpconversation/asyncsend_v2");

        OapiMessageCorpconversationAsyncsendV2Request request = new OapiMessageCorpconversationAsyncsendV2Request();
        request.setUseridList("");
        request.setAgentId(316969988L);
        request.setToAllUser(false);*/
    }

}
