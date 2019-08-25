package com.duanya.spring.framework.core.annotation;

import com.duanya.spring.framework.mvc.enums.DyMethod;

import java.lang.annotation.*;

/**
 * @author zheng.liming
 * @date 2019/8/5
 * @description
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,ElementType.TYPE})
public @interface DyRequestMapping {
    String value() default "/";
    DyMethod method() default DyMethod.GET;
}
