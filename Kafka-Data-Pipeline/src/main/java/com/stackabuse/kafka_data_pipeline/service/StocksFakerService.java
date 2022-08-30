package com.stackabuse.kafka_data_pipeline.service;

import com.github.javafaker.Faker;
import com.stackabuse.kafka_data_pipeline.model.Stock;
import com.stackabuse.kafka_data_pipeline.model.StockProfile;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class StocksFakerService {

    public List<Stock> getRandomStocks() throws ParseException {

        Faker faker = new Faker();
        List<Stock> stockCollection = new ArrayList<>();

        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));   // This line converts the given date into UTC time zone
        final Date dateObjFrom = sdf.parse("2001-01-01T01:37:56");
        final Date dateObjTo = sdf.parse("2020-03-31T01:37:56");

        for (int i = 0; i < 500; i++) {
            Stock stock = Stock.builder()
                    .id(UUID.randomUUID().toString())
                    .date(faker.date().between(dateObjFrom, dateObjTo).toString())
                    .company(faker.company().name())
                    .open(Float.parseFloat(faker.commerce().price(20, 1000)))
                    .close(Float.parseFloat(faker.commerce().price(500, 1000)))
                    .high(Float.parseFloat(faker.commerce().price(800, 1000)))
                    .low(Float.parseFloat(faker.commerce().price(5, 200)))
                    .volume(faker.random().nextInt(100, 1000000))
                    .build();
            stockCollection.add(stock);
        }

        return stockCollection;
    }

    public List<StockProfile> getRandomStockProfiles() throws ParseException {

        Faker faker = new Faker();
        List<StockProfile> stockProfileCollection = new ArrayList<>();

        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));   // This line converts the given date into UTC time zone
        final Date dateObjFrom = sdf.parse("2000-01-01T01:37:56");
        final Date dateObjTo = sdf.parse("2000-12-31T01:37:56");

        for (int i = 0; i < 100; i++) {
            StockProfile stockProfile = StockProfile.builder()
                    .id(UUID.randomUUID().toString())
                    .company(faker.company().name())
                    .profession(faker.company().profession())
                    .sector(faker.company().industry())
                    .address(faker.address().fullAddress())
                    .registration(faker.date().between(dateObjFrom, dateObjTo).toString())
                    .build();
            stockProfileCollection.add(stockProfile);
        }

        return stockProfileCollection;
    }
}
