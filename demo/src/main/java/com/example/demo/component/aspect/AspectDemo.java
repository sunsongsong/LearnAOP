package com.example.demo.component.aspect;

import net.sf.json.JSONObject;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 定义切面 用来记录日志
 */
@Aspect
@Component
@Order(-5)
public class AspectDemo {

    private Logger logger = LoggerFactory.getLogger(AspectDemo.class);


    /**
     * 前置通知
     * @param jp
     */
    @Before(value = "execution(* com.example.demo.web.AspectDemoController.*(..))")
    public void beforeMethod(JoinPoint jp){
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        //请求参数
        String params = "";
        if("GET".equals(request.getMethod())){
            params = request.getQueryString();
        }else if("POST".equals(request.getMethod())){
            params = JSONObject.fromObject(request.getParameterMap()).toString();
        }

        //调用方法名
        String methodName = jp.getSignature().getName();
        logger.info("{} invoked! 入参：{}",methodName,params);
    }

    /**
     * 返回通知 --有异常就执行不到了
     * @param jp
     * @param result
     */
    @AfterReturning(value="execution(* com.example.demo.web.AspectDemoController.*(..))",returning="result")
    public void afterReturningMethod(JoinPoint jp, Object result){
        logger.info("出参：{}",result.toString());
    }

}
