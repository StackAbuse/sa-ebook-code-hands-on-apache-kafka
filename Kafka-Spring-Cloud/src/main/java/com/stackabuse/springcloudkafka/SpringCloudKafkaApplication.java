package com.stackabuse.springcloudkafka;

import com.stackabuse.springcloudkafka.binder.OrderBinder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableBinding(value = {OrderBinder.class})
@SpringBootApplication
public class SpringCloudKafkaApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringCloudKafkaApplication.class, args);
	}

}
