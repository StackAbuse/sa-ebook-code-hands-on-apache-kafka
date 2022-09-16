package com.stackabuse.kafka_spring_cloud_reactive_cassandra;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.schema.client.EnableSchemaRegistryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableSchemaRegistryClient
@SpringBootApplication
public class KafkaSpringCloudReactiveCassandraApplication {

	public static void main(String[] args) {
		SpringApplication.run(KafkaSpringCloudReactiveCassandraApplication.class, args);
	}

}
