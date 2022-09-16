package com.stackabuse.kafka_schema_registry;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.kafka.annotation.EnableKafka;

@EnableKafka
@ComponentScan
@SpringBootApplication
public class KafkaSchemaRegistryApplication {

	public static void main(String[] args) {
		SpringApplication.run(KafkaSchemaRegistryApplication.class, args);
	}

	@Bean
	NewTopic newTopic() {
		return new NewTopic("schema-registry-order", 3, (short) 1);
	}

}
