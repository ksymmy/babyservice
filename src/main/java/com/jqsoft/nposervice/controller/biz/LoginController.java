package com.jqsoft.nposervice.controller.biz;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dingtalk.api.response.OapiUserGetResponse;
import com.jqsoft.nposervice.commons.constant.RedisKey;
import com.jqsoft.nposervice.commons.constant.ResultMsg;
import com.jqsoft.nposervice.commons.utils.CommUtils;
import com.jqsoft.nposervice.commons.utils.RedisUtils;
import com.jqsoft.nposervice.commons.vo.RestVo;
import com.jqsoft.nposervice.controller.dd.DdCallBack;
import com.jqsoft.nposervice.controller.system.BaseController;
import com.jqsoft.nposervice.entity.biz.UserInfo;
import com.jqsoft.nposervice.service.biz.LoginService;
import com.jqsoft.nposervice.service.biz.OrgInfoService;
import com.jqsoft.nposervice.service.biz.UserService;
import com.taobao.api.ApiException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("loginApi")
public class LoginController extends BaseController{
	@Autowired
	private LoginService loginService;
	@Autowired
	private UserService userService;
	@Autowired
	private OrgInfoService orgInfoService;
	@Autowired
	private DdCallBack callBack;
	@Autowired
	private RedisUtils redisUtils;
	
	
	/**
	 * TODO 登录
	 * @author	李健平2018072
	 * @date 2019年9月23日 下午2:28:21
	 * @param requestAuthCode 免登授权码
	 * @param corpId 从钉钉获取corpId
	 * @return
	 */
	@RequestMapping("login")
	public RestVo login(@RequestParam(value="authCode",required = false) String requestAuthCode,
								@RequestParam(value="corpId",required = false) String corpId){
		log.info("入参:临时授权码:{}",requestAuthCode);
		log.info("用户token：{}",this.getToken());
		log.info("授权企业id:{}",corpId);
		
		Map<String,Object> dataMap = new HashMap<>();
//		String token = this.getToken();
//		String userId = this.getUserId();
//		if(StringUtils.isNotBlank(token) && StringUtils.isNotBlank(userId) 
//				&& redisUtils.exists(RedisKey.LOGIN_TOKEN.getKey(userId))
//				&& corpId.equals(corpIdFromStorage)){
//			UserInfo userInfo = this.getUserInfo();
//			if (null != userInfo) {
//				dataMap = new HashMap<>();
//				dataMap.put("token", token);
//				dataMap.put("userType", userInfo.getUserType());
//				dataMap.put("status", userInfo.getStatus());
//				dataMap.put("corpId",userInfo.getCorpId());
//				if(null != userInfo.getUserType()){
//					if(userInfo.getUserType() == 2){
//						dataMap.put("isPerfectPersonalInfo", userService.isPerfectPersonalInfo(userInfo.getId()))  ;
//					}else if(userInfo.getUserType() == 0 || userInfo.getUserType() == 3){
//						dataMap.put("isPerfectCorpInfo", orgInfoService.isPerfectCorpInfo(corpId));
//					}
//				}
//				return RestVo.SUCCESS(dataMap);
//			} else {
//				this.removeUserOrToken(userId, token);
//			}
//		} else {
//			this.removeUserOrToken(userId, token);
//		}
		
		//查询本地数据库中是否有该用户的企业信息，如果没有则调钉钉接口获取并插入数据库
		//原因：用户在第一次开通应用后，企业信息未及时同步到本地数据库（定时同步任务暂定五分钟执行一次），需要直接调接口获取企业信息
		if (CommUtils.isNull(orgInfoService.selectByCorpId(corpId))) {
			try {
				callBack.getAuthCorpInfo(corpId);
			} catch (ApiException e) {
				e.printStackTrace();
				return RestVo.FAIL(ResultMsg.DD_GETCORPINFO_FAIL);
			}
		}

		String corpAccessToken = (String) redisUtils.get(RedisKey.LOGIN_CORPACCESSTOKEN.getKey(corpId));
		if (StringUtils.isBlank(corpAccessToken)){
			corpAccessToken = callBack.getCorpAccessToken(corpId).getAccessToken();
			//把corpAccessToken放入缓存中,设置失效时间110分钟（钉钉设置corpAccessToken的失效时间为120分钟），如果缓存中有就重置失效时间
			redisUtils.add(RedisKey.LOGIN_CORPACCESSTOKEN.getKey(corpId), corpAccessToken,110,TimeUnit.MINUTES);
		}
		String userid = null;
		try {
			//从钉钉获取的userid
			userid = loginService.getUserId(requestAuthCode, corpAccessToken);
		} catch (ApiException e1) {
			e1.printStackTrace();
			log.error("从钉钉获取的userid失败，失败原因：{}",e1);
			return RestVo.FAIL(ResultMsg.DD_GETUSERID_FAIL);
		}
		if(StringUtils.isBlank(userid)){
			return RestVo.FAIL(ResultMsg.DD_GETUSERID_FAIL);
		}
		
		UserInfo info = new UserInfo();
		info.setUserid(userid);
		info.setCorpId(corpId);
		UserInfo uInfo = userService.selectUserInfo(info).getData();
		if(CommUtils.isNull(uInfo)){
			RestVo rsp = null;
			try {
				//调钉钉接口拉取用户信息
				rsp = userService.getUserInfoFromDd(userid, corpAccessToken);
				OapiUserGetResponse oapiUserGetResponse = (OapiUserGetResponse) rsp.getData();
				uInfo = new UserInfo();
				//随机生成不重复的字符串作为用户id，主键
				uInfo.setId(CommUtils.getUUID());
				uInfo.setOrgId(orgInfoService.selectByCorpId(corpId).getId());
				uInfo.setCorpId(corpId);
				uInfo.setUserid(oapiUserGetResponse.getUserid());
				uInfo.setUnionid(oapiUserGetResponse.getUnionid());
				uInfo.setName(oapiUserGetResponse.getName());
				uInfo.setActive(oapiUserGetResponse.getActive()?(byte)1:(byte)0);
				uInfo.setOrderInDepts(oapiUserGetResponse.getOrderInDepts());
				uInfo.setIsadmin(oapiUserGetResponse.getIsAdmin()?(byte)1:(byte)0);
				uInfo.setIsboss(oapiUserGetResponse.getIsBoss()?(byte)1:(byte)0);
				uInfo.setIshide(oapiUserGetResponse.getIsHide()?(byte)1:(byte)0);
				uInfo.setIsleader(oapiUserGetResponse.getIsLeaderInDepts());
				uInfo.setDepartment(oapiUserGetResponse.getDepartment().toString());
				uInfo.setPosition(oapiUserGetResponse.getPosition());
				uInfo.setAvatar(oapiUserGetResponse.getAvatar());
				uInfo.setJobnumber(oapiUserGetResponse.getJobnumber());
				uInfo.setHireddate(oapiUserGetResponse.getHiredDate());
				//入库
				userService.insertUserInfo(uInfo);
			} catch (ApiException e) {
				log.error("调钉钉接口获取用户信息失败，失败原因：{}",e);
				return RestVo.FAIL(ResultMsg.DD_GETUSERINFO_FAIL);
			}
		}
		
		String newToken = (String)redisUtils.get(RedisKey.LOGIN_TOKEN.getKey(uInfo.getId()));
		String newUserId = (String)redisUtils.get(RedisKey.LOGIN_USERID.getKey(newToken));
		if(StringUtils.isBlank(newToken) || StringUtils.isBlank(newUserId)){
			this.removeUserOrToken(newUserId, newToken);
			//生成userToken的key
			String tokenKey = RedisKey.LOGIN_TOKEN.getKey(uInfo.getId());
			log.info("tokenKey:{}",tokenKey);
			//生成新的用户token
			newToken = CommUtils.getUUID();
			//生成id的key
			String idKey = RedisKey.LOGIN_USERID.getKey(newToken);
			redisUtils.add(tokenKey, newToken);
			redisUtils.add(idKey, uInfo.getId());
		}
		dataMap.put("token", newToken);
		dataMap.put("status", uInfo.getStatus());
		dataMap.put("userType", uInfo.getUserType());
		dataMap.put("corpId",uInfo.getCorpId());
		//【测试阶段】后期可能删除
		dataMap.put("curUserType", uInfo.getCurUserType());
		
		log.info("返回参数：{}",dataMap);
		return RestVo.SUCCESS(dataMap);
	}
	
	public void removeUserOrToken(String userId, String token){
		if (StringUtils.isNotBlank(userId)) {
			redisUtils.remove(RedisKey.LOGIN_TOKEN.getKey(userId));
		}
		if (StringUtils.isNotBlank(token)) {
			redisUtils.remove(RedisKey.LOGIN_USERID.getKey(token));
		}
	}
}
