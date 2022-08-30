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
@Table(name="stock")
public class Stock {

    @Id
    private String id;
    private String date;
    private String company;
    private Float open;
    private Float close;
    private Float high;
    private Float low;
    private Integer volume;
}
