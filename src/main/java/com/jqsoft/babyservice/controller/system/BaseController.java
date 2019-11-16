package com.jqsoft.babyservice.controller.system;

import com.jqsoft.babyservice.entity.biz.UserInfo;
import com.jqsoft.babyservice.service.biz.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;

@Slf4j
public abstract class BaseController {

    private static String corpidParamName = "corpid";
    private static String useridParamName = "userid";

    @Autowired(required = false)
    protected HttpServletRequest request;

    @Autowired
    UserService userService;

    /**
     * 获取钉钉corpid
     *
     * @return
     */
    public String getDdCorpid() {
        String corpid = request.getHeader(corpidParamName);
        if (StringUtils.isBlank(corpid)) {
            corpid = request.getParameter(corpidParamName);
        }
        return corpid;
    }

    /**
     * 获取钉钉userid
     *
     * @return
     */
    public String getDdUserid() {
        String userid = request.getHeader(useridParamName);
        if (StringUtils.isBlank(userid)) {
            userid = request.getParameter(useridParamName);
        }
        return userid;
    }

    /**
     * 获取当前用户
     *
     * @return
     */
    public UserInfo getUser() {
        return userService.selectByCorpIdAndUserid(this.getDdCorpid(), this.getDdUserid());
    }


    /**
     * 获取当前用户ID
     * @return
     */
    public Long getUserId(){
        return this.getUser().getId();
    }
}