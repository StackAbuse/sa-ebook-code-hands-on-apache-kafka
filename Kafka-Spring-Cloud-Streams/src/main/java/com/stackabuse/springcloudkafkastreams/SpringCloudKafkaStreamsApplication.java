package com.stackabuse.springcloudkafkastreams;

import com.github.javafaker.Faker;
import com.stackabuse.springcloudkafkastreams.model.Order;
import com.stackabuse.springcloudkafkastreams.model.OrderStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.Instant;
import java.time.ZoneId;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Slf4j
@EnableScheduling
@SpringBootApplication
public class SpringCloudKafkaStreamsApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringCloudKafkaStreamsApplication.class, args);
	}

	@Bean
	public Supplier<Message<Order>> producer() {
		Order order = Order.builder()
				.id(UUID.randomUUID().toString())
				.productName(Faker.instance().commerce().productName())
				.productId(Faker.instance().idNumber().ssnValid())
				.productType(Faker.instance().commerce().department())
				.productCount(Faker.instance().random().nextInt(1, 5))
				.listingDate(Instant
						.ofEpochMilli(Faker
								.instance()
								.date()
								.past(3, TimeUnit.DAYS)
								.getTime())
						.atZone(ZoneId.systemDefault())
						.toLocalDateTime())
				.customerId(Faker.instance().idNumber().invalid())
				.customerName(Faker.instance().artist().name())
				.customerEmail(Faker.instance().internet().emailAddress())
				.customerMobile(Faker.instance().phoneNumber().cellPhone())
				.shippingAddress(Faker.instance().address().fullAddress())
				.shippingPincode(Faker.instance().address().zipCode())
				.status(OrderStatus.PLACED)
				.price(Double.parseDouble(Faker.instance().commerce().price()))
				.build();
		log.info("Mock Order from Fulfillment station -> {}", order);
		return () -> MessageBuilder
				.withPayload(order)
				.setHeader(KafkaHeaders.MESSAGE_KEY, order.getId().getBytes())
				.build();
	}

}
