package com.example.demo.component.aspect;

import com.example.demo.component.annotation.RequestCache;
import com.example.demo.component.constant.RedisKeyPrefix;
import com.example.demo.component.util.MD5Util;
import com.example.demo.service.RedisService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 使用AOP中的环绕通知,实现缓存查找和保存
 * 该方法目前主要用于对外使用的接口上,增加@RequestCache注释即可被拦截
 */
@Aspect
@Component
@Order(-5)
public class AspectDemo2 {

    private Logger logger = LoggerFactory.getLogger(AspectDemo2.class);

    /**
     * 是否开启拦截标识
     */
    private boolean OPEN = true;

    private static Lock lock  = new ReentrantLock();


    /**
     * 定义切面
     */
//    @Pointcut("execution(* com.example.demo.web.AspectDemoController2.*(..))")
    @Pointcut("@annotation(com.example.demo.component.annotation.RequestCache)")
    public void cache() {

    }

    @Autowired
    RedisService redisService;

    /**
     * 环绕通知
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around("cache()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        if(!OPEN){
            return joinPoint.proceed();
        }
        logger.info("doAround...");
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        if(!"GET".equals(request.getMethod())){//只对GET请求处理
            return joinPoint.proceed();
        }
        //GET请求链接
        String url = request.getRequestURL().toString();
        String queryStr = request.getQueryString();
        if(queryStr != null){
            url += "?" + queryStr;
        }
        String key = getMd5Url(url);
        Object value = redisService.get(key);
        if(value != null){
            logger.info("key: "+key+" in cache has value!");
            return value;
        }
        Object result = joinPoint.proceed();// result的值就是被拦截方法的返回值
        if(result != null && checkResult(result)){
            long time = getRequestCacheTime(joinPoint);
            redisService.set(key,result,time*60L);
            logger.info("setCache key="+key+" value="+result);
        }
        return result;
    }




    /**
     * 判断当前结果是否需要缓存
     * @param result
     * @return
     */
    private boolean checkResult(Object result){
        //todo:判断当前结果是否需要缓存（例如：异常结果不需要缓存）

        return true;
    }

    /**
     * 获取自定义注解的缓存时间（分钟）
     * @param joinPoint
     * @return
     */
    private long getRequestCacheTime(ProceedingJoinPoint joinPoint){
        long time = 5;
        RequestCache requestCache = ((MethodSignature)joinPoint.getSignature()).getMethod().getAnnotation(RequestCache.class);
        if(requestCache != null){
            time = requestCache.time();
        }
        return time;
    }

    private String getMd5Url(String url){
        String md5Url = RedisKeyPrefix.STUDENT + MD5Util.toMD5Str(url);
        logger.info("getMd5Url url="+url+" md5Url="+md5Url);
        return md5Url;
    }

    /**
     * 环绕通知
     * @param joinPoint
     * @return
     * @throws Throwable
     */
//    @Around("cache()")
    public Object doAround2(ProceedingJoinPoint joinPoint) throws Throwable {
        if(!OPEN){
            return joinPoint.proceed();
        }
        logger.info("doAround2...");
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        if(!"GET".equals(request.getMethod())){//只对GET请求处理
            return joinPoint.proceed();
        }
        //GET请求链接
        String url = request.getRequestURL().toString();
        String queryStr = request.getQueryString();
        if(queryStr != null){
            url += "?" + queryStr;
        }
        String key = getMd5Url(url);
        Object value = redisService.get(key);

        //使用双重检查锁
        if(value != null){
            logger.info("第一次检查 key: "+key+" in cache has value!");
            return value;
        }
        lock.lock();
        try{
            value = redisService.get(key);
            if(value != null){
                logger.info("第二次检查 key: "+key+" in cache has value!");
                return value;
            }
            Object result = joinPoint.proceed();// result的值就是被拦截方法的返回值
            if(result != null && checkResult(result)){
                long time = getRequestCacheTime(joinPoint);
                redisService.set(key,result,time*60L);
                logger.info("setCache key="+key+" value="+result);
            }
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return "error";
        }finally {
            lock.unlock();
        }
    }

}
