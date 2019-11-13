package com.jqsoft.babyservice.commons.interceptor;

import java.lang.annotation.*;

/**
 *  此注解在类上，类的所有方法被拦截；
 *  此注解在方法上，只有所加方法被拦截。
 *
 *  权限校验
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AdminCheck {

}
