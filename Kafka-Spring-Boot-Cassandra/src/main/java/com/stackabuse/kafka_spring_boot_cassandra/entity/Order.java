package com.stackabuse.kafka_spring_boot_cassandra.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.UUID;

@Table
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @PrimaryKeyColumn(
            ordinal = 2,
            type = PrimaryKeyType.CLUSTERED,
            ordering = Ordering.DESCENDING)
    private UUID id;

    @PrimaryKeyColumn(
            name = "product_name",
            ordinal = 0,
            type = PrimaryKeyType.PARTITIONED)
    private String productName;

    @PrimaryKeyColumn(
            name = "product_id",
            ordinal = 1,
            type = PrimaryKeyType.PARTITIONED)
    private String productId;

    @Column(value = "product_type")
    private String productType;

    @Column(value = "product_count")
    private int productCount;

    @Column(value = "listing_date")
    private String listingDate;

    @Column(value = "customer_id")
    private String customerId;

    @Column(value = "customer_email")
    private String customerEmail;

    @Column(value = "customer_name")
    private String customerName;

    @Column(value = "customer_mobile")
    private String customerMobile;

    @Column(value = "shipping_address")
    private String shippingAddress;

    @Column(value = "shipping_pincode")
    private String shippingPincode;

    private String status;
    private double price;
    private double weight;

    @Column(value = "automated_email")
    private Boolean automatedEmail;
}
