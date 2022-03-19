package com.stackabuse.model;

import java.util.Date;
import java.util.List;

public class CorrelatedTransaction {

    private final String customerName;
    private final List<String> itemsPurchased;
    private final double totalAmount;
    private final Date firstPurchaseTime;
    private final Date secondPurchaseTime;

    private CorrelatedTransaction(Builder builder) {
        customerName = builder.customerName;
        itemsPurchased = builder.itemsPurchased;
        totalAmount = builder.totalAmount;
        firstPurchaseTime = builder.firstPurchasedItem;
        secondPurchaseTime = builder.secondPurchasedItem;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public String getCustomerName() {
        return customerName;
    }

    public List<String> getItemsPurchased() {
        return itemsPurchased;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public Date getFirstPurchaseTime() {
        return firstPurchaseTime;
    }

    public Date getSecondPurchaseTime() {
        return secondPurchaseTime;
    }


    @Override
    public String toString() {
        return "CorrelatedTransaction{" +
                "customerId='" + customerName + '\'' +
                ", itemsPurchased=" + itemsPurchased +
                ", totalAmount=" + totalAmount +
                ", firstPurchaseTime=" + firstPurchaseTime +
                ", secondPurchaseTime=" + secondPurchaseTime +
                '}';
    }

    public static final class Builder {
        private String customerName;
        private List<String> itemsPurchased;
        private double totalAmount;
        private Date firstPurchasedItem;
        private Date secondPurchasedItem;

        private Builder() {
        }

        public Builder withCustomerName(String val) {
            customerName = val;
            return this;
        }

        public Builder withItemsPurchased(List<String> val) {
            itemsPurchased = val;
            return this;
        }

        public Builder withTotalAmount(double val) {
            totalAmount = val;
            return this;
        }

        public Builder withFirstPurchaseDate(Date val) {
            firstPurchasedItem = val;
            return this;
        }

        public Builder withSecondPurchaseDate(Date val) {
            secondPurchasedItem = val;
            return this;
        }

        public CorrelatedTransaction build() {
            return new CorrelatedTransaction(this);
        }
    }
}
