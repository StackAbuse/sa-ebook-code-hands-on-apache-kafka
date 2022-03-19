package com.stackabuse.joiner;

import com.stackabuse.model.CorrelatedTransaction;
import com.stackabuse.model.Transaction;
import org.apache.kafka.streams.kstream.ValueJoiner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class TransactionJoiner implements ValueJoiner<Transaction, Transaction, CorrelatedTransaction> {

    @Override
    public CorrelatedTransaction apply(Transaction transaction, Transaction otherTransaction) {

        CorrelatedTransaction.Builder builder = CorrelatedTransaction.newBuilder();

        Date purchaseDate = Objects.nonNull(transaction) ? transaction.getPurchaseDate() : null;
        Double price = Objects.nonNull(transaction) ? transaction.getPrice() : 0.0;
        String itemPurchased = Objects.nonNull(transaction) ? transaction.getItemPurchased() : null;

        Date otherPurchaseDate = Objects.nonNull(otherTransaction) ? otherTransaction.getPurchaseDate() : null;
        Double otherPrice = Objects.nonNull(otherTransaction) ? otherTransaction.getPrice() : 0.0;
        String otherItemPurchased = Objects.nonNull(otherTransaction) ? otherTransaction.getItemPurchased() : null;

        List<String> purchasedItems = new ArrayList<>();

        if (Objects.nonNull(itemPurchased)) {
            purchasedItems.add(itemPurchased);
        }

        if (Objects.nonNull(otherItemPurchased)) {
            purchasedItems.add(otherItemPurchased);
        }

        String customerName = Objects.nonNull(transaction)
                ? transaction.getFirstName() + " " + transaction.getLastName() : null;
        String otherCustomerName = Objects.nonNull(otherTransaction)
                ? otherTransaction.getFirstName() + " " + otherTransaction.getLastName() : null;

        builder.withCustomerName(Objects.nonNull(customerName) ? customerName : otherCustomerName)
                .withFirstPurchaseDate(purchaseDate)
                .withSecondPurchaseDate(otherPurchaseDate)
                .withItemsPurchased(purchasedItems)
                .withTotalAmount(price + otherPrice);

        return builder.build();
    }
}
