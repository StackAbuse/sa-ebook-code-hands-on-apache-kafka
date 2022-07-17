package com.stackabuse.kafkaspringbootreactive.service;

import com.stackabuse.kafkaspringbootreactive.model.Order;
import com.stackabuse.kafkaspringbootreactive.model.OrderStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.kafka.transaction.KafkaTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;
import reactor.kafka.sender.TransactionManager;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.Map;

@Slf4j
@Service
public class ReactiveConsumerService {

    @Autowired
    private ReactiveKafkaConsumerTemplate<String, Order> reactiveKafkaConsumerTemplate;

    @Autowired
    private ReactiveProducerService reactiveProducerService;

    @Autowired
    private WebClient webClient;

//    @Autowired
//    private KafkaProperties properties;

    private Flux<Order> consumeAnyOrders() {

        // For exactly once processing of messages
//        Map<String, Object> props = properties.buildProducerProperties();
//        SenderOptions<Object, Object> senderOptions = SenderOptions.create(props)
//                .producerProperty(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "courier-booking-txn")
//                .producerProperty(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);

        return reactiveKafkaConsumerTemplate
//                .receiveAtMostOnce()
                .receiveAutoAck()
//                .receiveExactlyOnce(KafkaSender.create(senderOptions).transactionManager())
                .delayElements(Duration.ofSeconds(5)) //BACKPRESSURE
//                .flatMap(r -> r)
                .doOnNext(consumerRecord ->
                        log.info("Received an Order with key={}, value={} from topic={} at offset={}",
                        consumerRecord.key(),
                        consumerRecord.value(),
                        consumerRecord.topic(),
                        consumerRecord.offset()))
                .map(ConsumerRecord::value)
                .filter(o -> o.getStatus().equals(OrderStatus.READY_TO_PICK))
                .doOnNext(order -> log.info("Processing Order with details {}={} to book a courier",
                        Order.class.getSimpleName(), order))
                .flatMap(this::bookDHLShipment)
                .doOnNext(reactiveProducerService::send)
                .doOnNext(order -> log.info("Order sent {}={} after booking the courier",
                        Order.class.getSimpleName(), order))
                .doOnError(throwable -> log.error("Some error occurred while consuming an order due to: {}",
                        throwable.getMessage()));
    }

    private void concurrentProcessing() {

        Scheduler scheduler = Schedulers.newElastic("order-warehouse", 60, true);

        reactiveKafkaConsumerTemplate
                .receive()
                .groupBy(m -> m.receiverOffset().topicPartition())
                .flatMap(partitionFlux -> partitionFlux
                        .publishOn(scheduler)
                        .doOnNext(receiverRecord ->
                                log.info("Received an Order with key={}, value={} from topic={} at offset={}",
                                        receiverRecord.key(),
                                        receiverRecord.value(),
                                        receiverRecord.topic(),
                                        receiverRecord.offset()))
                        .sample(Duration.ofMillis(5000))
                        .concatMap(receiverRecord -> receiverRecord.receiverOffset().commit()));
    }

    @PostConstruct
    public void startConsuming() {
        // We need to trigger the consumption process. This can either be a PostConstruct,
        // CommandLineRunner, ApplicationContext or ApplicationReadyEvent
        consumeAnyOrders().subscribe();
    }

    private Mono<Order> bookDHLShipment(Order order) {

        // To mock the Courier booking API
        order.setStatus(OrderStatus.COURIER_BOOKED);
        order.setCourierCompany("DHL");

        return webClient
                .post()
                .uri("https://dhl-shipment-api/dummy") // Dummy API URL to mock shipment booking
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(order))
                .retrieve()
                .bodyToMono(Order.class)
                .log()
                .onErrorReturn(order);
    }
}
