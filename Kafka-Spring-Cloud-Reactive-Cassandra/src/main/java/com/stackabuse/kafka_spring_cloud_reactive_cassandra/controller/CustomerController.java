package com.stackabuse.kafka_spring_cloud_reactive_cassandra.controller;

import com.stackabuse.kafka_spring_cloud_reactive_cassandra.entity.Customer;
import com.stackabuse.kafka_spring_cloud_reactive_cassandra.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    CustomerRepository customerRepository;

    @GetMapping
    public Flux<Customer> getAllCustomers(@RequestParam(required = false) String department) {
        if (Objects.isNull(department)) {
            return customerRepository.findAll();
        } else {
            return customerRepository.findByDepartment(department);
        }
    }

    @GetMapping("/{id}")
    public Mono<Customer> getCustomerById(@PathVariable("id") int id) {
        return customerRepository.findById(id);
    }

    @PostMapping
    public Mono<Customer> saveCustomer(@RequestBody Customer customer) {
        return customerRepository.save(customer);
    }
}
