package com.stackabuse.model;

import java.util.Date;

public class TransactionPattern {

    private final String zipCode;
    private final String item;
    private final Date date;

    private TransactionPattern(Builder builder) {
        zipCode = builder.zipCode;
        item = builder.item;
        date = builder.date;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Builder builder(Transaction transaction){
        return new Builder(transaction);

    }
    public String getZipCode() {
        return zipCode;
    }

    public String getItem() {
        return item;
    }

    public Date getDate() {
        return date;
    }


    @Override
    public String toString() {
        return "TransactionPattern{" +
                "zipCode='" + zipCode + '\'' +
                ", item='" + item + '\'' +
                ", date=" + date +
                '}';
    }

    public static final class Builder {
        private String zipCode;
        private String item;
        private Date date;

        private  Builder() {
        }

        private Builder(Transaction transaction) {
            this.zipCode = transaction.getZipCode();
            this.item = transaction.getItemPurchased();
            this.date = transaction.getPurchaseDate();
        }

        public Builder zipCode(String val) {
            zipCode = val;
            return this;
        }

        public Builder item(String val) {
            item = val;
            return this;
        }

        public Builder date(Date val) {
            date = val;
            return this;
        }

        public TransactionPattern build() {
            return new TransactionPattern(this);
        }
    }
}
