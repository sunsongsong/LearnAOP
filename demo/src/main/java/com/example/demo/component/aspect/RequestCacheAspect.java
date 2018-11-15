package com.abc5w.datacenter.provider.service.aspest;

import com.abc5w.datacenter.provider.service.cache.RedisService;
import com.abc5w.datacenter.provider.service.annotation.RequestCache;
import com.google.gson.Gson;
import net.p5w.common.util.MD5Util;
import net.sf.json.JSONObject;
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

/**
 * 使用AOP中的环绕通知,实现缓存查找和保存
 * 该方法目前主要用于对外使用的接口上,增加@RequestCache注释即可被拦截
 */
@Aspect
@Component
@Order(-5)
public class RequestCacheAspect {

    private Logger logger = LoggerFactory.getLogger(RequestCacheAspect.class);

    /**
     * 是否开启拦截标识
     */
    private boolean OPEN = true;

    /**
     * RequestCache 缓存key的前缀
     */
    private String cacheKeyPre = "RequsetCache:";

    @Autowired
    RedisService redisService;

    @Pointcut("@annotation(com.abc5w.datacenter.provider.service.annotation.RequestCache)")
    public void cache() {

    }

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
     * 检验返回值是否有效
     * 目前校验规则：返回值状态码200 目前适用于PublicServiceImpl、CppInfoServiceImpl
     * @param result
     * @return
     */
    private boolean checkResult(Object result){
        Gson gson = new Gson();
        try{
            String json = (String)result;
            JSONObject jsonObject = gson.fromJson(json,JSONObject.class);
            //注意：反序列化时 jsonObject.get("status")转为dubble了
            Double status = Double.valueOf(jsonObject.get("status").toString());
            if(200-status != 0.0){//不是正确的状态码
                return false;
            }
            return true;
        }catch (Exception e){
            logger.error("checkResult Exception",e);
            return false;
        }
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
        String md5Url = cacheKeyPre + MD5Util.toMD5Str(url);
        logger.info("getMd5Url url="+url+" md5Url="+md5Url);
        return md5Url;
    }
}
