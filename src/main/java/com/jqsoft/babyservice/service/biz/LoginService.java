package com.jqsoft.babyservice.service.biz;

import org.springframework.stereotype.Service;

import com.alibaba.druid.util.StringUtils;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiUserGetuserinfoRequest;
import com.dingtalk.api.response.OapiUserGetuserinfoResponse;
import com.taobao.api.ApiException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class LoginService {
	/**
	 * TODO 从钉钉接口获取userid
	 * @author	李健平2018072
	 * @date 2019年9月16日 上午10:34:47
	 * @param requestAuthCode 免登授权码
	 * @param accessToken 第三方企业凭证
	 * @return
	 * @throws ApiException
	 */
	public String getUserId(String requestAuthCode,String accessToken) throws ApiException{
		log.info("入参：requestAuthCode={}",requestAuthCode);
		log.info("入参：accessToken={}",accessToken);
		DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/user/getuserinfo");
		OapiUserGetuserinfoRequest request = new OapiUserGetuserinfoRequest();
		request.setCode(requestAuthCode);
		request.setHttpMethod("GET");
		OapiUserGetuserinfoResponse response = client.execute(request, accessToken);
		if(StringUtils.isEmpty(response.getUserid())){
			log.info("用户不存在！");
			return null;
		}
		log.info("获取userId成功:{}",response.getUserid());
		return response.getUserid();
	}
}
