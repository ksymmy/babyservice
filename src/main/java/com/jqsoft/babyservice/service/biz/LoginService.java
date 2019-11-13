package com.jqsoft.babyservice.service.biz;

import com.alibaba.druid.util.StringUtils;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiGettokenRequest;
import com.dingtalk.api.request.OapiUserGetuserinfoRequest;
import com.dingtalk.api.response.OapiGettokenResponse;
import com.dingtalk.api.response.OapiUserGetuserinfoResponse;
import com.jqsoft.babyservice.commons.vo.RestVo;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LoginService {
    @Value("${app.key}")
    private String appKey;
    @Value("${app.secret}")
    private String appSecret;

    /**
     * TODO 从钉钉接口获取userid
     *
     * @param requestAuthCode 免登授权码
     * @param accessToken     第三方企业凭证
     * @return
     * @throws ApiException
     * @author 李健平2018072
     * @date 2019年9月16日 上午10:34:47
     */
    public String getUserId(String requestAuthCode, String accessToken) throws ApiException {
        log.info("入参：requestAuthCode={}", requestAuthCode);
        log.info("入参：accessToken={}", accessToken);
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/user/getuserinfo");
        OapiUserGetuserinfoRequest request = new OapiUserGetuserinfoRequest();
        request.setCode(requestAuthCode);
        request.setHttpMethod("GET");
        OapiUserGetuserinfoResponse response = client.execute(request, accessToken);
        if (StringUtils.isEmpty(response.getUserid())) {
            log.info("用户不存在！");
            return null;
        }
        log.info("获取userId成功:{}", response.getUserid());
        return response.getUserid();
    }

    public RestVo login(String requestAuthCode, String corpId) {
        DefaultDingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/gettoken");
        OapiGettokenRequest request = new OapiGettokenRequest();
        request.setAppkey(appKey);
        request.setAppsecret(appSecret);
        request.setHttpMethod("GET");
        OapiGettokenResponse response;
        try {
            response = client.execute(request);
        } catch (ApiException e) {
            e.printStackTrace();
            return RestVo.FAIL("gettoken失败");
        }
        String accessToken = response.getAccessToken();

        client = new DefaultDingTalkClient("https://oapi.dingtalk.com/user/getuserinfo");
        OapiUserGetuserinfoRequest userGetuserinfoRequest = new OapiUserGetuserinfoRequest();
        userGetuserinfoRequest.setCode(requestAuthCode);
        userGetuserinfoRequest.setHttpMethod("GET");
        OapiUserGetuserinfoResponse userGetuserinfoResponse;
        try {
            userGetuserinfoResponse = client.execute(userGetuserinfoRequest, accessToken);
        } catch (ApiException e) {
            e.printStackTrace();
            return RestVo.FAIL("getuserinfo失败");
        }
        return RestVo.SUCCESS(userGetuserinfoResponse);
    }
}
