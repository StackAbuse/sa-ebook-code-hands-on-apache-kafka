package com.stackabuse.kafka_data_pipeline.postgresql.repository;

import com.stackabuse.kafka_data_pipeline.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockPostgresRepository extends JpaRepository<Stock, Long> {
}
