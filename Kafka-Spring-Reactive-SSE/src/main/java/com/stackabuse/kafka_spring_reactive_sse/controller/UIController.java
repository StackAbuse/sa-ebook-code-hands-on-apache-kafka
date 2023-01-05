package com.stackabuse.kafka_spring_reactive_sse.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UIController {

    @GetMapping("/")
    public String displayHome(Model model) {
        return "index";
    }
}
