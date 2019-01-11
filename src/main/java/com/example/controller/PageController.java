package com.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PageController {

    @RequestMapping("/client")
    public String toClientPage(){
        return "index";
    }

    @RequestMapping("/server")
    public String toServerPage(){
        return "server";
    }

}
