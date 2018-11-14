package com.example.demo.component.annotation;

import java.lang.annotation.*;

/**
 * 自定义缓存注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface RequsetCache {
    long time() default 5;
}
