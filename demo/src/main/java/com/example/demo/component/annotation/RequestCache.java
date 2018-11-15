package com.abc5w.datacenter.provider.service.annotation;

import java.lang.annotation.*;

/**
 * 使用缓存的注解,主要适用于GET请求
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface RequestCache {
    long time() default 5; //默认缓存时间5分钟
}
