package com.stackabuse.kafka_spring_boot_cassandra.controller;

import com.stackabuse.kafka_spring_boot_cassandra.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UIController {

    @Autowired
    OrderRepository orderRepository;

    @GetMapping("/")
    public String displayHome(Model model) {
        return "main/home";
    }

    @GetMapping("/orders/all")
    public String displayCustomers(Model model) {
        model.addAttribute("orders", orderRepository.findAll());
        return "orders/list-orders";
    }
}
