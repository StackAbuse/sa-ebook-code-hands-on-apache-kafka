package com.stackabuse.kafka_data_pipeline.service;

import com.stackabuse.kafka_data_pipeline.model.Stock;
import com.stackabuse.kafka_data_pipeline.model.StockProfile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.text.ParseException;
import java.util.List;

@Slf4j
@Service
public class ProducerService {

    private static final String STOCKS_TOPIC = "stocks";
    private static final String STOCKS_PROFILES_TOPIC = "stocks_profiles";

    @Autowired
    StocksFakerService stocksFakerService;

    @Autowired
    private KafkaTemplate<String, Stock> kafkaStockTemplate;

    @Autowired
    private KafkaTemplate<String, StockProfile> kafkaStockProfileTemplate;

    @PostConstruct
    @Transactional
    public void sendMessage() throws ParseException {
        List<Stock> stocks = stocksFakerService.getRandomStocks();
        List<StockProfile> stocksProfiles = stocksFakerService.getRandomStockProfiles();
        for (Stock stock: stocks) {
            log.info("$$ -> Producing stocks message --> {}", stock);
            kafkaStockTemplate.send(STOCKS_TOPIC, stock);
        }
        for (StockProfile stockProfile: stocksProfiles) {
            log.info("$$ -> Producing stocks profile message --> {}", stockProfile);
            kafkaStockProfileTemplate.send(STOCKS_PROFILES_TOPIC, stockProfile);
        }
    }
}
