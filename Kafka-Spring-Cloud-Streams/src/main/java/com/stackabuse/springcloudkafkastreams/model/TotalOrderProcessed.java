package com.stackabuse.springcloudkafkastreams.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TotalOrderProcessed {

    private OrderStatus status;
    private int orderCount;
    private int productCount;
    private double totalAmount;
}
