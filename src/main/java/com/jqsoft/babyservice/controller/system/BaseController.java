package com.jqsoft.babyservice.controller.system;

import com.jqsoft.babyservice.commons.utils.RedisUtils;
import com.jqsoft.babyservice.commons.constant.RedisKey;
import com.jqsoft.babyservice.entity.biz.UserInfo;
import com.jqsoft.babyservice.service.biz.MemberUserInfoService;
import com.jqsoft.babyservice.service.biz.OrgInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public abstract class BaseController {

    private static String tokenHeader = "token";

    @Autowired(required = false)
    protected HttpServletRequest request;

    @Autowired(required = false)
    protected HttpServletResponse response;

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    MemberUserInfoService memberUserInfoService;

    @Autowired
    OrgInfoService orgInfoService;

    /**
     * 获取登录token
     * @return
     */
    public String getToken() {
        String token = null;
        if (StringUtils.isEmpty(token)) {
            token = request.getHeader(tokenHeader);
        }
        if (StringUtils.isEmpty(token)) {
            token = request.getParameter(tokenHeader);
        }
        return token;
    }

    /**
     * 获取用户ID
     * @return
     */
    public String getUserId() {
        String token = this.getToken();
        String userIdKey = RedisKey.LOGIN_USERID.getKey(token);
        String userIdString = (String) redisUtils.get(userIdKey);
        return userIdString;
    }

    /**
     * 获取用户所在的组织ID
     * @return
     */
    public String getOrgId() {
        UserInfo user = this.getUserInfo();
        return user.getOrgId();
    }

    /**
     * 获取用户登录信息
     *
     * @return
     */
    public UserInfo getUserInfo() {
        return memberUserInfoService.findByID(this.getUserId()).getData();
    }

}