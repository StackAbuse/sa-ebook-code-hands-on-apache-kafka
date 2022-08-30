package com.stackabuse.kafka_data_pipeline.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stackabuse.kafka_data_pipeline.model.Stock;
import com.stackabuse.kafka_data_pipeline.model.StockProfile;
import com.stackabuse.kafka_data_pipeline.mysql.repository.StockMysqlRepository;
import com.stackabuse.kafka_data_pipeline.mysql.repository.StockProfileMysqlRepository;
import com.stackabuse.kafka_data_pipeline.postgresql.repository.StockPostgresRepository;
import com.stackabuse.kafka_data_pipeline.postgresql.repository.StockProfilePostgresRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class ConsumerService {

    @Autowired
    StockPostgresRepository stockPostgresRepo;

    @Autowired
    StockMysqlRepository stockMysqlRepo;

    @Autowired
    StockProfilePostgresRepository stockProfilePostgresRepo;

    @Autowired
    StockProfileMysqlRepository stockProfileMysqlRepo;

    @KafkaListener(topics = "stocks",
            groupId = "${spring.kafka.consumer.group}",
            autoStartup = "true",
            containerFactory = "kafkaListenerContainerFactory")
    public void consumeStockJson(String message) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode json = mapper.readTree(message);
        Stock stock = new Stock();
        stock.setId(json.findValue("id").textValue());
        stock.setDate(json.findValue("date").textValue());
        stock.setCompany(json.findValue("company").textValue());
        stock.setOpen(json.findValue("open").floatValue());
        stock.setClose(json.findValue("close").floatValue());
        stock.setHigh(json.findValue("high").floatValue());
        stock.setLow(json.findValue("low").floatValue());
        stock.setVolume(json.findValue("volume").intValue());
        stockPostgresRepo.save(stock);
        stockMysqlRepo.save(stock);
        log.info("Consumed Stock message: " + stock);
    }

    @KafkaListener(topics = "stocks_profiles",
            groupId = "${spring.kafka.consumer.group}",
            autoStartup = "true",
            containerFactory = "kafkaListenerContainerFactory")
    public void consumeStockProfileJson(String message) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode json = mapper.readTree(message);
        StockProfile stockProfile = new StockProfile();
        stockProfile.setId(json.findValue("id").textValue());
        stockProfile.setCompany(json.findValue("company").textValue());
        stockProfile.setProfession(json.findValue("profession").textValue());
        stockProfile.setSector(json.findValue("sector").textValue());
        stockProfile.setAddress(json.findValue("address").textValue());
        stockProfile.setRegistration(json.findValue("registration").textValue());
        stockProfilePostgresRepo.save(stockProfile);
        stockProfileMysqlRepo.save(stockProfile);
        log.info("Consumed Stock Profile message: " + stockProfile);
    }
}
