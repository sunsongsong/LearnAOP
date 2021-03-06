package com.example.demo.web;

import com.example.demo.component.annotation.RequestCache;
import com.example.demo.component.common.vo.JsonResult;
import com.example.demo.component.constant.RedisKeyPrefix;
import com.example.demo.dao.StudentMapper;
import com.example.demo.model.Student;
import com.example.demo.service.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AspectDemoController2 {

    private static final Logger logger = LoggerFactory.getLogger(AspectDemoController2.class);

    @Autowired
    StudentMapper studentMapper;

    @Autowired
    RedisService redisService;

    @GetMapping(value = "/getStudent")
    public String getStudent(String id){
        logger.info("getStudent invoked! id="+id);
        return JsonResult.okResult(studentMapper.queryStudent(id));
    }

    @GetMapping(value = "/getStudent2")
    public String getStudent2(String id){
        logger.info("getStudent2 invoked! id="+id);
        String key = RedisKeyPrefix.STUDENT + id;
        Object object = redisService.get(key);
        if(object != null){
            logger.info("cache has this key:"+key);
            return JsonResult.okResult(object);
        }
        Student student = studentMapper.queryStudent(id);
        redisService.set(key,student,3000L);
        return JsonResult.okResult(student);
    }

    @RequestCache(time = 1)
    @GetMapping(value = "/getStudent3")
    public String getStudent3(String id){
        logger.info("getStudent3 invoked! id="+id);
        Student student = studentMapper.queryStudent(id);
        return JsonResult.okResult(student);
    }

}
