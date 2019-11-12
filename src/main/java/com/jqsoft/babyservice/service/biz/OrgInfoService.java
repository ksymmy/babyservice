package com.jqsoft.babyservice.service.biz;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.dingtalk.api.response.OapiServiceGetAuthInfoResponse.Agent;
import com.dingtalk.api.response.OapiServiceGetAuthInfoResponse.AuthCorpInfo;
import com.dingtalk.api.response.OapiServiceGetAuthInfoResponse.AuthInfo;
import com.jqsoft.babyservice.commons.utils.CommUtils;
import com.jqsoft.babyservice.commons.vo.RestVo;
import com.jqsoft.babyservice.entity.biz.OrgInfo;
import com.jqsoft.babyservice.mapper.biz.OrgInfoMapper;
import com.jqsoft.babyservice.service.system.AreaService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OrgInfoService {

    @Autowired(required = false)
    private OrgInfoMapper orgInfoMapper;

    @Autowired
    private AreaService areaService;
    
    @Value("${env.appid}")
	private String appid;

	/**
	 * TODO 社会组织信息入库
	 * @author	李健平2018072
	 * @date 2019年9月18日 下午4:20:42
	 * @param orgInfo
	 * @return
	 */
	public RestVo insertOrginfo(AuthCorpInfo authCorpInfo,AuthInfo authInfo){
		log.info("OrgInfoService/insertOrginfo入参，authCorpInfo:{}",JSON.toJSONString(authCorpInfo));
		log.info("OrgInfoService/insertOrginfo入参，authInfo:{}",JSON.toJSONString(authInfo));
		OrgInfo record = new OrgInfo();
		record.setCorpid(authCorpInfo.getCorpid());
		record.setCorpLogoUrl(authCorpInfo.getCorpLogoUrl());
		record.setCorpName(authCorpInfo.getCorpName());
		record.setIndustry(authCorpInfo.getIndustry());
		record.setIsAuthenticated(authCorpInfo.getIsAuthenticated());
		record.setAuthLevel(authCorpInfo.getAuthLevel());
		record.setUpdateTime(new Timestamp(System.currentTimeMillis()));
		List<Agent> agentList = authInfo.getAgent();
		for (Agent agent : agentList) {
			if(appid.equals(agent.getAppid().toString())){
				record.setAgentId(agent.getAgentid().toString());
				break;
			}
		}
		OrgInfo info = orgInfoMapper.selectByCorpId(authCorpInfo.getCorpid());
		if(CommUtils.isNull(info) ){
			record.setId(CommUtils.getUUID());
			record.setCreateTime(new Timestamp(System.currentTimeMillis()));
			orgInfoMapper.insert(record);
		}else{
			record.setId(info.getId());
			orgInfoMapper.updateByPrimaryKeySelective(record);
		}
		return RestVo.SUCCESS();
	}

	/**
	 * TODO 查询社会组织信息是否完善
	 * @author	李健平2018072
	 * @date 2019年9月18日 下午4:25:16
	 * @param orgId
	 * @return
	 */
	public boolean isPerfectCorpInfo(String orgId){
		OrgInfo orgInfo = orgInfoMapper.selectByCorpId(orgId);
		//社会组织信息维护页面必填项
		if(StringUtils.isBlank(orgInfo.getCorpName()) || StringUtils.isBlank(orgInfo.getOrgType()) ||
				StringUtils.isBlank(orgInfo.getOrgTypeName()) || StringUtils.isBlank(orgInfo.getIndustryName()) ||
				StringUtils.isBlank(orgInfo.getIndustry()) || StringUtils.isBlank(orgInfo.getProvinceId()) ||
				StringUtils.isBlank(orgInfo.getCityId()) || StringUtils.isBlank(orgInfo.getCreditCode()) ||
				StringUtils.isBlank(orgInfo.getEstablishDate().toString()) ||
				StringUtils.isBlank(orgInfo.getPhone()) || StringUtils.isBlank(orgInfo.getEmail())){
			return false;
		}
		return true;
	}

	public RestVo selectOrgInfo(String id) {
		OrgInfo info = orgInfoMapper.selectByPrimaryKey(id);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		info.setEstablishDateStr(info.getEstablishDate() == null ? "" : simpleDateFormat.format(info.getEstablishDate()));
		return  RestVo.SUCCESS(info);
	}

	public RestVo saveOrUpdate(OrgInfo info) {
		String address = "";
		if (StringUtils.isNotBlank(info.getProvinceId())) {
			address += areaService.getAreaName(info.getProvinceId());
		}
		if (StringUtils.isNotBlank(info.getCityId())) {
			address += areaService.getAreaName(info.getCityId());
		}
		if (StringUtils.isNotBlank(info.getCountyId())) {
			address += areaService.getAreaName(info.getCountyId());
		}
		address += info.getDetail();
		info.setAddress(address);
		Timestamp d = new Timestamp(System.currentTimeMillis());
		info.setUpdateTime(d);
		orgInfoMapper.updateByPrimaryKeySelective(info);
		return RestVo.SUCCESS();
	}
	
	/**
	 * TODO 通过企业id获取企业信息
	 * @author	李健平2018072
	 * @date 2019年9月23日 上午9:36:35
	 * @param corpId
	 * @return
	 */
	public OrgInfo selectByCorpId(String corpId){
		return orgInfoMapper.selectByCorpId(corpId);
	}

}
