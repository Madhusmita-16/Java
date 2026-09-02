package com.atm.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction {

    public enum Type {
        DEPOSIT,
        WITHDRAWAL,
        TRANSFER_SENT,
        TRANSFER_RECEIVED,
        BALANCE_INQUIRY
    }

    private static long idCounter = 1000;

    private final String transactionId;
    private final LocalDateTime timestamp;
    private final Type type;
    private final double amount;
    private final double balanceAfter;
    private final String targetAccount;
    private final String description;

    public Transaction(Type type, double amount, double balanceAfter, String targetAccount, String description) {
        this.transactionId = "TXN" + (++idCounter);
        this.timestamp = LocalDateTime.now();
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.targetAccount = targetAccount;
        this.description = description;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getFormattedTimestamp() {
        return timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public Type getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public double getBalanceAfter() {
        return balanceAfter;
    }

    public String getTargetAccount() {
        return targetAccount;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return String.format("[%s] %-18s | Amt: ₹%9.2f | Bal: ₹10.2f | %s",
                getFormattedTimestamp(), type, amount, balanceAfter, description);
    }
}
