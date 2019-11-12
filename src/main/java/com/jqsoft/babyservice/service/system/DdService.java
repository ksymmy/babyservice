package com.jqsoft.babyservice.service.system;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.dingtalk.api.response.OapiServiceGetAuthInfoResponse.Agent;
import com.dingtalk.api.response.OapiServiceGetAuthInfoResponse.AuthCorpInfo;
import com.dingtalk.api.response.OapiServiceGetAuthInfoResponse.AuthInfo;
import com.google.common.base.CaseFormat;
import com.google.common.base.Converter;
import com.jqsoft.babyservice.commons.constant.RedisKey;
import com.jqsoft.babyservice.commons.utils.CommUtils;
import com.jqsoft.babyservice.commons.utils.RedisUtils;
import com.jqsoft.babyservice.entity.biz.UserInfo;
import com.jqsoft.babyservice.entity.system.Dd;
import com.jqsoft.babyservice.mapper.system.DdMapper;
import com.jqsoft.babyservice.service.biz.OrgInfoService;
import com.jqsoft.babyservice.service.biz.ParticipantsCheckService;
import com.jqsoft.babyservice.service.biz.UserService;

import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;

@Slf4j
@Service
public class DdService {
	@Autowired
	private DdMapper ddMapper;
	@Autowired
	private OrgInfoService orgInfoService;
	@Autowired
	private UserService userService;
	@Autowired
	private RedisUtils redisUtils;
	@Resource
    private ParticipantsCheckService participantsCheckService;
	
	
	public void syncDdCloudData () {
		List<Dd> ddList = ddMapper.selectDdCloudData();
		if(!CommUtils.isNull(ddList)){
			for (Dd dd : ddList) {
				com.alibaba.fastjson.JSONObject bizData = JSON.parseObject(dd.getBizData());
				Map<String,Object> params = new HashMap<String,Object>();
				params.put("id", dd.getId());
				try {
					switch (dd.getBizType().toString()) {
					case "2"://套件票据
						log.info("套件票据suiteTicket:{}",bizData.getString("suiteTicket"));
						saveSuiteTicketInRedis(bizData.getString("suiteTicket"),dd.getId());
						break;
					case "4"://企业授权变更，包含授权、解除授权、授权变更。
						log.info("企业授权变更，包含授权、解除授权、授权变更");
						authorize (bizData,dd);
						break;
					case "7"://企业微应用变更，包含停用、启用、删除(删除保留授权)
						log.info("企业微应用变更，包含停用、启用、删除(删除保留授权)");
						break;
					case "17"://市场订单
						log.info("市场订单");
						break;
					}
					params.put("status", "1");
				} catch (Exception e) {
					log.error("同步失败，失败原因：{}",e.getMessage());
					params.put("status", "2");
				}
				try {
					ddMapper.updateStatusById(params);
				} catch (Exception e) {
					log.error(e.getMessage());
					return;
				}
			}
		}
		
		List<Dd> ddListMedium = ddMapper.selectDdCloudDataMedium();
		if (!CommUtils.isNull(ddListMedium)) {
			for (Dd dd : ddListMedium) {
				com.alibaba.fastjson.JSONObject bizData = JSON.parseObject(dd.getBizData());
				Map<String,Object> params = new HashMap<String,Object>();
				params.put("id", dd.getId());
				try {
					switch (dd.getBizType().toString()) {
					case "13"://企业用户变更，包含用户添加、修改、删除
						log.info("企业用户变更，包含用户添加、修改、删除");
						syncUserInfo (bizData,dd);
						break;
					case "14"://企业部门变更，包含部门添加、修改、删除
						log.info("企业部门变更，包含部门添加、修改、删除");
						break;
					case "15"://企业角色变更，包含角色添加、修改、删除
						log.info("企业角色变更，包含角色添加、修改、删除");
						break;
					case "16"://企业变更，包含企业修改、删除
						log.info("企业变更，包含企业修改、删除");
						break;
					case "20"://企业外部联系人变更，包含添加、修改、删除
						log.info("企业外部联系人变更，包含添加、修改、删除");
						break;
					case "22"://ISV自定义审批
						log.info("ISV自定义审批");
                        participantsCheckService.updateStateByProcessInstanceId(bizData);
                        break;
					case "25"://家校通信息变更
						log.info("家校通信息变更");
						break;
					}
					params.put("status", "1");
				} catch (Exception e) {
					log.error("同步失败，失败原因：{}",e.getMessage());
					params.put("status", "2");
				}
				try {
					ddMapper.updateStatusByMediumId(params);
				} catch (Exception e) {
					log.error(e.getMessage());
					return;
				}
			}
		}
	}
	
	/**
	 * TODO 把推送来的suiteTicket保存在redis中
	 * @author	李健平2018072
	 * @date 2019年10月22日 下午3:58:56
	 * @param suiteTicket
	 */
	private void saveSuiteTicketInRedis (String suiteTicket,BigInteger id) {
		redisUtils.add(RedisKey.SUITE_TICKET.getKey("suite_ticket"),suiteTicket);
		return;
	}
	
	/**
	 * TODO 企业授权变更，包含授权、解除授权、授权变更。
	 * @author	李健平2018072
	 * @date 2019年10月23日 上午8:49:03
	 * @param syncAction
	 */
	private void authorize (com.alibaba.fastjson.JSONObject bizData,Dd dd) {
		//由于推送的json字符串中的的字段与实体类中的字段不匹配，导致后面json对象转实体类报错。
		//解决办法：把json字符串中的下划线转驼峰，例如：corp_name -> corpName
		Converter<String, String> converter = CaseFormat.LOWER_UNDERSCORE.converterTo(CaseFormat.LOWER_CAMEL);
		JSONObject authCorpInfoJsonObject = JSONObject.fromObject(converter.convert(bizData.getString("auth_corp_info")));
		JSONObject authInfoJsonObject = JSONObject.fromObject(converter.convert(bizData.getString("auth_info")));
		Map<String,Class> classMap = new HashMap<String,Class>();
		classMap.put("agent", Agent.class);
		AuthCorpInfo authCorpInfo = (AuthCorpInfo)JSONObject.toBean(authCorpInfoJsonObject,AuthCorpInfo.class);
		AuthInfo authInfo = (AuthInfo)JSONObject.toBean(authInfoJsonObject,AuthInfo.class,classMap);
		log.info("authCorpInfo:{}",JSON.toJSONString(authCorpInfo));
		log.info("authInfo:{}",JSON.toJSONString(authInfo));
		if ("org_suite_auth".equals(bizData.getString("syncAction"))) {	//表示企业授权套件
			log.info("org_suite_auth");
			orgInfoService.insertOrginfo(authCorpInfo, authInfo);
		}else if ("org_suite_change".equals(bizData.getString("syncAction"))) {	//表示企业变更授权范围
			log.info("org_suite_change");
		}else if ("org_suite_relieve".equals(bizData.getString("syncAction"))) {		//表示企业解除授权
			log.info("org_suite_relieve");
			if(StringUtils.isNotBlank(RedisKey.LOGIN_CORPACCESSTOKEN.getKey(dd.getCorpId()))){
				redisUtils.remove(RedisKey.LOGIN_CORPACCESSTOKEN.getKey(dd.getCorpId()));
			}
		}
	}
	
	/**
	 * TODO 同步企业用户信息
	 * @author	李健平2018072
	 * @date 2019年10月25日 下午3:27:09
	 * @param bizData
	 * @param id
	 */
	private void syncUserInfo (com.alibaba.fastjson.JSONObject bizData,Dd dd) throws Exception{
		UserInfo userInfo = initUserInfo(bizData, dd);
		if ("user_add_org".equals(bizData.getString("syncAction"))) {
			userInfo.setId(CommUtils.getUUID());
			userService.insertUserInfo(userInfo);
		}else if ("user_modify_org".equals(bizData.getString("syncAction"))) {
			userService.updateByUseridSelective(userInfo);
		}else if ("user_dept_change".equals(bizData.getString("syncAction"))) {
			userService.updateByUseridSelective(userInfo);
		}else if ("user_role_change".equals(bizData.getString("syncAction"))) {
			userService.updateByUseridSelective(userInfo);
		}else if ("user_active_org".equals(bizData.getString("syncAction"))) {
			userService.updateByUseridSelective(userInfo);
		}else if ("user_leave_org".equals(bizData.getString("syncAction"))) {
			userService.deleteUserByUserid(dd.getBizId());
		}
	}
	
	/**
	 * TODO 初始化userInfo
	 * @author	李健平2018072
	 * @date 2019年10月28日 上午8:54:32
	 * @param bizData
	 * @param dd
	 * @return
	 */
	private UserInfo initUserInfo (com.alibaba.fastjson.JSONObject bizData,Dd dd) {
		UserInfo userInfo = new UserInfo();
		userInfo.setOrgId(orgInfoService.selectByCorpId(dd.getCorpId()).getId());
		userInfo.setCorpId(dd.getCorpId());
		userInfo.setUserid(bizData.getString("userid"));
		userInfo.setUnionid(bizData.getString("unionid"));
		userInfo.setName(bizData.getString("name"));
		userInfo.setActive(Boolean.valueOf(bizData.getString("active"))?(byte)1:(byte)0);
		userInfo.setOrderInDepts(bizData.getString("orderInDepts"));
		userInfo.setIsadmin(Boolean.valueOf(bizData.getString("isAdmin"))?(byte)1:(byte)0);
		userInfo.setIsboss(Boolean.valueOf(bizData.getString("isBoss"))?(byte)1:(byte)0);
		userInfo.setIshide(Boolean.valueOf(bizData.getString("isHide"))?(byte)1:(byte)0);
		userInfo.setIsleader(bizData.getString("isLeaderInDepts"));
		userInfo.setDepartment(bizData.getString("department"));
		userInfo.setPosition(bizData.getString("position"));
		userInfo.setAvatar(bizData.getString("avatar"));
//		userInfo.setJobnumber(bizData.getString("jobnumber"));
//		userInfo.setHireddate(DateUtil.parseDate(bizData.getString("hireddate"), "yyyy-MM-dd HH:mm:ss"));
        return userInfo;
    }
}
