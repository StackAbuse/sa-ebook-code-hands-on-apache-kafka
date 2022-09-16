package com.stackabuse.kafka_spring_boot_cassandra.controller;

import com.stackabuse.kafka_spring_boot_cassandra.entity.Order;
import com.stackabuse.kafka_spring_boot_cassandra.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    OrderRepository orderRepository;

    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders(@RequestParam(required = false) String productName) {
        try {
            List<Order> orders = new ArrayList<>();

            if (Objects.isNull(productName)) {
                orders.addAll(orderRepository.findAll());
            } else {
                orders.addAll(orderRepository.findByProductName(productName));
            }

            if (orders.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(orders, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable("id") UUID id) {
        return orderRepository
                .findById(id)
                .map(order -> new ResponseEntity<>(order, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        try {
            return new ResponseEntity<>(orderRepository.save(order), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
