package com.jqsoft.babyservice.commons.utils;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiMessageCorpconversationAsyncsendV2Request;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class DdUtils {

    @Value("${dd.app.agentId}")
    private Long agentId;


    /**
     * 批量发送钉钉通知消息
     * @param ddMessageList
     */
    public void batchSendDdMessage(List<DdMessage> ddMessageList){
        if (CollectionUtils.isNotEmpty(ddMessageList)) {
            for (DdMessage message : ddMessageList) {
                this.sendDdMessage(message.getTitle(),message.getContext(),message.getUserid());
            }
        }
    }

    /**
     * 发送钉钉通知消息
     * @param title
     * @param context
     * @param userid
     */
    public void sendDdMessage(String title, String context,String userid){
        if (StringUtils.isBlank(userid)) {
            return;
        }
        List<String> useridList= new ArrayList<>(Arrays.asList(userid.split(",")));
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
                batchUseridList.add(StringUtils.join(useridList.subList(start, end).toArray(),","));
            }
        }

        for (String batchUserid : batchUseridList) {
            DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/message/corpconversation/asyncsend_v2");

            OapiMessageCorpconversationAsyncsendV2Request request = new OapiMessageCorpconversationAsyncsendV2Request();
            request.setUseridList(batchUserid);
            request.setAgentId(agentId);
            request.setToAllUser(false);

            OapiMessageCorpconversationAsyncsendV2Request.Msg msg = new OapiMessageCorpconversationAsyncsendV2Request.Msg();
//        msg.setMsgtype("text");
//        msg.setText(new OapiMessageCorpconversationAsyncsendV2Request.Text());
//        msg.getText().setContent("test123");
//        request.setMsg(msg);
//
//        msg.setMsgtype("image");
//        msg.setImage(new OapiMessageCorpconversationAsyncsendV2Request.Image());
//        msg.getImage().setMediaId("@lADOdvRYes0CbM0CbA");
//        request.setMsg(msg);
//
//        msg.setMsgtype("file");
//        msg.setFile(new OapiMessageCorpconversationAsyncsendV2Request.File());
//        msg.getFile().setMediaId("@lADOdvRYes0CbM0CbA");
//        request.setMsg(msg);
//
//        msg.setMsgtype("link");
//        msg.setLink(new OapiMessageCorpconversationAsyncsendV2Request.Link());
//        msg.getLink().setTitle("test");
//        msg.getLink().setText("test");
//        msg.getLink().setMessageUrl("test");
//        msg.getLink().setPicUrl("test");
//        request.setMsg(msg);

            msg.setMsgtype("markdown");
            msg.setMarkdown(new OapiMessageCorpconversationAsyncsendV2Request.Markdown());
            msg.getMarkdown().setText(context);
            msg.getMarkdown().setTitle(title);
            request.setMsg(msg);

//        msg.setOa(new OapiMessageCorpconversationAsyncsendV2Request.OA());
//        msg.getOa().setHead(new OapiMessageCorpconversationAsyncsendV2Request.Head());
//        msg.getOa().getHead().setText("head");
//        msg.getOa().setBody(new OapiMessageCorpconversationAsyncsendV2Request.Body());
//        msg.getOa().getBody().setContent("xxx");
//        msg.setMsgtype("oa");
//        request.setMsg(msg);

//        msg.setActionCard(new OapiMessageCorpconversationAsyncsendV2Request.ActionCard());
//        msg.getActionCard().setTitle("xxx123411111");
//        msg.getActionCard().setMarkdown("### 测试123111");
//        msg.getActionCard().setSingleTitle("测试测试");
//        msg.getActionCard().setSingleUrl("https://www.baidu.com");
//        msg.setMsgtype("action_card");
//        request.setMsg(msg);

//        OapiMessageCorpconversationAsyncsendV2Response response = client.execute(request,accessToken);
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
