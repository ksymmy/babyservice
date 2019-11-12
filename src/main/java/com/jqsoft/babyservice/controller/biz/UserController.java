package com.jqsoft.babyservice.controller.biz;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jqsoft.babyservice.commons.interceptor.AuthCheck;
import com.jqsoft.babyservice.commons.vo.RestVo;
import com.jqsoft.babyservice.controller.system.BaseController;
import com.jqsoft.babyservice.entity.biz.UserInfo;
import com.jqsoft.babyservice.service.biz.OrgInfoService;
import com.jqsoft.babyservice.service.biz.UserRoleService;
import com.jqsoft.babyservice.service.biz.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("user")
public class UserController extends BaseController {
    @Autowired
    private UserService userService;
    @Autowired
    private OrgInfoService orgInfoService;
    @Autowired
    private UserRoleService userRoleService;

    @AuthCheck
    @RequestMapping("/list")
    public RestVo selectList(int currentPageIndex, int pageSize) {
        String orgId = this.getOrgId();
        HashMap<String, Object> params = new HashMap<String, Object>() {{
            put("orgId", orgId);
            put("currentPageIndex", currentPageIndex);
            put("pageSize", pageSize);
        }};
        RestVo restVo = userService.selectUserList(params);
        return restVo;
    }

    @AuthCheck
    @RequestMapping("/updateStatusById")
    public RestVo updateStatusById(String id) {
        log.info(String.valueOf(id));
        UserInfo entity = new UserInfo();
        entity.setId(id);
        UserInfo search = userService.selectUserById(id);
        if (search.getStatus() == 1) {
            Byte a = 3;
            entity.setStatus(a);
        } else {
            Byte a = 1;
            entity.setStatus(a);
        }
        Timestamp d = new Timestamp(System.currentTimeMillis());
        entity.setUpdateTime(d);
        RestVo restVo = userService.updateStatusById(entity);
        return restVo;
    }

    @AuthCheck
    @RequestMapping("/deleteByid")
    public RestVo deleteByid(String id) {
        userService.deleteUserInfo(id);
        return RestVo.SUCCESS();
    }


    @AuthCheck
    @RequestMapping("/listForStatus")
    public RestVo selectUserListForStatus(int currentPageIndex, int pageSize) {
        String orgId = this.getOrgId();
        HashMap<String, Object> params = new HashMap<String, Object>() {{
            put("orgId", orgId);
            put("currentPageIndex", currentPageIndex);
            put("pageSize", pageSize);
        }};
        RestVo restVo = userService.selectUserListForStatus(params);
        return restVo;
    }

    @AuthCheck
    @PostMapping("/updateStatusAsRole")
    public RestVo updateStatusAsRole(@RequestBody UserInfo userInfo) {
        log.info(String.valueOf(userInfo));
        RestVo restVo = userService.updateUserStatusAsRole(userInfo);
        return restVo;
    }

    @AuthCheck
    @RequestMapping("/selectUserInfo")
    public RestVo selectUserInfo(String id) {
        log.info(String.valueOf(id));
        RestVo restVo = userService.selectUserInfo(id);
        log.info(String.valueOf(restVo));
        return restVo;
    }

    /**
     * TODO 重新提交审核（把审核状态置为0，待审核）
     *
     * @return
     * @author 李健平2018072
     * @date 2019年9月24日 上午11:35:17
     */
    @RequestMapping("/reApprove")
    public RestVo reApprove() {
    	log.info("=====reApprove=====");
        UserInfo userInfo = new UserInfo();
        userInfo.setId(this.getUserId());
        userInfo.setStatus((byte) 0);
        userService.updateStatusById(userInfo);
        return RestVo.SUCCESS();
    }
  
    @RequestMapping("/selectUserInfoByToken")
    public RestVo selectUserInfoByToken(){
    	log.info("===selectUserInfoByToken===");
    	UserInfo userInfo = null;
    	Map<String,Object> map = new HashMap<String,Object>();
    	if(StringUtils.isNotBlank(this.getToken())){
    		if(StringUtils.isNotBlank(this.getUserId())){
    			userInfo = (UserInfo)userService.selectUserInfo(this.getUserId()).getData();
    			map.put("userInfo", userInfo);
//    			if(null != userInfo.getUserType()){
//    				if(userInfo.getUserType() == (byte)2 || userInfo.getUserType() == (byte)3){	//如果是个人会员或者是单位会员
//    					map.put("isPerfectPersonalInfo", userService.isPerfectPersonalInfo(userInfo.getId(),userInfo.getUserType()))  ;
//    				}else if(userInfo.getUserType() == (byte)0){
//    					map.put("isPerfectCorpInfo", orgInfoService.isPerfectCorpInfo(userInfo.getCorpId()));
//    				}
//    			}
    			//【测试阶段】条件修改成以下，线上可能要改回上面的条件
    			if(null != userInfo.getCurUserType()){
					if(userInfo.getCurUserType() == (byte)2 || userInfo.getCurUserType() == (byte)3){	//如果是个人会员或者是单位会员
						map.put("isPerfectPersonalInfo", userService.isPerfectPersonalInfo(userInfo.getId(),userInfo.getCurUserType()))  ;
					}else if(userInfo.getCurUserType() == (byte)0){
						map.put("isPerfectCorpInfo", orgInfoService.isPerfectCorpInfo(userInfo.getCorpId()));
					}
    			}	
    		}
    	}
    	log.info("selectUserInfoByToken出参：{}",map);
    	return RestVo.SUCCESS(map);
    }
    
    @RequestMapping("/switchRole")
    public RestVo<String> switchRole (String curUserType) {
    	userService.updateCurUserType(this.getUserId(),curUserType);
    	userRoleService.updateRoleIdByUserIdAndCurUserType(this.getUserId(), curUserType);
    	return RestVo.SUCCESS();
    }
}
