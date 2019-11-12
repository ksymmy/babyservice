package com.jqsoft.nposervice.commons.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.jqsoft.nposervice.commons.constant.RedisKey;
import com.jqsoft.nposervice.commons.constant.ResultMsg;
import com.jqsoft.nposervice.commons.utils.RedisUtils;
import com.jqsoft.nposervice.commons.vo.RestVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 多重提交拦截
 */
@Slf4j
@Component
public class SubmitInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private RedisUtils redisUtils;

    /**
     * 前处理
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {

        Map<String, Object> lockData = getLockData(request, handler);
        String lockkey = (String) lockData.get("lockkey");
        Integer seconds = (Integer) lockData.get("seconds");
        if (seconds == null) {
            seconds = 5;
        }

        if (StringUtils.isNotBlank(lockkey)) {
            String value = (String)redisUtils.get(lockkey);
            if (StringUtils.isNotBlank(value)) {
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");

                RestVo restVo = new RestVo();
                restVo.setCode(ResultMsg.OPERATE_FREQUENT);
                response.getWriter().write(JSONObject.toJSONString(restVo));
                return false;
            }
            redisUtils.add(lockkey, "1", seconds, TimeUnit.SECONDS);
        }
        return true;
    }

    /**
     * 返回处理
     */
    @Override
    public void postHandle(HttpServletRequest hsr, HttpServletResponse hsr1,
                           Object o, ModelAndView mav) {
    }

    /**
     * 后处理
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception excptn) {
        Map<String, Object> lockData = getLockData(request, handler);
        String lockkey = (String) lockData.get("lockkey");
        if (StringUtils.isNotBlank(lockkey)) {
            redisUtils.remove(lockkey);
        }
    }

    Map<String, Object> getLockData(HttpServletRequest request, Object handler) {

        Map<String, Object> lockData = new HashMap<String, Object>();

        if (!handler.getClass().isAssignableFrom(HandlerMethod.class)) {
            return lockData;
        }

        final HandlerMethod handlerMethod = (HandlerMethod) handler;
        final Method method = handlerMethod.getMethod();
        final Class<?> clazz = method.getDeclaringClass();
        if (clazz.isAnnotationPresent(SubmitTarget.class) || method.isAnnotationPresent(SubmitTarget.class)) {
            SubmitTarget submitTarget = clazz.getAnnotation(SubmitTarget.class);
            submitTarget = (submitTarget == null) ? method.getAnnotation(SubmitTarget.class) : submitTarget;

            String token = request.getHeader(submitTarget.tokenName());
            if (StringUtils.isBlank(token)) {
                token = request.getParameter(submitTarget.tokenName());
            }
            String key = RedisKey.REPEAT_SUBMIT.getKey(token, request.getRequestURI());

            lockData.put("lockkey", key);
            lockData.put("seconds", submitTarget.seconds());
        }

        return lockData;
    }
}
