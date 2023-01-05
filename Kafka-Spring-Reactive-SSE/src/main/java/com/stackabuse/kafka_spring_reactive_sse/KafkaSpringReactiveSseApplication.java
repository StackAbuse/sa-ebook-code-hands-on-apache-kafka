package com.stackabuse.kafka_spring_reactive_sse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class KafkaSpringReactiveSseApplication {

	public static void main(String[] args) {
		SpringApplication.run(KafkaSpringReactiveSseApplication.class, args);
	}

}
