package com.atm.service;

import com.atm.model.Account;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class BankService {

    private final Map<String, Account> accounts = new ConcurrentHashMap<>();

    public BankService() {
        seedDefaultAccounts();
    }

    private void seedDefaultAccounts() {
        // Pre-seeded demo bank accounts
        registerAccount(new Account("1001", "1234", "John Doe", Account.AccountType.CHECKING, 2500.00));
        registerAccount(new Account("1002", "5678", "Jane Smith", Account.AccountType.SAVINGS, 10500.50));
        registerAccount(new Account("1003", "1111", "Alex Johnson", Account.AccountType.CHECKING, 750.25));
        registerAccount(new Account("9999", "9999", "ATM Administrator", Account.AccountType.SAVINGS, 50000.00));
    }

    public void registerAccount(Account account) {
        if (account == null || account.getAccountNumber() == null) {
            throw new IllegalArgumentException("Invalid account object.");
        }
        accounts.put(account.getAccountNumber(), account);
    }

    public Optional<Account> findAccount(String accountNumber) {
        if (accountNumber == null) return Optional.empty();
        return Optional.ofNullable(accounts.get(accountNumber.trim()));
    }

    public Account authenticate(String accountNumber, String pin) {
        Account account = findAccount(accountNumber)
                .orElseThrow(() -> new IllegalArgumentException("Account number not found."));

        if (account.isLocked()) {
            throw new IllegalStateException("Account is LOCKED due to multiple failed PIN attempts. Contact bank support.");
        }

        if (!account.validatePin(pin)) {
            int remaining = 3 - account.getFailedPinAttempts();
            if (account.isLocked()) {
                throw new IllegalStateException("Account has been LOCKED after 3 consecutive failed PIN attempts.");
            } else {
                throw new IllegalArgumentException("Incorrect PIN. " + remaining + " attempt(s) remaining before account lock.");
            }
        }

        return account;
    }

    public void transfer(String fromAccountNumber, String toAccountNumber, double amount) {
        Account sender = findAccount(fromAccountNumber)
                .orElseThrow(() -> new IllegalArgumentException("Sender account not found."));
        Account recipient = findAccount(toAccountNumber)
                .orElseThrow(() -> new IllegalArgumentException("Recipient account not found."));

        sender.transferTo(recipient, amount);
    }

    public Collection<Account> getAllAccounts() {
        return accounts.values();
    }
}
