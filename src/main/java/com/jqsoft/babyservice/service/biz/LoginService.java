package com.jqsoft.babyservice.service.biz;

import com.alipay.api.internal.util.codec.Base64;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.*;
import com.dingtalk.api.response.*;
import com.jqsoft.babyservice.commons.constant.RedisKey;
import com.jqsoft.babyservice.commons.utils.RedisUtils;
import com.jqsoft.babyservice.commons.vo.RestVo;
import com.jqsoft.babyservice.entity.biz.HospitalInfo;
import com.jqsoft.babyservice.entity.biz.UserInfo;
import com.jqsoft.babyservice.mapper.biz.UserInfoMapper;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class LoginService {
    @Resource
    private UserService userService;
    @Resource
    private UserInfoMapper userInfoMapper;
    @Resource
    private RedisUtils redisUtils;
    @Resource
    private RemindNewsService remindNewsService;
    @Resource
    private HospitalService hospitalService;

    public RestVo getUserIdByMobile() {
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/user/get_by_mobile");
        OapiUserGetByMobileRequest request = new OapiUserGetByMobileRequest();
        request.setMobile("18056006654");
        OapiUserGetByMobileResponse execute;
        try {
            execute = client.execute(request, getAccessToken("dinge3e211ad45c983df35c2f4657eb6378f"));
            return RestVo.SUCCESS(execute);
        } catch (ApiException e) {
            log.error("user/get错误:{}", e.getErrMsg());
            e.printStackTrace();
            return RestVo.FAIL();
        }
    }

    public RestVo login(String authCode, String corpid, String userid) {
        UserInfo userInfo1 = userService.selectByCorpIdAndUserid(corpid, userid);
        if (StringUtils.isNotBlank(userid) && null != userInfo1) {
            // 初始化一条欢迎消息
            remindNewsService.addWelcomeNews(userInfo1);
            return RestVo.SUCCESS(userInfo1);
        }

        userid = getUserid(authCode, corpid);
        if (StringUtils.isBlank(userid)) {
            return RestVo.FAIL("userid/get失败");
        }

        //定制应用获取不到手机号mobile
        OapiUserGetResponse userGetResponse = getUserInfo(userid, corpid);
        if (null == userGetResponse) {
            return RestVo.FAIL("user/get失败");
        }

        UserInfo oldUserInfo = userInfoMapper.selectByCorpIdAndUserid(corpid, userid), userInfo;
        String key = RedisKey.LOGIN_CORP_USER.getKey(corpid, userid);
        Date createDate = new Date();
        if (null == oldUserInfo) {
            userInfo = new UserInfo(null, userGetResponse.getName(), userGetResponse.getMobile(), null,
                    userGetResponse.getActive() ? (byte) 1 : (byte) 0, userGetResponse.getIsAdmin() ? (byte) 1 : (byte) 0, corpid, userid, createDate, createDate);
            if (1 == userService.insert(userInfo)) {
                redisUtils.add(key, userInfo, 7, TimeUnit.DAYS);
            }
        } else {
//            oldUserInfo.setMobile(userGetResponse.getMobile());
            oldUserInfo.setName(userGetResponse.getName());
            oldUserInfo.setAdmin(userGetResponse.getIsAdmin() ? (byte) 1 : (byte) 0);
            oldUserInfo.setActive(userGetResponse.getActive() ? (byte) 1 : (byte) 0);
            oldUserInfo.setUpdateTime(createDate);
//            userInfo = new UserInfo(oldUserInfo.getId(), userGetResponse.getName(), userGetResponse.getMobile(), null,
//                    userGetResponse.getActive() ? (byte) 1 : (byte) 0, userGetResponse.getIsAdmin() ? (byte) 1 : (byte) 0, corpid, userid, null, createDate);
            userInfo = oldUserInfo;
            if (1 == userService.updateByPrimaryKeySelective(userInfo)) {
                redisUtils.add(key, userInfo, 7, TimeUnit.DAYS);
            }
        }

        // 初始化一条欢迎消息
        remindNewsService.addWelcomeNews(userInfo);

        return RestVo.SUCCESS(userInfo);
    }

    public String getAccessToken(String corpid) {
        String key = RedisKey.LOGIN_ACCESS_TOKEN.getKey(corpid);
        if (redisUtils.exists(key)) {
            return (String) redisUtils.get(key);
        }
        HospitalInfo hospitalInfo = hospitalService.selectBycorpid(corpid);
        long timestamp = System.currentTimeMillis();
        String accessKey = hospitalInfo.getAppKey();
        String customSecret = hospitalInfo.getAppSecret();
        String suiteTicket = "suiteTicket";
        String signature = this.urlEncode(this.signature(timestamp, suiteTicket, customSecret), "UTF-8");
        String url = "https://oapi.dingtalk.com/service/get_corp_token?signature=" + signature + "&timestamp=" + timestamp + "&suiteTicket=" + suiteTicket + "&accessKey=" + accessKey;
        DefaultDingTalkClient client = new DefaultDingTalkClient(url);
        OapiServiceGetCorpTokenRequest req = new OapiServiceGetCorpTokenRequest();
        req.setAuthCorpid(hospitalInfo.getCorpid());
        String accessToken = null;
        try {
            OapiServiceGetCorpTokenResponse response = client.execute(req);
            accessToken = response.getAccessToken();
            if (StringUtils.isNotBlank(accessToken))
                redisUtils.add(key, accessToken, 7140, TimeUnit.SECONDS);
        } catch (ApiException e) {
            log.error("获取accessToken失败:corpid:{},{}", hospitalInfo.getCorpid(), e.getErrMsg());
            e.printStackTrace();
        }
        return accessToken;
    }

    /**
     * 设置缓存: corpid:accessToken
     *
     * @todo 每小时要更新一次
     */
    public void setAccessTokens() {
        //取出所有医院
        List<HospitalInfo> hospitalInfos = hospitalService.selectAll();
        if (CollectionUtils.isEmpty(hospitalInfos)) return;
        hospitalInfos.forEach(hospitalInfo -> {
            long timestamp = System.currentTimeMillis();
            String accessKey = hospitalInfo.getAppKey();
            String customSecret = hospitalInfo.getAppSecret();
            String suiteTicket = "suiteTicket";
            String signature = this.urlEncode(this.signature(timestamp, suiteTicket, customSecret), "UTF-8");
            String url = "https://oapi.dingtalk.com/service/get_corp_token?signature=" + signature + "&timestamp=" + timestamp + "&suiteTicket=" + suiteTicket + "&accessKey=" + accessKey;
            DefaultDingTalkClient client = new DefaultDingTalkClient(url);
            OapiServiceGetCorpTokenRequest req = new OapiServiceGetCorpTokenRequest();
            req.setAuthCorpid(hospitalInfo.getCorpid());
            OapiServiceGetCorpTokenResponse response;
            try {
                response = client.execute(req);
                String accessToken = response.getAccessToken();
                if (StringUtils.isNotBlank(accessToken))
                    redisUtils.add(RedisKey.LOGIN_ACCESS_TOKEN.getKey(hospitalInfo.getCorpid()), accessToken, 7140, TimeUnit.SECONDS);
            } catch (ApiException e) {
                log.error("获取accessToken失败:corpid:{},{}", hospitalInfo.getCorpid(), e.getErrMsg());
                e.printStackTrace();
            }
        });
    }

    /**
     * 签名
     *
     * @param timestamp
     * @param suiteTicket
     * @param suiteSecret
     * @return
     */
    public String signature(long timestamp, String suiteTicket, String suiteSecret) {
        String stringToSign = timestamp + "\n" + suiteTicket;
        Mac mac = null;
        try {
            mac = Mac.getInstance("HmacSHA256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            mac.init(new SecretKeySpec(suiteSecret.getBytes("UTF-8"), "HmacSHA256"));
        } catch (InvalidKeyException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        byte[] signData = new byte[0];
        try {
            signData = mac.doFinal(stringToSign.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return new String(Base64.encodeBase64(signData));
    }

    /**
     * encoding参数使用utf-8
     *
     * @param value
     * @param encoding
     * @return
     */
    public String urlEncode(String value, String encoding) {
        if (value == null) {
            return "";
        }
        try {
            String encoded = URLEncoder.encode(value, encoding);
            return encoded.replace("+", "%20").replace("*", "%2A")
                    .replace("~", "%7E").replace("/", "%2F");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("FailedToEncodeUri", e);
        }
    }

    public String getUserid(String authCode, String corpid) {
        DefaultDingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/user/getuserinfo");
        OapiUserGetuserinfoRequest userGetuserinfoRequest = new OapiUserGetuserinfoRequest();
        userGetuserinfoRequest.setCode(authCode);
        userGetuserinfoRequest.setHttpMethod("GET");
        OapiUserGetuserinfoResponse userGetuserinfoResponse;
        try {
            userGetuserinfoResponse = client.execute(userGetuserinfoRequest, getAccessToken(corpid));
        } catch (ApiException e) {
            log.error("user/getuserinfo错误:{}", e.getErrMsg());
            e.printStackTrace();
            return null;
        }
        return userGetuserinfoResponse.getUserid();
    }

    public OapiUserGetResponse  getUserInfo(String userid, String corpid) {
        DefaultDingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/user/get");
        OapiUserGetRequest userGetRequest = new OapiUserGetRequest();
        userGetRequest.setUserid(userid);
        userGetRequest.setHttpMethod("GET");
        OapiUserGetResponse userGetResponse = null;
        try {
            userGetResponse = client.execute(userGetRequest, getAccessToken(corpid));
        } catch (ApiException e) {
            log.error("user/get错误:{}", e.getErrMsg());
            e.printStackTrace();
        }
        return userGetResponse;
    }

    public OapiServiceGetAuthInfoResponse getAuthInfo(String corpid) {
        HospitalInfo hospitalInfo = hospitalService.selectBycorpid(corpid);
        long timestamp = System.currentTimeMillis();
        String accessKey = hospitalInfo.getAppKey();
        String customSecret = hospitalInfo.getAppSecret();
        String suiteTicket = "suiteTicket";
        String signature = this.urlEncode(this.signature(timestamp, suiteTicket, customSecret), "UTF-8");
        String url = "https://oapi.dingtalk.com/service/get_auth_info?signature=" + signature + "&timestamp=" + timestamp + "&suiteTicket=" + suiteTicket + "&accessKey=" + accessKey;
        DingTalkClient client = new DefaultDingTalkClient(url);
        OapiServiceGetAuthInfoRequest req = new OapiServiceGetAuthInfoRequest();
        req.setAuthCorpid(corpid);
        OapiServiceGetAuthInfoResponse response = null;
        try {
            response = client.execute(req, "suiteKey", "suiteSecrect", "suiteTicket");
        } catch (ApiException e) {
            log.error("service/get_auth_info错误:{}", e.getErrMsg());
            e.printStackTrace();
        }
        return response;
    }
}
