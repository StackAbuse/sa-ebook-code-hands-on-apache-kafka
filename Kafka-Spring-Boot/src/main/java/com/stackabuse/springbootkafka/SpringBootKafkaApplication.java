package com.stackabuse.springbootkafka;

import com.stackabuse.springbootkafka.model.InventoryItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;

import javax.annotation.PostConstruct;

@Slf4j
@SpringBootApplication
public class SpringBootKafkaApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootKafkaApplication.class, args);
	}

//	@Autowired
//	private KafkaTemplate<String, String> kafkaTemplate;
//
//	@PostConstruct
//	public void sendMessage() {
//		String message = "Hello World";
//		log.info(String.format("Message sent -> %s", message));
//		this.kafkaTemplate.send("test", message);
//	}
//
//	@KafkaListener(topics = "test",	groupId = "group-id")
//	public void consume(String message)	{
//		log.info(String.format("Message received -> %s", message));
//	}
}
