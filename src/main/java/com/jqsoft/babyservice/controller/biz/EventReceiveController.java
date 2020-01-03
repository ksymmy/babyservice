package com.jqsoft.babyservice.controller.biz;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiCallBackDeleteCallBackRequest;
import com.dingtalk.api.request.OapiCallBackRegisterCallBackRequest;
import com.dingtalk.api.response.OapiCallBackDeleteCallBackResponse;
import com.dingtalk.api.response.OapiCallBackRegisterCallBackResponse;
import com.dingtalk.api.response.OapiUserGetResponse;
import com.dingtalk.oapi.lib.aes.DingTalkEncryptException;
import com.dingtalk.oapi.lib.aes.DingTalkEncryptor;
import com.jqsoft.babyservice.commons.constant.AddressListRegister;
import com.jqsoft.babyservice.commons.constant.RedisKey;
import com.jqsoft.babyservice.commons.utils.RedisUtils;
import com.jqsoft.babyservice.entity.biz.HospitalInfo;
import com.jqsoft.babyservice.entity.biz.UserInfo;
import com.jqsoft.babyservice.service.biz.HospitalService;
import com.jqsoft.babyservice.service.biz.LoginService;
import com.jqsoft.babyservice.service.biz.UserService;
import com.taobao.api.ApiException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author: created by ksymmy@163.com at 2019/11/13 19:58
 * @desc: 企业注册事件回调
 */
@Controller
@RequestMapping("/event")
@Slf4j
public class EventReceiveController {
    @Value("${env.token}")
    private String token;
    @Value("${env.aes_key}")
    private String aes_key;
    @Value("${env.domain}")
    private String domain;
    @Resource
    private LoginService loginService;
    @Resource
    private UserService userService;
    @Resource
    private RedisUtils redisUtils;
    @Resource
    private HospitalService hospitalService;
    /**
     * 线程安全的变量customKey
     */
    private ThreadLocal<String> corpCustomKey = new ThreadLocal<>();

    /**
     * 注册业务事件回调接口
     */
    public void registerCallBack() {
        //取出所有企业,一一删除原注册事件,再重新注册
        List<HospitalInfo> hospitalInfos = hospitalService.selectAll();
        hospitalInfos.forEach(hospitalInfo -> {
            String corpid = hospitalInfo.getCorpid();
            //1.先删除已注册的事件回调
            DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/call_back/delete_call_back");
            OapiCallBackDeleteCallBackRequest deleteCallBackRequest = new OapiCallBackDeleteCallBackRequest();
            deleteCallBackRequest.setHttpMethod("GET");
            OapiCallBackDeleteCallBackResponse deleteCallBackResponse;
            try {
                deleteCallBackResponse = client.execute(deleteCallBackRequest, loginService.getAccessToken(corpid));
                log.info("删除事件回调,corpid:{},结果:{}", corpid, JSON.toJSONString(deleteCallBackResponse));
            } catch (ApiException e) {
                log.info("删除事件回调失败,corpid:{},error:{}", corpid, e.getErrMsg());
                e.printStackTrace();
            }

            //2.再重新注册的事件回调
            client = new DefaultDingTalkClient("https://oapi.dingtalk.com/call_back/register_call_back");
            OapiCallBackRegisterCallBackRequest registerCallBackRequest = new OapiCallBackRegisterCallBackRequest();
            registerCallBackRequest.setToken(token);
            registerCallBackRequest.setAesKey(aes_key);
            registerCallBackRequest.setUrl(domain + "/event/eventreceive_v1?regCorpId=" + corpid);
            registerCallBackRequest.setCallBackTag(Arrays.asList(AddressListRegister.USER_ADD_ORG, AddressListRegister.USER_MODIFY_ORG, AddressListRegister.USER_LEAVE_ORG,
                    AddressListRegister.USER_ACTIVE_ORG, AddressListRegister.ORG_ADMIN_ADD, AddressListRegister.ORG_ADMIN_REMOVE));
            OapiCallBackRegisterCallBackResponse registerCallBackResponse;
            try {
                registerCallBackResponse = client.execute(registerCallBackRequest, loginService.getAccessToken(corpid));
                log.info("注册事件回调,corpid:{},结果:{}", corpid, JSON.toJSONString(registerCallBackResponse));
            } catch (ApiException e) {
                log.info("注册事件回调失败,corpid:{},error:{}", corpid, e.getErrMsg());
                e.printStackTrace();
            }
        });
    }

    /**
     * 监听回调事件v1
     *
     * @param msgSignature url中的签名
     * @param timeStamp    url中的时间戳
     * @param nonce        url中的随机字符串
     * @param regCorpId    自定义的corpid
     * @param jsonEncrypt  加密的JSON字符串
     */
    @RequestMapping(value = "/eventreceive_v1", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String eventreceive_v1(@RequestParam(value = "signature") String msgSignature,
                                  @RequestParam(value = "timestamp") String timeStamp,
                                  @RequestParam(value = "nonce") String nonce,
                                  @RequestParam(value = "regCorpId") String regCorpId,
                                  @RequestBody JSONObject jsonEncrypt) {

        String result = "false";
        try {
            //res是需要返回给钉钉服务器的字符串，一般为success;
            String res = "success";
            if (jsonEncrypt != null && jsonEncrypt.size() > 0 && jsonEncrypt.containsKey("encrypt")) {
                String encrypt = jsonEncrypt.getString("encrypt");
                //密文解密
                String decodeEncrypt = decodeEncrypt(msgSignature, timeStamp, nonce, encrypt, regCorpId);
                DecodeEncryptEntity decodeEncryptJson = JSONObject.parseObject(decodeEncrypt, DecodeEncryptEntity.class);
                //根据不同的回调类型，进行相应的操作
                String corpid = decodeEncryptJson.getCorpId();
                JSONArray userids = decodeEncryptJson.getUserId();
                String userid;
                switch (decodeEncryptJson.getEventType()) {
                    case AddressListRegister.CHECK_URL:
                        log.info("服务注册成功```{}", corpid);
                        break;
                    case AddressListRegister.USER_ADD_ORG:
                        log.info("通讯录用户增加```{}", JSON.toJSONString(decodeEncryptJson));
                        for (int i = 0; i < userids.size(); i++) {
                            userid = userids.getString(i);
                            Date createDate = new Date();
                            OapiUserGetResponse userGetResponse = loginService.getUserInfo(userid, corpid);
                            UserInfo userInfo = new UserInfo(null, userGetResponse.getName(), userGetResponse.getMobile(), null,
                                    userGetResponse.getActive() ? (byte) 1 : (byte) 0, userGetResponse.getIsAdmin() ? (byte) 1 : (byte) 0, corpid, userid, createDate, createDate, null);
                            if (1 == userService.insert(userInfo)) {
                                String key = RedisKey.LOGIN_CORP_USER.getKey(corpid, userid);
                                redisUtils.add(key, userInfo, 7, TimeUnit.DAYS);
                            }
                        }
                        break;
                    case AddressListRegister.USER_MODIFY_ORG:
                        log.info("通讯录用户更改```{}", JSON.toJSONString(decodeEncryptJson));
                        corpid = decodeEncryptJson.getCorpId();
                        userids = decodeEncryptJson.getUserId();
                        for (int i = 0; i < userids.size(); i++) {
                            userid = userids.getString(i);
                            this.updateUserInfo(corpid, userid);
                        }
                        break;
                    case AddressListRegister.USER_LEAVE_ORG:
                        log.info("通讯录用户离职```{}", JSON.toJSONString(decodeEncryptJson));
                        corpid = decodeEncryptJson.getCorpId();
                        userids = decodeEncryptJson.getUserId();
                        for (int i = 0; i < userids.size(); i++) {
                            userid = userids.getString(i);
                            redisUtils.remove(RedisKey.LOGIN_CORP_USER.getKey(corpid, userid));
                            userService.deleteByUserid(userid);
                        }
                        break;
                    case AddressListRegister.USER_ACTIVE_ORG:
                        log.info("加入企业后用户激活```{}", JSON.toJSONString(decodeEncryptJson));
                        corpid = decodeEncryptJson.getCorpId();
                        userids = decodeEncryptJson.getUserId();
                        for (int i = 0; i < userids.size(); i++) {
                            userid = userids.getString(i);
                            this.updateUserInfo(corpid, userid);
                        }
                        break;
                    case AddressListRegister.ORG_ADMIN_ADD:
                        log.info("通讯录用户被设为管理员```{}", JSON.toJSONString(decodeEncryptJson));
                        corpid = decodeEncryptJson.getCorpId();
                        userids = decodeEncryptJson.getUserId();
                        for (int i = 0; i < userids.size(); i++) {
                            userid = userids.getString(i);
                            this.updateUserInfo(corpid, userid);
                        }
                        break;
                    case AddressListRegister.ORG_ADMIN_REMOVE:
                        log.info("通讯录用户被取消设置管理员```{}", JSON.toJSONString(decodeEncryptJson));
                        corpid = decodeEncryptJson.getCorpId();
                        userids = decodeEncryptJson.getUserId();
                        for (int i = 0; i < userids.size(); i++) {
                            userid = userids.getString(i);
                            this.updateUserInfo(corpid, userid);
                        }
                        break;
                    default:
                        log.info("EventType:{} of {}暂未处理```", decodeEncryptJson.getEventType(), corpid);
                        break;
                }
                result = codeEncrypt(res, timeStamp, nonce, regCorpId).toString();
            }
        } catch (Exception e) {
            log.error("回调事件错误:{}", e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 监听回调事件v2(可备用)
     *
     * @param request request
     */
    @ResponseBody
    @PostMapping(value = "/eventreceive_v2", produces = "application/json;charset=UTF-8")
    public String eventreceive_v2(HttpServletRequest request) {
        String result = "false";
//        try {
//            /** url中的签名 **/
//            String msgSignature = request.getParameter("signature");
//            /** url中的时间戳 **/
//            String timeStamp = request.getParameter("timestamp");
//            /** url中的随机字符串 **/
//            String nonce = request.getParameter("nonce");
//            /** 取得JSON对象中的encrypt字段 **/
//            String encrypt;
//
//            ServletInputStream sis = request.getInputStream();
//            BufferedReader br = new BufferedReader(new InputStreamReader(sis));
//            String line;
//            StringBuilder sb = new StringBuilder();
//            while ((line = br.readLine()) != null) {
//                sb.append(line);
//            }
//            JSONObject jsonEncrypt = JSONObject.parseObject(sb.toString());
//            String res = "success"; //res是需要返回给钉钉服务器的字符串，一般为success;
//            if (jsonEncrypt != null && jsonEncrypt.size() > 0 && jsonEncrypt.containsKey("encrypt")) {
//                encrypt = jsonEncrypt.getString("encrypt");
//                String decodeEncrypt = decodeEncrypt(msgSignature, timeStamp, nonce, encrypt); //密文解密
//                DecodeEncryptEntity decodeEncryptJson = JSONObject.parseObject(decodeEncrypt, DecodeEncryptEntity.class);
//                //根据不同的回调类型，进行相应的操作
//                String corpid = decodeEncryptJson.getCorpId();
//                JSONArray userids = decodeEncryptJson.getUserId();
//                String userid;
//                switch (decodeEncryptJson.getEventType()) {
//                    case AddressListRegister.CHECK_URL:
//                        log.info("服务注册成功```");
//                        break;
//                    case AddressListRegister.USER_ADD_ORG:
//                        log.info("通讯录用户增加```{}", JSON.toJSONString(decodeEncryptJson));
//                        for (int i = 0; i < userids.size(); i++) {
//                            userid = userids.getString(i);
//                            Date createDate = new Date();
//                            OapiUserGetResponse userGetResponse = loginService.getUserInfo(userid);
//                            UserInfo userInfo = new UserInfo(null, userGetResponse.getName(), userGetResponse.getMobile(), null,
//                                    userGetResponse.getActive() ? (byte) 1 : (byte) 0, userGetResponse.getIsAdmin() ? (byte) 1 : (byte) 0, corpid, userid, createDate, createDate);
//                            if (1 == userService.insert(userInfo)) {
//                                String key = RedisKey.LOGIN_CORP_USER.getKey(corpid, userid);
//                                redisUtils.add(key, userInfo, 7, TimeUnit.DAYS);
//                            }
//                        }
//                        break;
//                    case AddressListRegister.USER_MODIFY_ORG:
//                        log.info("通讯录用户更改```{}", JSON.toJSONString(decodeEncryptJson));
//                        corpid = decodeEncryptJson.getCorpId();
//                        userids = decodeEncryptJson.getUserId();
//                        for (int i = 0; i < userids.size(); i++) {
//                            userid = userids.getString(i);
//                            this.updateUserInfo(corpid, userid);
//                        }
//                        break;
//                    case AddressListRegister.USER_LEAVE_ORG:
//                        log.info("通讯录用户离职```{}", JSON.toJSONString(decodeEncryptJson));
//                        corpid = decodeEncryptJson.getCorpId();
//                        userids = decodeEncryptJson.getUserId();
//                        for (int i = 0; i < userids.size(); i++) {
//                            userid = userids.getString(i);
//                            redisUtils.remove(RedisKey.LOGIN_CORP_USER.getKey(corpid, userid));
//                            userService.deleteByUserid(userid);
//                        }
//                        break;
//                    case AddressListRegister.USER_ACTIVE_ORG:
//                        log.info("加入企业后用户激活```{}", JSON.toJSONString(decodeEncryptJson));
//                        corpid = decodeEncryptJson.getCorpId();
//                        userids = decodeEncryptJson.getUserId();
//                        for (int i = 0; i < userids.size(); i++) {
//                            userid = userids.getString(i);
//                            this.updateUserInfo(corpid, userid);
//                        }
//                        break;
//                    case AddressListRegister.ORG_ADMIN_ADD:
//                        log.info("通讯录用户被设为管理员```{}", JSON.toJSONString(decodeEncryptJson));
//                        corpid = decodeEncryptJson.getCorpId();
//                        userids = decodeEncryptJson.getUserId();
//                        for (int i = 0; i < userids.size(); i++) {
//                            userid = userids.getString(i);
//                            this.updateUserInfo(corpid, userid);
//                        }
//                        break;
//                    case AddressListRegister.ORG_ADMIN_REMOVE:
//                        log.info("通讯录用户被取消设置管理员```{}", JSON.toJSONString(decodeEncryptJson));
//                        corpid = decodeEncryptJson.getCorpId();
//                        userids = decodeEncryptJson.getUserId();
//                        for (int i = 0; i < userids.size(); i++) {
//                            userid = userids.getString(i);
//                            this.updateUserInfo(corpid, userid);
//                        }
//                        break;
//                    default:
//                        log.info("EventType:{},暂未处理```", decodeEncryptJson.getEventType());
//                        break;
//                }
//                result = codeEncrypt(res, timeStamp, nonce).toString();
//                //response.getWriter().append(codeEncrypt(res, timeStamp, nonce).toString()); //返回加密后的数据
//            }
//        } catch (Exception e) {
//            log.error("回调事件错误:{}", e.getMessage());
//            e.printStackTrace();
//        }
        return result;
    }

    /**
     * 更新钉钉用户信息到企业数据库 biz_user_info表
     */
    private void updateUserInfo(String corpid, String userid) {
        OapiUserGetResponse userGetResponse = loginService.getUserInfo(userid, corpid);
        UserInfo oldUserInfo = userService.selectByCorpIdAndUserid(corpid, userid), userInfo;
        if (null == oldUserInfo) {
            return;
        }
        Date createDate = new Date();
        userInfo = new UserInfo(oldUserInfo.getId(), userGetResponse.getName(), userGetResponse.getMobile(), null,
                userGetResponse.getActive() ? (byte) 1 : (byte) 0, userGetResponse.getIsAdmin() ? (byte) 1 : (byte) 0, corpid, userid, null, createDate, null);
        if (1 == userService.updateByPrimaryKeySelective(userInfo)) {
            String key = RedisKey.LOGIN_CORP_USER.getKey(corpid, userid);
            redisUtils.add(key, userInfo, 7, TimeUnit.DAYS);
        }
    }

    /**
     * encrypt解密
     *
     * @param msgSignature
     * @param timeStamp
     * @param nonce
     * @param encrypt      密文
     * @param corpid
     * @return decodeEncrypt 解密后的明文
     */
    public String decodeEncrypt(String msgSignature, String timeStamp, String nonce, String encrypt, String corpid) {
        String decodeEncrypt = null;
        try {
            //encrypt解密
            decodeEncrypt = createDingTalkEncryptor(corpid).getDecryptMsg(msgSignature, timeStamp, nonce, encrypt);
        } catch (DingTalkEncryptException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
        return decodeEncrypt;
    }

    /**
     * 创建加密/解密 类
     *
     * @return
     */
    public DingTalkEncryptor createDingTalkEncryptor(String corpid) {
        //加密方法类
        DingTalkEncryptor dingTalkEncryptor = null;
        try {
            String customKey = hospitalService.selectBycorpid(corpid).getAppKey();
            corpCustomKey.set(customKey);
            log.info("获取corpid:{}的customKey:{}", corpid, corpCustomKey.get());
            //创建加解密类
            dingTalkEncryptor = new DingTalkEncryptor(token, aes_key, corpCustomKey.get());
        } catch (DingTalkEncryptException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        } finally {
            corpCustomKey.remove();
        }
        return dingTalkEncryptor;
    }

    /**
     * 对返回信息进行加密
     *
     * @param res
     * @param timeStamp
     * @param nonce
     * @param corpid
     * @return
     */
    public JSONObject codeEncrypt(String res, String timeStamp, String nonce, String corpid) {
        long timeStampLong = Long.parseLong(timeStamp);
        Map<String, String> jsonMap = null;
        try {
            //jsonMap是需要返回给钉钉服务器的加密数据包
            jsonMap = createDingTalkEncryptor(corpid).getEncryptedMap(res, timeStampLong, nonce);
        } catch (DingTalkEncryptException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
        JSONObject json = new JSONObject();
        json.putAll(jsonMap);
        return json;
    }

    @Data
    public final static class DecodeEncryptEntity {
        private String EventType;
        private String CorpId;
        private Long TimeStamp;
        private JSONArray UserId;
    }
}
