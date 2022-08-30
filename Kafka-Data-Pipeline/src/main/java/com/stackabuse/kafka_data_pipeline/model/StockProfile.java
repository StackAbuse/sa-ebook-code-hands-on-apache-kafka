package com.stackabuse.kafka_data_pipeline.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Getter
@Setter
@Entity
@Table(name="stock_profile")
public class StockProfile {

    @Id
    private String id;
    private String company;
    private String profession;
    private String sector;
    private String address;
    private String registration;
}
