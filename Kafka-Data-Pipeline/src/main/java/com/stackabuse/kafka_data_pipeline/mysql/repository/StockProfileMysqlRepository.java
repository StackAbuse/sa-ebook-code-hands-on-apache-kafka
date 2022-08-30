package com.stackabuse.kafka_data_pipeline.mysql.repository;

import com.stackabuse.kafka_data_pipeline.model.StockProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockProfileMysqlRepository extends JpaRepository<StockProfile, String> {
}
