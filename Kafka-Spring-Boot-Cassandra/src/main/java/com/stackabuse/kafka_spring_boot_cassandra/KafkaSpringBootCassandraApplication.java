package com.stackabuse.kafka_spring_boot_cassandra;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.kafka.annotation.EnableKafka;

@EnableKafka
@ComponentScan
@SpringBootApplication
public class KafkaSpringBootCassandraApplication {

	public static void main(String[] args) {
		SpringApplication.run(KafkaSpringBootCassandraApplication.class, args);
	}

}
