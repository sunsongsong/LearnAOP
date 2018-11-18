package com.example.demo.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AspectController {

    @GetMapping(value = "/hello")
    public String helloAspect(){
        return "helloAspect";
    }

    @GetMapping(value = "/hello2")
    public String helloAspect2(){
        int i = 1/0;
        return "helloAspect2";
    }


}
