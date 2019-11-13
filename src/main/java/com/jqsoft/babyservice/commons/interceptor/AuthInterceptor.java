package com.jqsoft.babyservice.commons.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.jqsoft.babyservice.commons.constant.ResultMsg;
import com.jqsoft.babyservice.commons.utils.RedisUtils;
import com.jqsoft.babyservice.commons.vo.RestVo;
import com.jqsoft.babyservice.entity.biz.UserInfo;
import com.jqsoft.babyservice.service.biz.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

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
    UserService userService;
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

        // 允许访问标记(0:可以访问 1：corpid或userid为空 2：用户不存在 3：没有权限)
        int flag = 0;
        // 校验类型（0:不校验 1：真实用户校验 2：管理员权限校验 2：家长权限校验）
        int checkType = 0;

        // 出现注解则需要验证用户权限
        if (clazz.isAnnotationPresent(UserCheck.class) || method.isAnnotationPresent(UserCheck.class)) {
            checkType = 1;
        } else if (clazz.isAnnotationPresent(AdminCheck.class) || method.isAnnotationPresent(AdminCheck.class)) {
            checkType = 2;
        } else if (clazz.isAnnotationPresent(ParentCheck.class) || method.isAnnotationPresent(ParentCheck.class)) {
            checkType = 3;
        }
        if (checkType !=0 ) {
           String corpid = this.getCorpid(request);
           String userid = this.getUserid(request);
           if (StringUtils.isBlank(corpid) || StringUtils.isBlank(userid)) {
               // corpid 或 userid 为空
               flag = 1;
           } else {
               UserInfo user = null;
               if (null == user) {
                   // 用户不存在
                   flag = 2;
               } else {
                   Byte admin = user.getAdmin();
                   if (!((admin == 1 && checkType == 2) || (admin == 0 && checkType == 3))) {
                       // 用户角色和接口权限不符
                       flag = 3;
                   }
               }
           }
        }

        if (0 != flag) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            RestVo restVo = new RestVo();
            if (1 == flag) {
                restVo.setCode(ResultMsg.CORPID_USERID_IS_NULL);
            } else if (2 == flag) {
                restVo.setCode(ResultMsg.USER_NOT_EXISTS);
            } else if (3 == flag) {
                restVo.setCode(ResultMsg.NOT_AUTH);
            }
            response.getWriter().write(JSONObject.toJSONString(restVo));
            return false;
        }
        return true;
    }

    private static String getCorpid(HttpServletRequest request){
        String corpid = request.getHeader("corpid");
        if (StringUtils.isBlank(corpid)) {
            corpid = request.getParameter("corpid");
        }
        return corpid;
    }

    private static String getUserid(HttpServletRequest request){
        String userid = request.getHeader("userid");
        if (StringUtils.isBlank(userid)) {
            userid = request.getParameter("userid");
        }
        return userid;
    }
}
