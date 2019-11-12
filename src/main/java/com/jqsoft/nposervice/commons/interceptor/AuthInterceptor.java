package com.jqsoft.nposervice.commons.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.jqsoft.nposervice.commons.utils.RedisUtils;
import com.jqsoft.nposervice.commons.constant.RedisKey;
import com.jqsoft.nposervice.commons.constant.ResultMsg;
import com.jqsoft.nposervice.commons.vo.RestVo;
import com.jqsoft.nposervice.entity.biz.UserInfo;
import com.jqsoft.nposervice.service.biz.MemberUserInfoService;
import com.jqsoft.nposervice.service.system.ResourcesService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 用户登录及权限验证
 */
@Slf4j
@Component
public class AuthInterceptor extends HandlerInterceptorAdapter {
    /**
     * 登录过期时间
     */
    @Value("${token-expire}")
    private Integer tokenExpire;

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    ResourcesService resourcesService;

    @Autowired
    MemberUserInfoService memberUserInfoService;

    /**
     * 前处理
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        if (request.getHeader(HttpHeaders.ORIGIN) != null) {
            response.addHeader("Access-Control-Allow-Credentials", "true");
            response.addHeader("Access-Control-Allow-Headers:", "x-requested-with, authorization, Content-Type, Authorization, credential, X-XSRF-TOKEN,token,username,client");
            response.addHeader("Access-Control-Allow-Methods", "*");
            response.addHeader("Access-Control-Allow-Origin", "*");
            response.addHeader("Access-Control-Expose-Headers", "*");
            response.addHeader("Access-Control-Max-Age", "18000L");
        }
        if (!handler.getClass().isAssignableFrom(HandlerMethod.class)) {
            return true;
        }
        final HandlerMethod handlerMethod = (HandlerMethod) handler;
        final Method method = handlerMethod.getMethod();
        final Class<?> clazz = method.getDeclaringClass();

        // 允许访问标记(0:可以访问 1：token为空 2：未登录 3：未审核 4：审核不通过 5：用户被停用 6：没有权限)
        int flag = 0;
        // 校验类型（0:不校验 1：登录校验 2：登录校验+权限校验）
        int checkType = 0;

        // 出现注解则需要验证用户权限
        if (clazz.isAnnotationPresent(LoginCheck.class) || method.isAnnotationPresent(LoginCheck.class)) {
            checkType = 1;
        } else if (clazz.isAnnotationPresent(AuthCheck.class) || method.isAnnotationPresent(AuthCheck.class)) {
            checkType = 2;
        }
        String userId = null;
        if (checkType == 1 || checkType == 2) {
            // 登录校验
            String tokenName = "token";
            String token = request.getParameter(tokenName);
            if(StringUtils.isBlank(token)) {
                token = request.getHeader(tokenName);
            }

            if (StringUtils.isBlank(token)) {
                flag = 1;
            } else {
                // 查看redis当前用户是否已经登录
                String userIdKey = RedisKey.LOGIN_USERID.getKey(token);
                userId = (String)redisUtils.get(userIdKey);
                if(StringUtils.isEmpty(userId)){
                    flag = 2;
                }else{
                    // 重新设置登陆缓存的过期时间
                    String tokenKey = RedisKey.LOGIN_TOKEN.getKey(userId);
                    redisUtils.expire(tokenKey, tokenExpire, TimeUnit.MINUTES);
                    redisUtils.expire(userIdKey, tokenExpire, TimeUnit.MINUTES);

                    // 判断用户状态（未审核、审核不通过、停用的用户不能访问系统）
                    UserInfo userInfo = memberUserInfoService.findByID(userId).getData();
                    Byte status = userInfo.getStatus();
                    if (status.byteValue() == 0) {
                        flag = 3;// 未审核
                    } else if (status.byteValue() == 2) {
                        flag = 4;// 审核不通过
                    } else if (status.byteValue() == 3) {
                        flag = 5;// 用户被停用
                    }
                }
            }



        }
        if (flag == 0 && checkType == 2) {
            // 权限校验
            // 获取当前用户的所有资源权限
            List<String> jumpList = resourcesService.getAllResourcesJumpByUserId(userId);
            String requestUri = request.getRequestURI();
            if (CollectionUtils.isEmpty(jumpList) || !jumpList.contains(requestUri)) {
                flag = 6;
            }
        }

        if(0 != flag){
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            RestVo restVo = new RestVo();
            if (1 == flag) {
                restVo.setCode(ResultMsg.TOKEN_IS_NULL);
            } else if (2 == flag){
                restVo.setCode(ResultMsg.NOT_LOGIN);
            } else if (3 == flag){
                restVo.setCode(ResultMsg.USER_UNCHECK);
            } else if (4 == flag){
                restVo.setCode(ResultMsg.USER_CHECK_FAIL);
            } else if (5 == flag){
                restVo.setCode(ResultMsg.USER_IS_STOPED);
            } else if (6 == flag){
                restVo.setCode(ResultMsg.NOT_AUTH);
            }
            response.getWriter().write(JSONObject.toJSONString(restVo));
            return false;
        }
        return true;
    }
}
