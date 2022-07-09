package com.stackabuse.model;

import java.util.Date;
import java.util.Objects;

public class Transaction {

    private final String firstName;
    private final String lastName;
    private final String cardNumber;
    private final String customerId;
    private final String itemPurchased;
    private final String orderId;
    int quantity;
    double price;
    private final Date purchaseDate;
    private final String zipCode;

    private Transaction(Builder builder) {
        firstName = builder.firstName;
        lastName = builder.lastName;
        cardNumber = builder.cardNumber;
        customerId = builder.customerId;
        itemPurchased = builder.itemPurchased;
        orderId = builder.orderId;
        quantity = builder.quantity;
        price = builder.price;
        purchaseDate = builder.purchaseDate;
        zipCode = builder.zipCode;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(Transaction copy) {
        Builder builder = new Builder();
        builder.firstName = copy.firstName;
        builder.lastName = copy.lastName;
        builder.cardNumber = copy.cardNumber;
        builder.customerId = copy.customerId;
        builder.itemPurchased = copy.itemPurchased;
        builder.orderId = copy.orderId;
        builder.quantity = copy.quantity;
        builder.price = copy.price;
        builder.purchaseDate = copy.purchaseDate;
        builder.zipCode = copy.zipCode;
        return builder;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getCustomerId() { return customerId; }

    public String getItemPurchased() {
        return itemPurchased;
    }

    public String getOrderId() {
        return orderId;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public Date getPurchaseDate() {
        return purchaseDate;
    }

    public String getZipCode() {
        return zipCode;
    }

    @Override
    public String toString() {
        return "Purchase{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", cardNumber='" + cardNumber + '\'' +
                ", customerId='" + customerId + '\'' +
                ", itemPurchased='" + itemPurchased + '\'' +
                ", orderId='" + orderId + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                ", purchaseDate=" + purchaseDate +
                ", zipCode='" + zipCode + '\'' +
                '}';
    }

    public static final class Builder {
        private String firstName;
        private String lastName;
        private String cardNumber;
        private String customerId;
        private String itemPurchased;
        private String orderId;
        private int quantity;
        private double price;
        private Date purchaseDate;
        private String zipCode;

        private static final String CC_NUMBER_REPLACEMENT="xxxx-xxxx-xxxx-";

        private Builder() {
        }

        public Builder firstName(String val) {
            firstName = val;
            return this;
        }

        public Builder lastName(String val) {
            lastName = val;
            return this;
        }

        public Builder maskCard(){
            Objects.requireNonNull(this.cardNumber, "Card can't have null value");
            String last4Digits = this.cardNumber.split("-")[3];
            this.cardNumber = CC_NUMBER_REPLACEMENT+last4Digits;
            return this;
        }

        public Builder cardNumber(String val) {
            cardNumber = val;
            return this;
        }

        public Builder customerId(String val) {
            customerId = val;
            return this;
        }

        public Builder itemPurchased(String val) {
            itemPurchased = val;
            return this;
        }

        public Builder orderId(String val) {
            orderId = val;
            return this;
        }

        public Builder quantity(int val) {
            quantity = val;
            return this;
        }

        public Builder price(double val) {
            price = val;
            return this;
        }

        public Builder purchaseDate(Date val) {
            purchaseDate = val;
            return this;
        }

        public Builder zipCode(String val) {
            zipCode = val;
            return this;
        }

        public Transaction build() {
            return new Transaction(this);
        }
    }
}
