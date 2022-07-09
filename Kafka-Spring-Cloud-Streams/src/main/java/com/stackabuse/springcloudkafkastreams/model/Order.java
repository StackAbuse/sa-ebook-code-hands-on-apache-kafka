package com.stackabuse.springcloudkafkastreams.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.*;

import javax.validation.constraints.Email;
import java.time.LocalDateTime;

@ToString
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {

    private String id;
    private String productName;
    private String productId;
    private String productType;
    private Integer productCount;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime listingDate;
    private String customerId;

    @Email
    private String customerEmail;
    private String customerName;
    private String customerMobile;
    private String shippingAddress;
    private String shippingPincode;
    private String courierCompany;
    private OrderStatus status;
    private Double price;
}
