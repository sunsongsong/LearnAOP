package com.example.demo.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AspectDemoController {

    private static final Logger logger = LoggerFactory.getLogger(AspectDemoController.class);

    /**
     * 日志需要记录 入参,出参,访问ip,执行时间
     * @param name
     * @return
     */
    @GetMapping(value = "/print")
    public String print(String name){
        logger.info("入参：name="+name);

        String result = "hello " + name;//业务处理

        logger.info("出参："+result);
        return result;
}

    @GetMapping(value = "/print2")
    public String print2(String name){
        String result = "hello " + name;//业务处理
        return result;
    }

    @GetMapping(value = "/print3")
    public String print3(String name){
        String result = "hello " + name;//业务处理
        return result;
    }

    @GetMapping(value = "/print4")
    public String print4(String name){
        String result = "hello " + name;//业务处理
        return result;
    }

}
