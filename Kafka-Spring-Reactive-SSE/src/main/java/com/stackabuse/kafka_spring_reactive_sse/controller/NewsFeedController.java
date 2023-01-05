package com.stackabuse.kafka_spring_reactive_sse.controller;

import com.stackabuse.kafka_spring_reactive_sse.model.Article;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;

@Slf4j
@RestController
@RequestMapping("/sse/newsfeed")
public class NewsFeedController {

    @Autowired
    private ReactiveKafkaConsumerTemplate<String, Article> reactiveKafkaConsumerTemplate;

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Article> getEventsFlux(){
        return reactiveKafkaConsumerTemplate
                .receiveAutoAck()
                .delayElements(Duration.ofSeconds(5))
                .checkpoint("Messages started to be consumed")
                .log()
                .doOnNext(consumerRecord ->
                        log.info("Received a News with key={}, value={} from topic={} at offset={}",
                                consumerRecord.key(),
                                consumerRecord.value(),
                                consumerRecord.topic(),
                                consumerRecord.offset()))
                .doOnError(throwable ->
                        log.error("Some error occurred while consuming a News due to: {}",
                                throwable.getMessage()))
                .map(ConsumerRecord::value)
                .checkpoint("Message consumption from Kafka is complete");
    }
}
