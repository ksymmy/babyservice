package com.jqsoft.nposervice.service.biz;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiUserGetRequest;
import com.dingtalk.api.response.OapiUserGetResponse;
import com.jqsoft.nposervice.commons.constant.RedisKey;
import com.jqsoft.nposervice.commons.utils.CommUtils;
import com.jqsoft.nposervice.commons.utils.RedisUtils;
import com.jqsoft.nposervice.commons.vo.RestVo;
import com.jqsoft.nposervice.entity.biz.MeetingInfo;
import com.jqsoft.nposervice.entity.biz.UserInfo;
import com.jqsoft.nposervice.entity.system.UserRole;
import com.jqsoft.nposervice.mapper.biz.MeetingInfoMapper;
import com.jqsoft.nposervice.mapper.biz.UserInfoMapper;
import com.jqsoft.nposervice.mapper.system.UserRoleMapper;
import com.taobao.api.ApiException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserService {
    @Autowired(required = false)
    private UserInfoMapper userInfoMapper;
    @Autowired(required = false)
    private UserRoleMapper userRoleMapper;
    @Resource
    private MeetingInfoMapper meetingInfoMapper;
    @Resource
    private RedisUtils redisUtils;

    public RestVo<UserInfo> selectUserInfo(UserInfo userInfo) {
        return RestVo.SUCCESS(userInfoMapper.selectUserInfo(userInfo));
    }

    public RestVo getUserInfoFromDd(String userId, String corpAccessToken) throws ApiException {
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/user/get");
        OapiUserGetRequest request = new OapiUserGetRequest();
        request.setUserid(userId);
        request.setHttpMethod("GET");
        OapiUserGetResponse response = client.execute(request, corpAccessToken);
        return RestVo.SUCCESS(response);
    }

    /**
     * TODO 新增用户
     * @param userInfo 大部分是从钉钉接口拉取的用户信息，其中id是user表主键，随机数
     * @return
     * @author 李健平2018072
     * @date 2019年9月18日 上午10:49:21
     */
    @Transactional(rollbackFor = Exception.class)
    public RestVo insertUserInfo(UserInfo userInfo) {
        log.info("========UserService>>>insertUserInfo=======");
        //如果是管理员，把用户类型user_type置为0（管理员）,用户状态置为1（审核通过）
        if (userInfo.getIsadmin() == 1) {
            userInfo.setUserType((byte) 0);
            userInfo.setStatus((byte) 1);
            
            //【测试阶段】把当前用户类型置为0（管理员），后期上线可能需要删除
            userInfo.setCurUserType((byte)0);
            
            UserRole userRole = new UserRole();
            userRole.setId(CommUtils.getUUID());
            userRole.setUserId(userInfo.getId());
            //组织管理员
            userRole.setRoleId("61af6388d38142bd9baa0362c1c2b9fc");
            //往t_user_role表中插入数据
            userRoleMapper.insertUserRole(userRole);
        }
        //往biz_user_info表中插入数据
        userInfoMapper.insertSelective(userInfo);
        return RestVo.SUCCESS();
    }

    /**
     * TODO 个人信息或者企业信息是否完善
     * @param userId 用户id
     * @return true:已完善；false:未完善
     * @author 李健平2018072
     * @date 2019年9月18日 下午4:03:53
     */
    public boolean isPerfectPersonalInfo(String userId,byte userType) {
        UserInfo info = new UserInfo();
        info.setId(userId);
        UserInfo userInfo = userInfoMapper.selectUserInfo(info);
        if(userType == (byte)2){
            //个人会员基本信息维护页面必填项
            if (StringUtils.isEmpty(userInfo.getName()) || StringUtils.isEmpty(userInfo.getIdtype())
                    || StringUtils.isEmpty(userInfo.getIdnumber()) || StringUtils.isEmpty(userInfo.getPhome())
                    || StringUtils.isEmpty(userInfo.getEmail())) {
                return false;
            }
        }else if(userType == (byte)3){
            //企业会员基本信息维护页面必填项
            if(StringUtils.isEmpty(userInfo.getCreditCode()) || StringUtils.isEmpty(userInfo.getEnterpriseName()) ||
                    StringUtils.isEmpty(userInfo.getOrgType()) || StringUtils.isEmpty(userInfo.getIndustry()) ||
                    StringUtils.isEmpty(userInfo.getProvinceId()) || StringUtils.isEmpty(userInfo.getCityId()) ||
                    StringUtils.isEmpty(userInfo.getDetail()) || StringUtils.isEmpty(userInfo.getEstablishDate().toString()) ||
                    StringUtils.isEmpty(userInfo.getEnterprisePhone()) || StringUtils.isEmpty(userInfo.getEnterpriseEmail()) ){
                return false;
            }
        }
        return true;
    }


    public RestVo selectUserList(Map<String, Object> params) {
        List<UserInfo> data = userInfoMapper.selectUserList(params);
        return RestVo.SUCCESS(data);
    }

    public RestVo updateStatusById(UserInfo userInfo) {
        Timestamp d = new Timestamp(System.currentTimeMillis());
        userInfo.setUpdateTime(d);
        userInfoMapper.updateStatusById(userInfo);
        return RestVo.SUCCESS();
    }

    public RestVo deleteUserInfo(String id) {
        userInfoMapper.deleteByPrimaryKey(id);
        return RestVo.SUCCESS();
    }

    public UserInfo selectUserById(String id) {
        return userInfoMapper.selectByPrimaryKey(id);
    }

    public RestVo selectUserListForStatus(Map<String, Object> params) {
        List<UserInfo> data = userInfoMapper.selectUserListForStatus(params);
        return RestVo.SUCCESS(data);
    }

    @Transactional(rollbackFor = Exception.class)
    public RestVo updateUserStatusAsRole(UserInfo userInfo) {
        Timestamp d = new Timestamp(System.currentTimeMillis());
        userInfo.setUpdateTime(d);
        if (userInfo.getStatus() == 2) {//当审核状态为不通过时，人员类别、会员职位为空
            userInfo.setUserType(null);
            userInfo.setJob(null);
        } else if (userInfo.getStatus() == 1 && userInfo.getUserType() == 1) {//当审核状态为通过时且人员类别为工作人员时会员职位为空
            userInfo.setJob(null);
        }
        userInfo.setCurUserType(userInfo.getUserType());
        userInfoMapper.updateStatusById(userInfo);
        //如果审核不过则不增加用户角色
        if (userInfo.getUserType() != null) {
            UserRole userRole = new UserRole();
            userRole.setId(CommUtils.getUUID());
            userRole.setUserId(userInfo.getId());

            // 1：工作人员  2：个人会员 3：单位会员

            if (userInfo.getUserType() == 1) {
                userRole.setRoleId("61af6388d38142bd9baa0362c1c2b9fc");//组织管理人员
            } else {
                userRole.setRoleId("023d8fc384c94377b95d3da6c74d5260");//组织会员
            }
            userRoleMapper.insertUserRole(userRole);
        }
        return RestVo.SUCCESS();
    }

    public RestVo selectUserInfo(String id) {
        UserInfo info = userInfoMapper.selectByPrimaryKey(id);
        return RestVo.SUCCESS(info);
    }

    public List<UserRole> selectUserRoleByUserId(String userId) {
        return userRoleMapper.selectUserRoleByUserId(userId);
    }

    public void updateCurUserType (String userId,String curUserType) {
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(curUserType)) {
            return;
        }
        Map<String,Object> params = new HashMap<String, Object>();
        params.put("id", userId);
        params.put("curUserType", curUserType);
        userInfoMapper.updateCurUserType(params);
        return;
    }

    /**
     * 获取审批人信息
     * @param orgId
     * @return
     */
    public List<UserInfo> queryUserByOrgID(String orgId,String userId) {
        return   userInfoMapper.queryUserByOrgID(orgId,userId);
    }

    /**
     * 获取MeetingInfo
     * @param meetingId
     * @return
     */
    public MeetingInfo queryByID(String meetingId) {
        String key = RedisKey.MEETING_INFO.getKey(meetingId);
        MeetingInfo meetingInfo = null;//(MeetingInfo) redisUtils.get(key);
        if (null == meetingInfo) {
            meetingInfo = meetingInfoMapper.selectByPrimaryKey(meetingId);
            //redisUtils.add(key, meetingInfo);
        }
        return meetingInfo;
    }
    
    /**
     * TODO 更新用户信息
     * @author	李健平2018072
     * @date 2019年10月28日 下午4:20:19
     * @param userInfo
     * @return
     */
    public int updateByUseridSelective (UserInfo userInfo) {
    	UserInfo uInfo = new UserInfo();
    	uInfo.setUserid(userInfo.getUserid());
    	//查询已有的用户信息
    	UserInfo info = userInfoMapper.selectUserInfo(uInfo);
    	//如果用户角色改变了（通过对比已有的用户信息中的isadmin字段和钉钉推送的用户信息中的isadmin字段是否相同来判断）
    	if (info.getIsadmin() != userInfo.getIsadmin()) {
    		if (userInfo.getIsadmin() == 1) {
        		userInfo.setStatus((byte)1);
        		userInfo.setUserType((byte)0);
        		userInfo.setCurUserType((byte)0);
        	}else {
        		userInfo.setStatus((byte)0);
        		userInfo.setUserType(null);
        		userInfo.setCurUserType(null);
        	}
    	}
    	return userInfoMapper.updateByUseridSelective(userInfo);
    }
    
    /**
     * TODO 删除用户
     * @author	李健平2018072
     * @date 2019年10月30日 上午8:47:26
     * @param userid
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteUserByUserid (String userid) {
    	UserInfo userInfo = new UserInfo();
    	userInfo.setUserid(userid);
    	userInfoMapper.deleteUserByUserid(userid);
    	if(!CommUtils.isNull(userInfoMapper.selectUserInfo(userInfo))) {
        	userRoleMapper.deleteUserRoleByUserId(userInfoMapper.selectUserInfo(userInfo).getId());
    	}
    	return 0;
    }
}

