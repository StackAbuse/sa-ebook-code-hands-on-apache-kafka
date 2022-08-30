package com.stackabuse.kafka_data_pipeline.mysql.repository;

import com.stackabuse.kafka_data_pipeline.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockMysqlRepository extends JpaRepository<Stock, Long> {
}
