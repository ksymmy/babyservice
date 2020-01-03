package com.jqsoft.babyservice.controller.biz;

import com.jqsoft.babyservice.commons.constant.RedisKey;
import com.jqsoft.babyservice.commons.utils.RedisUtils;
import com.jqsoft.babyservice.commons.vo.RestVo;
import com.jqsoft.babyservice.controller.system.BaseController;
import com.jqsoft.babyservice.entity.biz.UserInfo;
import com.jqsoft.babyservice.service.biz.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("/")
public class LoginController extends BaseController {
    @Resource
    private LoginService loginService;
    @Resource
    private RedisUtils redisUtils;

    /**
     * 登录
     *
     * @param authCode 免登授权码
     * @param corpid   从钉钉获取corpId
     * @param userid   userid
     */
    @RequestMapping("/login")
    public RestVo login(@RequestParam(value = "authCode") String authCode,
                        @RequestParam(value = "corpid") String corpid,
                        @RequestParam(value = "userid", required = false) String userid) {
        return loginService.login(authCode, corpid, userid);
    }

//    /**
//     * 根据手机号获取userid
//     */
//    @RequestMapping("/mobile")
//    public RestVo getUserIdByMobile() {
//        return loginService.getUserIdByMobile();
//    }

    /**
     * 查询当前用户是否绑定了手机号码
     */
    @RequestMapping("/vaildMobile")
    public RestVo vaildMobile() {
        UserInfo userInfo = this.getUser();
        if (StringUtils.isNotBlank(userInfo.getMobile())) {
            return RestVo.SUCCESS("ok");
        } else {
            return RestVo.SUCCESS("fail");
        }
    }

    public void removeUserOrToken(String userId, String token) {
        if (StringUtils.isNotBlank(userId)) {
            redisUtils.remove(RedisKey.LOGIN_TOKEN.getKey(userId));
        }
        if (StringUtils.isNotBlank(token)) {
            redisUtils.remove(RedisKey.LOGIN_USERID.getKey(token));
        }
    }
}
