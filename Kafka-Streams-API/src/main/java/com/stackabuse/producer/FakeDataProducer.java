package com.stackabuse.producer;

import com.github.javafaker.Faker;
import com.github.javafaker.Finance;
import com.github.javafaker.Name;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.stackabuse.constants.ApplicationConstants;
import com.stackabuse.model.Customer;
import com.stackabuse.model.Transaction;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.regex.Pattern;

public class FakeDataProducer {

    private static final Logger logger = LoggerFactory.getLogger(FakeDataProducer.class);

    private static Producer<String, String> producer;
    private static final Gson gson = new GsonBuilder().disableHtmlEscaping().create();
    private static ExecutorService executorService = Executors.newFixedThreadPool(1);
    private static Callback callback;
    private static final String TRANSACTIONS_TOPIC = ApplicationConstants.SOURCE_TOPIC;
    private static volatile boolean keepRunning = true;
    private static final Supplier<Date> timestampGenerator = () -> new Faker().date().past(15, TimeUnit.MINUTES, new Date());

    public static List<Transaction> generateTransactions(int number, int numberCustomers) {
        List<Transaction> transactions = new ArrayList<>();
        Faker faker = new Faker();
        List<Customer> customers = generateCustomers(numberCustomers);

        Random random = new Random();
        for (int i = 0; i < number; i++) {
            String itemPurchased = faker.commerce().productName();
            int quantity = faker.number().numberBetween(1, 5);
            double price = Double.parseDouble(faker.commerce().price(4.00, 295.00));
            Date purchaseDate = timestampGenerator.get();

            Customer customer = customers.get(random.nextInt(numberCustomers));
            String zipCode = faker.options().option("471975", "976663", "113469", "334457");

            Transaction transaction = Transaction
                    .builder()
                    .cardNumber(customer.getCardNumber())
                    .customerId(customer.getCustomerId())
                    .firstName(customer.getFirstName())
                    .lastName(customer.getLastName())
                    .itemPurchased(itemPurchased)
                    .quantity(quantity)
                    .price(price)
                    .purchaseDate(purchaseDate)
                    .zipCode(zipCode)
                    .build();
            transactions.add(transaction);
        }
        return transactions;
    }

    public static List<Customer> generateCustomers(int numberCustomers) {
        List<Customer> customers = new ArrayList<>(numberCustomers);
        Faker faker = new Faker();
        List<String> cards = generateCardNumbers(numberCustomers);
        for (int i = 0; i < numberCustomers; i++) {
            Name name = faker.name();
            customers.add(new Customer(name.firstName(), name.lastName(), faker.idNumber().valid(), cards.get(i)));
        }
        return customers;
    }

    private static List<String> generateCardNumbers(int numberCards) {
        int counter = 0;
        Pattern visaMasterCardAmex = Pattern.compile("(\\d{4}-){3}\\d{4}");
        List<String> cardNumbers = new ArrayList<>(numberCards);
        Finance finance = new Faker().finance();
        while (counter < numberCards) {
            String cardNumber = finance.creditCard();
            if (visaMasterCardAmex.matcher(cardNumber).matches()) {
                cardNumbers.add(cardNumber);
                counter++;
            }
        }
        return cardNumbers;
    }

    public static void produceTransactionsData() {
        produceTransactionData(
                100,
                10,
                100);
    }

    public static void produceTransactionData(int numberPurchases, int numberIterations, int numberCustomers) {
        Runnable generateTask = () -> {
            init();
            int counter = 0;
            while (counter++ < numberIterations  && keepRunning) {
                List<Transaction> transactions = generateTransactions(numberPurchases, numberCustomers);
                List<String> jsonValues = convertToJson(transactions);
                for (String value : jsonValues) {
                    Gson gson = new Gson();
                    String key = gson.fromJson(value, Transaction.class).getCustomerId();
                    ProducerRecord<String, String> record = new ProducerRecord<>(TRANSACTIONS_TOPIC, key, value);
                    producer.send(record, callback);
                }
                logger.info("Record batch sent");
                try {
                    Thread.sleep(6000);
                } catch (InterruptedException e) {
                    Thread.interrupted();
                }
            }
            logger.info("Done generating transaction data");

        };
        executorService.submit(generateTask);
    }

    public static void shutdown() {
        logger.info("Shutting down data generation");
        keepRunning = false;
        if (Objects.nonNull(executorService)) {
            executorService.shutdownNow();
            executorService = null;
        }
        if (Objects.nonNull(producer)) {
            producer.close();
            producer = null;
        }

    }

    private static void init() {
        if (Objects.isNull(producer)) {
            logger.info("Initializing the producer");
            Properties properties = new Properties();
            properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, ApplicationConstants.BOOTSTRAP_SERVERS);
            properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
            properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
            properties.put(ProducerConfig.ACKS_CONFIG, "1");
            properties.put(ProducerConfig.RETRIES_CONFIG, "3");

            producer = new KafkaProducer<>(properties);

            callback = (metadata, exception) -> {
                if (Objects.nonNull(exception)) {
                    exception.printStackTrace();
                }
            };
            logger.info("Producer initialized");
        }
    }


    private static <T> List<String> convertToJson(List<T> generatedDataItems) {
        List<String> jsonList = new ArrayList<>();
        for (T generatedData : generatedDataItems) {
            jsonList.add(convertToJson(generatedData));
        }
        return jsonList;
    }

    private static <T> String convertToJson(T generatedDataItem) {
        return gson.toJson(generatedDataItem);
    }
}
