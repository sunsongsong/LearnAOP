package com.example.demo.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LearnAspectController {

    Logger logger = LoggerFactory.getLogger(LearnAspectController.class);

    @GetMapping(value = "/hello")
    public String helloAspect(){
        logger.info("helloAspect invoked!");
        return "helloAspect";
    }

    @GetMapping(value = "/hello2")
    public String helloAspect2(){
        int i = 1/0;
        return "helloAspect2";
    }


}
