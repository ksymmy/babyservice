package com.jqsoft.babyservice.commons.interceptor;

import java.lang.annotation.*;

/**
 *  此注解在类上，类的所有方法被拦截；
 *  此注解在方法上，只有所加方法被拦截。
 *
 *  拦截防止重复提交
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SubmitTarget {

	/**
	 * 从哪获取令牌，默认是从请求头，否则从参数获取
	 *
	 * @return
	 */
	String holdPlace() default "header";

	/**
	 * 存储令牌的名称，默认是token
	 *
	 * @return
	 */
	String tokenName() default "token";

	/**
	 * 锁定时间，单位：秒
	 *
	 * @return
	 */
	int seconds() default 5;
}
