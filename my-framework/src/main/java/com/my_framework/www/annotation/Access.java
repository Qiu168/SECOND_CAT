package com.my_framework.www.annotation;


import java.lang.annotation.*;


/**
 * 权限管理
 * @author 14629
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface Access {
    boolean authority() default true;
    String message() default "没有权限";
}
