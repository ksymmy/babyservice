package com.jqsoft.babyservice.controller.biz;

import com.jqsoft.babyservice.commons.constant.RedisKey;
import com.jqsoft.babyservice.commons.utils.RedisUtils;
import com.jqsoft.babyservice.commons.vo.RestVo;
import com.jqsoft.babyservice.controller.system.BaseController;
import com.jqsoft.babyservice.service.biz.LoginService;
import com.jqsoft.babyservice.service.biz.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/babyservice")
public class LoginController extends BaseController {
    @Autowired
    private LoginService loginService;
    @Autowired
    private UserService userService;
    @Autowired
    private RedisUtils redisUtils;


    /**
     * TODO 登录
     *
     * @param authCode 免登授权码
     * @param corpId   从钉钉获取corpId
     * @return
     * @author 李健平2018072
     * @date 2019年9月23日 下午2:28:21
     */
    @RequestMapping("/login")
    public RestVo login(@RequestParam(value = "authCode") String authCode,
                        @RequestParam(value = "corpId", required = false) String corpId) {
        log.info("入参:临时授权码:{}", authCode);
        log.info("用户token：{}", this.getToken());
        log.info("授权企业id:{}", corpId);
        return loginService.login(authCode, corpId);
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
