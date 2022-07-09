package com.stackabuse.springcloudkafkastreams.model;

import lombok.*;

import java.util.Date;

@ToString
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {

    private String firstName;
    private String lastName;
    private String cardNumber;
    private String customerId;
    private String itemPurchased;
    private String orderId;
    int quantity;
    double price;
    private Date purchaseDate;
    private String zipCode;
}
