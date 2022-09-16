package com.stackabuse.kafka_spring_cloud_reactive_cassandra.repository;

import com.stackabuse.kafka_spring_cloud_reactive_cassandra.entity.Customer;
import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import reactor.core.publisher.Flux;

public interface CustomerRepository extends ReactiveCassandraRepository<Customer, Integer> {

    @AllowFiltering
    Flux<Customer> findByDepartment(String department);
}
