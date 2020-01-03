package com.jqsoft.babyservice.controller.biz;

import com.jqsoft.babyservice.commons.interceptor.AdminCheck;
import com.jqsoft.babyservice.commons.vo.RestVo;
import com.jqsoft.babyservice.controller.system.BaseController;
import com.jqsoft.babyservice.entity.biz.UserInfo;
import com.jqsoft.babyservice.service.biz.LoginService;
import com.jqsoft.babyservice.service.biz.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController extends BaseController {
    @Autowired
    private UserService userService;

    @Autowired
    private LoginService loginService;

    @AdminCheck
    @RequestMapping("/list")
    public RestVo selectList(int currentPageIndex, int pageSize) {
        HashMap<String, Object> params = new HashMap<String, Object>() {{
            put("currentPageIndex", currentPageIndex);
            put("pageSize", pageSize);
        }};
        return null;
    }

    /**
     * 查询当前用户是否绑定了手机号码
     */
    @RequestMapping("/updateUserInfo")
    public RestVo updateUserInfo(@RequestParam(value = "mobile") String mobile, @RequestParam(value = "sex") String sex) {
        String userId = loginService.getUserIdByMobile(mobile, this.getDdCorpid());
        String localhostUserId = this.getDdUserid();
        if (StringUtils.isNotBlank(userId) && userId.equalsIgnoreCase(localhostUserId)) {
            UserInfo userInfo = this.getUser();
            userInfo.setMobile(mobile);
            userInfo.setSex(Byte.decode(sex));
            userService.updateByPrimaryKeySelective(userInfo);
            return RestVo.SUCCESS("ok");
        } else {
            return RestVo.SUCCESS("您的填写的手机号不是钉钉登陆用户手机号，请重新填写！");
        }
    }


}
