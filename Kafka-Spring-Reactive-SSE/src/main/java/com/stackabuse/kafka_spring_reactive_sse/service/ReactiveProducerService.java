package com.stackabuse.kafka_spring_reactive_sse.service;

import com.github.javafaker.Faker;
import com.stackabuse.kafka_spring_reactive_sse.model.Article;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ReactiveProducerService {

    @Autowired
    private ReactiveKafkaProducerTemplate<String, Article> reactiveKafkaProducerTemplate;

    @Value(value = "${spring.kafka.producer.topic}")
    private String topic;

    @Scheduled(cron = "*/10 * * * * *")
    public void send() {
        Article latestNews = new Article(
                Faker.instance().lorem().sentence(5),
                Faker.instance().lorem().paragraph());
        log.info("Record sent to topic={}, {}={}",
                topic,
                Article.class.getSimpleName(),
                latestNews);
        reactiveKafkaProducerTemplate
                .send(topic, latestNews)
                .doOnSuccess(senderResult ->
                        log.info("Sent News: {} at offset : {}",
                                latestNews,
                                senderResult.recordMetadata().offset()))
                .doOnError(throwable ->
                        log.error("Some error occurred while consuming an order due to: {}",
                                throwable.getMessage()))
                .subscribe();
    }
}