package com.atm.model;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Account {

    public enum AccountType {
        CHECKING,
        SAVINGS
    }

    private final String accountNumber;
    private final String holderName;
    private final AccountType accountType;
    private String pinHash;
    private double balance;
    private boolean locked;
    private int failedPinAttempts;
    private final List<Transaction> transactions;

    public Account(String accountNumber, String pin, String holderName, AccountType accountType, double initialBalance) {
        this.accountNumber = accountNumber;
        this.pinHash = hashPin(pin);
        this.holderName = holderName;
        this.accountType = accountType;
        this.balance = Math.max(0, initialBalance);
        this.locked = false;
        this.failedPinAttempts = 0;
        this.transactions = Collections.synchronizedList(new ArrayList<>());

        if (initialBalance > 0) {
            this.transactions.add(new Transaction(
                    Transaction.Type.DEPOSIT,
                    initialBalance,
                    this.balance,
                    null,
                    "Initial Account Opening Deposit"
            ));
        }
    }

    public boolean validatePin(String inputPin) {
        if (locked) {
            return false;
        }

        boolean isValid = hashPin(inputPin).equals(this.pinHash);
        if (isValid) {
            this.failedPinAttempts = 0;
        } else {
            this.failedPinAttempts++;
            if (this.failedPinAttempts >= 3) {
                this.locked = true;
            }
        }
        return isValid;
    }

    public synchronized void deposit(double amount, String description) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive.");
        }
        this.balance += amount;
        Transaction txn = new Transaction(
                Transaction.Type.DEPOSIT,
                amount,
                this.balance,
                null,
                description != null ? description : "Cash Deposit"
        );
        this.transactions.add(txn);
    }

    public synchronized void withdraw(double amount, String description) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive.");
        }
        if (amount > this.balance) {
            throw new IllegalStateException("Insufficient account funds.");
        }
        this.balance -= amount;
        Transaction txn = new Transaction(
                Transaction.Type.WITHDRAWAL,
                amount,
                this.balance,
                null,
                description != null ? description : "ATM Cash Withdrawal"
        );
        this.transactions.add(txn);
    }

    public synchronized void transferTo(Account targetAccount, double amount) {
        if (targetAccount == null) {
            throw new IllegalArgumentException("Target account does not exist.");
        }
        if (targetAccount.getAccountNumber().equals(this.accountNumber)) {
            throw new IllegalArgumentException("Cannot transfer funds to the same account.");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive.");
        }
        if (amount > this.balance) {
            throw new IllegalStateException("Insufficient balance for fund transfer.");
        }

        // Deduct from sender
        this.balance -= amount;
        Transaction senderTxn = new Transaction(
                Transaction.Type.TRANSFER_SENT,
                amount,
                this.balance,
                targetAccount.getAccountNumber(),
                "Transfer to " + targetAccount.getHolderName() + " (" + targetAccount.getAccountNumber() + ")"
        );
        this.transactions.add(senderTxn);

        // Credit to recipient
        synchronized (targetAccount) {
            targetAccount.balance += amount;
            Transaction recipientTxn = new Transaction(
                    Transaction.Type.TRANSFER_RECEIVED,
                    amount,
                    targetAccount.balance,
                    this.accountNumber,
                    "Transfer received from " + this.holderName + " (" + this.accountNumber + ")"
            );
            targetAccount.transactions.add(recipientTxn);
        }
    }

    public void addBalanceInquiryRecord() {
        Transaction txn = new Transaction(
                Transaction.Type.BALANCE_INQUIRY,
                0.0,
                this.balance,
                null,
                "ATM Balance Inquiry"
        );
        this.transactions.add(txn);
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getHolderName() {
        return holderName;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public double getBalance() {
        return balance;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
        if (!locked) {
            this.failedPinAttempts = 0;
        }
    }

    public int getFailedPinAttempts() {
        return failedPinAttempts;
    }

    public List<Transaction> getTransactions() {
        synchronized (transactions) {
            return new ArrayList<>(transactions);
        }
    }

    public static String hashPin(String pin) {
        if (pin == null) return "";
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(pin.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            return String.valueOf(pin.hashCode());
        }
    }
}
