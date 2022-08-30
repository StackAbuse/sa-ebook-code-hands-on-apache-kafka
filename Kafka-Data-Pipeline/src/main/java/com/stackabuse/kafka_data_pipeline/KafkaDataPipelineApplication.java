package com.stackabuse.kafka_data_pipeline;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
//@EnableJpaRepositories
@ComponentScan
@ConfigurationPropertiesScan("com.stackabuse.kafka_data_pipeline.config")
public class KafkaDataPipelineApplication {

	public static void main(String[] args) {
		SpringApplication.run(KafkaDataPipelineApplication.class, args);
	}

}
