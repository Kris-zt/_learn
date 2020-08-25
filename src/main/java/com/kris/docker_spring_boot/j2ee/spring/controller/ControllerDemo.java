package com.kris.docker_spring_boot.j2ee.spring.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ControllerDemo {

    @RequestMapping("/test")
    public String show() {
        return "==================== Hello Docker ====================";
    }
}
