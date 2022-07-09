package com.stackabuse.springcloudkafkastreams.model;

import lombok.*;

import java.util.Date;

@ToString
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryItem {

    private String id;
    private String name;
    private int count;
    private Date listingDate;
}
