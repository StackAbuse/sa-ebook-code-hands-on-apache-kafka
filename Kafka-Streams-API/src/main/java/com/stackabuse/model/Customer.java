package com.stackabuse.model;

public class Customer {
    private final String firstName;
    private final String lastName;
    private final String customerId;
    private final String cardNumber;

    public Customer(String firstName, String lastName, String customerId, String cardNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.customerId = customerId;
        this.cardNumber = cardNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getCardNumber() {
        return cardNumber;
    }
}
