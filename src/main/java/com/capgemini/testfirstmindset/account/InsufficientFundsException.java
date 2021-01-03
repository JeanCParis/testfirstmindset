package com.capgemini.testfirstmindset.account;

public class InsufficientFundsException extends Exception {
    public InsufficientFundsException(float balance, float amount) {
        super(String.format("Insufficient funds : withdraw of %s requested while only %s available", amount, balance));
    }
}
