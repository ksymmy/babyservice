package com.jqsoft.babyservice.service.biz;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.request.OapiGettokenRequest;
import com.dingtalk.api.request.OapiUserGetRequest;
import com.dingtalk.api.request.OapiUserGetuserinfoRequest;
import com.dingtalk.api.response.OapiGettokenResponse;
import com.dingtalk.api.response.OapiUserGetResponse;
import com.dingtalk.api.response.OapiUserGetuserinfoResponse;
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
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class LoginService {
//    @Value("${app.key}")
//    private String appKey;
//    @Value("${app.secret}")
//    private String appSecret;

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
            userInfo = new UserInfo(oldUserInfo.getId(), userGetResponse.getName(), userGetResponse.getMobile(), null,
                    userGetResponse.getActive() ? (byte) 1 : (byte) 0, userGetResponse.getIsAdmin() ? (byte) 1 : (byte) 0, corpid, userid, null, createDate);
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
        DefaultDingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/gettoken");
        OapiGettokenRequest request = new OapiGettokenRequest();
        request.setAppkey(hospitalInfo.getAppKey());
        request.setAppsecret(hospitalInfo.getAppSecret());
        request.setHttpMethod("GET");
        OapiGettokenResponse response;
        try {
            response = client.execute(request);
        } catch (ApiException e) {
            e.printStackTrace();
            return null;
        }
        String accessToken = response.getAccessToken();
        redisUtils.add(key, accessToken, 7140, TimeUnit.SECONDS);
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
            DefaultDingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/gettoken");
            OapiGettokenRequest request = new OapiGettokenRequest();
            request.setAppkey(hospitalInfo.getAppKey());
            request.setAppsecret(hospitalInfo.getAppSecret());
            request.setHttpMethod("GET");
            OapiGettokenResponse response;
            try {
                response = client.execute(request);
                String accessToken = response.getAccessToken();
                if (StringUtils.isNotBlank(accessToken))
                    redisUtils.add(RedisKey.LOGIN_ACCESS_TOKEN.getKey(hospitalInfo.getCorpid()), accessToken, 7140, TimeUnit.SECONDS);
            } catch (ApiException e) {
                log.error("获取accessToken失败:corpid:{},{}", hospitalInfo.getCorpid(), e.getErrMsg());
                e.printStackTrace();
            }
        });
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
            e.printStackTrace();
            return null;
        }
        return userGetuserinfoResponse.getUserid();
    }

    public OapiUserGetResponse getUserInfo(String userid, String corpid) {
        DefaultDingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/user/get");
        OapiUserGetRequest userGetRequest = new OapiUserGetRequest();
        userGetRequest.setUserid(userid);
        userGetRequest.setHttpMethod("GET");
        OapiUserGetResponse userGetResponse = null;
        try {
            userGetResponse = client.execute(userGetRequest, getAccessToken(corpid));
        } catch (ApiException e) {
            e.printStackTrace();
        }

        return userGetResponse;
    }
}
