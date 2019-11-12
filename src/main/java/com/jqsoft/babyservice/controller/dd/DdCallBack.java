package com.jqsoft.babyservice.controller.dd;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiServiceActivateSuiteRequest;
import com.dingtalk.api.request.OapiServiceGetAuthInfoRequest;
import com.dingtalk.api.request.OapiServiceGetCorpTokenRequest;
import com.dingtalk.api.request.OapiServiceGetPermanentCodeRequest;
import com.dingtalk.api.request.OapiServiceGetSuiteTokenRequest;
import com.dingtalk.api.response.OapiServiceActivateSuiteResponse;
import com.dingtalk.api.response.OapiServiceGetAuthInfoResponse;
import com.dingtalk.api.response.OapiServiceGetCorpTokenResponse;
import com.dingtalk.api.response.OapiServiceGetPermanentCodeResponse;
import com.dingtalk.api.response.OapiServiceGetSuiteTokenResponse;
import com.dingtalk.oapi.lib.aes.DingTalkEncryptException;
import com.dingtalk.oapi.lib.aes.DingTalkEncryptor;
import com.dingtalk.oapi.lib.aes.Utils;
import com.jqsoft.babyservice.commons.constant.RedisKey;
import com.jqsoft.babyservice.commons.utils.RedisUtils;
import com.jqsoft.babyservice.commons.vo.RestVo;
import com.jqsoft.babyservice.service.biz.OrgInfoService;
import com.taobao.api.ApiException;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Controller
@RequestMapping("/ddCallBack")
public class DdCallBack {
	@Autowired
	private OrgInfoService orgInfoService;
	@Autowired
	private RedisUtils redisUtils;
//	@Value("${env.token}")
//	private String token;
//	@Value("${env.encoding_aes_key}")
//	private String encoding_aes_key;
	@Value("${env.suite_key}")
	private String suite_key;
	@Value("${env.suite_secret}")
	private String suite_secret;
	
	/*@RequestMapping(value = "/callBack", method = RequestMethod.POST)
	@ResponseBody
	private Map<String, String> callBack(@RequestParam(value = "signature", required = false) String signature,
			@RequestParam(value = "timestamp", required = false) String timeStamp,
			@RequestParam(value = "nonce", required = false) String nonce,
			@RequestBody(required = false) JSONObject json) {
		log.info("----------begin callback----------");
		log.info("入参：signature=" + signature + "," + "timestamp=" + timeStamp + "," + "nonce=" + nonce);
		log.info("json:{}",json);
		DingTalkEncryptor dingTalkEncryptor = null;
		String plainText;
		String encryptMsg;
		
		try {
			dingTalkEncryptor = new DingTalkEncryptor(token, encoding_aes_key, suite_key);
			// 从post请求的body中获取回调信息的加密数据进行解密处理
			encryptMsg = json.getString("encrypt");
			plainText = dingTalkEncryptor.getDecryptMsg(signature, timeStamp, nonce, encryptMsg);
			JSONObject obj = JSON.parseObject(plainText);
			// 根据回调数据类型做不同的业务处理
			String eventType = obj.getString("EventType");
			log.info("eventType:{}",eventType);
			if ("check_create_suite_url".equals(eventType)) {
				log.info("第一次设置回调地址检测推送: " + plainText);
			} else if ("check_update_suite_url".equals(eventType)) {
				log.info("更新回调地址检测推送: " + plainText);
			} else if ("suite_ticket".equals(eventType)) {
				log.info("套件Ticket数据推送: " + plainText);
				redisUtils.add(RedisKey.SUITE_TICKET.getKey("suite_ticket"),obj.getString("SuiteTicket"));
			} else if ("tmp_auth_code".equals(eventType)) {
				log.info("E应用企业开通数据推送通知: " + plainText);
				OapiServiceGetPermanentCodeResponse gpcrsp = getPermanentCcode(obj.getString("AuthCode"));
				//激活企业授权的应用
				activation(getSuiteAccessToken(), gpcrsp.getPermanentCode(), gpcrsp.getAuthCorpInfo().getCorpid());
				//获取授权企业详细信息并存入数据库
				getAuthCorpInfo(gpcrsp.getAuthCorpInfo().getCorpid());
			} else if("suite_relieve".equals(eventType)){
				log.info("解除授权："+plainText);
				if(StringUtils.isNotBlank(RedisKey.LOGIN_CORPACCESSTOKEN.getKey(obj.getString("AuthCorpId")))){
					redisUtils.remove(RedisKey.LOGIN_CORPACCESSTOKEN.getKey(obj.getString("AuthCorpId")));
				}
			}
			// 返回success的加密信息表示回调处理成功
			return dingTalkEncryptor.getEncryptedMap("success", System.currentTimeMillis(), Utils.getRandomStr(8));
		} catch (DingTalkEncryptException e) {
			e.printStackTrace();
			return null;
		} catch (ApiException ae) {
			ae.printStackTrace();
			return null;
		}
	}*/

	/**
	 * TODO 获取第三方应用凭证
	 * @author 李健平2018072
	 * @date 2019年9月12日 上午8:31:45
	 * @return
	 * @throws ApiException
	 */
	public String getSuiteAccessToken() throws ApiException {
		DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/service/get_suite_token");
		OapiServiceGetSuiteTokenRequest request = new OapiServiceGetSuiteTokenRequest();
		request.setSuiteKey(suite_key);
		request.setSuiteSecret(suite_secret);
		request.setSuiteTicket((String) redisUtils.get(RedisKey.SUITE_TICKET.getKey("suite_ticket")));
		OapiServiceGetSuiteTokenResponse response = client.execute(request);
		return response.getSuiteAccessToken();
	}

	/**
	 * TODO 获取永久授权码、授权方企业信息、授权方企业id、授权方企业名称
	 * @author	李健平2018072
	 * @date 2019年9月16日 下午5:00:40
	 * @param tmpAuthCode 临时授权码
	 * @return
	 * @throws ApiException
	 */
	public OapiServiceGetPermanentCodeResponse getPermanentCcode(String tmpAuthCode) throws ApiException {
		String suite_access_token = getSuiteAccessToken();
		DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/service/get_permanent_code?suite_access_token=" + suite_access_token);
		OapiServiceGetPermanentCodeRequest req = new OapiServiceGetPermanentCodeRequest();
		req.setTmpAuthCode(tmpAuthCode);
		OapiServiceGetPermanentCodeResponse rsp = null;
		try {
			rsp = client.execute(req);
			log.info(rsp.getBody());
		} catch (ApiException e) {
			e.printStackTrace();
		}
		return rsp;
	}

	/**
	 * TODO 激活企业授权的应用
	 * @author	李健平2018072
	 * @date 2019年9月16日 下午4:59:12
	 * @param suite_access_token 第三方企业凭证
	 * @param permanent_code 永久授权码
	 * @param corpId 授权企业id
	 */
	private void activation(String suite_access_token, String permanent_code, String corpId) {
		DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/service/activate_suite?suite_access_token=" + suite_access_token);
		OapiServiceActivateSuiteRequest req = new OapiServiceActivateSuiteRequest();
		req.setSuiteKey(suite_key);
		req.setAuthCorpid(corpId);
		req.setPermanentCode(permanent_code);
		OapiServiceActivateSuiteResponse rsp = null;
		try {
			rsp = client.execute(req);
			log.info(rsp.getBody());
			JSONObject json = JSON.parseObject(rsp.getBody());
			log.info("errcode=" + json.getString("errcode") + "," + "errmsg=" + json.getString("errmsg"));
		} catch (ApiException e) {
			e.printStackTrace();
		}
		return;
	}

	/**
	 * TODO 获取授权企业凭证接口
	 * 
	 * @author 李健平2018072
	 * @date 2019年9月11日 上午11:16:35
	 * @return
	 */
	@RequestMapping("getCorpAccessToken")
	public OapiServiceGetCorpTokenResponse getCorpAccessToken(String corpId) {
		DefaultDingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/service/get_corp_token");
		OapiServiceGetCorpTokenRequest req = new OapiServiceGetCorpTokenRequest();
		req.setAuthCorpid(corpId);
		OapiServiceGetCorpTokenResponse rsp = null ;
		try {
			rsp = client.execute(req, suite_key, suite_secret, (String) redisUtils.get(RedisKey.SUITE_TICKET.getKey("suite_ticket")));
			log.info(rsp.getBody());
		} catch (ApiException e) {
			e.printStackTrace();
		}
		return rsp;
	}

	/**
	 * TODO 获取企业授权信息接口
	 * @author 李健平2018072
	 * @date 2019年9月11日 上午11:16:35
	 * @return
	 * @throws ApiException
	 */
	@RequestMapping("getAuthCorpInfo")
	public RestVo<OapiServiceGetAuthInfoResponse> getAuthCorpInfo(String corpId) throws ApiException {
		DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/service/get_auth_info");
		OapiServiceGetAuthInfoRequest req = new OapiServiceGetAuthInfoRequest();
		req.setAuthCorpid(corpId);
		OapiServiceGetAuthInfoResponse rsp = client.execute(req, suite_key, suite_secret, (String) redisUtils.get(RedisKey.SUITE_TICKET.getKey("suite_ticket")));
		log.info("rsp:{}",JSON.toJSON(rsp));
		orgInfoService.insertOrginfo(rsp.getAuthCorpInfo(),rsp.getAuthInfo());
		return RestVo.SUCCESS(rsp);
	}
}
