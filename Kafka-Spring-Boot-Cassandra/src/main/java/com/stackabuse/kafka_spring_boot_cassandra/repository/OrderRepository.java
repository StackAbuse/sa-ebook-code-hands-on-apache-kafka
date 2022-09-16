package com.stackabuse.kafka_spring_boot_cassandra.repository;

import com.stackabuse.kafka_spring_boot_cassandra.entity.Order;
import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.List;
import java.util.UUID;

public interface OrderRepository extends CassandraRepository<Order, UUID> {

    //Like other Database Repositories, some commonly used methods are already provided by CassandraRepository.
    //Hence, we don't need to write those here. We can write custom methods.
    //For example, below method is a custom method.
    @AllowFiltering
    List<Order> findByProductName(String productName);
}
