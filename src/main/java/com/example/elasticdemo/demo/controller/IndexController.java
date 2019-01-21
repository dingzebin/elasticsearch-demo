package com.example.elasticdemo.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RequestMapping("/index")
public class IndexController {
    @RequestMapping
    public String index() {
        return "index";
    }
}
