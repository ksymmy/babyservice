package com.jqsoft.babyservice.controller.biz;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dingtalk.oapi.lib.aes.DingTalkEncryptException;
import com.dingtalk.oapi.lib.aes.DingTalkEncryptor;
import com.dingtalk.oapi.lib.aes.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author: created by ksymmy@163.com at 2019/11/13 19:58
 * @desc:
 */
@Controller
@RequestMapping("/")
@Slf4j
public class EventReceiveController {
    @Value("${env.token}")
    private String token;
    @Value("${env.encoding_aes_key}")
    private String encoding_aes_key;
    @Value("${env.key}")
    private String env_key;

    @RequestMapping(value = "eventreceive", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> eventreceive(@RequestParam(value = "msg_signature", required = false) String msg_signature,
                                            @RequestParam(value = "timestamp", required = false) String timeStamp,
                                            @RequestParam(value = "nonce", required = false) String nonce,
                                            @RequestParam(value = "encrypt", required = false) String encrypt) throws DingTalkEncryptException {


        DingTalkEncryptor dingTalkEncryptor = null;
        String plainText;
        String encryptMsg;
        dingTalkEncryptor = new DingTalkEncryptor(token, encoding_aes_key, env_key);
        // 从post请求的body中获取回调信息的加密数据进行解密处理
//        encryptMsg = json.getString("encrypt");
        plainText = dingTalkEncryptor.getDecryptMsg(msg_signature, timeStamp, nonce, encrypt);
        JSONObject obj = JSON.parseObject(plainText);
        // 根据回调数据类型做不同的业务处理
        String eventType = obj.getString("EventType");

        if ("check_create_suite_url".equals(eventType)) {
            log.info("第一次设置回调地址检测推送: " + plainText);
        } else if ("check_update_suite_url".equals(eventType)) {
            log.info("更新回调地址检测推送: " + plainText);
        } else if ("suite_ticket".equals(eventType)) {
            log.info("套件Ticket数据推送: " + plainText);
        }
        log.info("eventType:{}", eventType);
        // 返回success的加密信息表示回调处理成功
        return dingTalkEncryptor.getEncryptedMap("success", System.currentTimeMillis(), Utils.getRandomStr(8));

    }

}
