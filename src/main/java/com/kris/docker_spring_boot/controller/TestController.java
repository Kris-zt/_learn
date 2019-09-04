package com.kris.docker_spring_boot.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @RequestMapping("/test")
    public String show() {
        return ">>>>>>>>>>>>>>>>>Hello World<<<<<<<<<<<<<<<<";
    }
}
