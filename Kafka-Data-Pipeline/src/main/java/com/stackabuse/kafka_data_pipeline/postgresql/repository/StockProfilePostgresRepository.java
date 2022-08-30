package com.stackabuse.kafka_data_pipeline.postgresql.repository;

import com.stackabuse.kafka_data_pipeline.model.StockProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockProfilePostgresRepository extends JpaRepository<StockProfile, String> {
}
