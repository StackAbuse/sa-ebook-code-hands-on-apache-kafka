package com.stackabuse.kafka_spring_cloud_reactive_cassandra.controller;

import com.stackabuse.kafka_spring_cloud_reactive_cassandra.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UIController {

    @Autowired
    CustomerRepository customerRepository;

    @GetMapping("/")
    public String displayHome(Model model) {
        return "main/home";
    }

    @GetMapping("/customers/all")
    public String displayCustomers(Model model) {
        model.addAttribute("customers", customerRepository.findAll().collectList().share().block());
        return "customers/list-customers";
    }
}
