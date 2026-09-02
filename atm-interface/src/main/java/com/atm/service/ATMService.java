package com.atm.service;

import com.atm.model.Account;
import com.atm.model.ATMVault;
import com.atm.model.Transaction;

import java.util.List;

public class ATMService {

    private final BankService bankService;
    private final ATMVault atmVault;
    private Account activeAccount;
    private double sessionWithdrawnTotal = 0.0;
    public static final double DAILY_WITHDRAWAL_LIMIT = 1000.0;

    public ATMService(BankService bankService, ATMVault atmVault) {
        this.bankService = bankService;
        this.atmVault = atmVault;
    }

    public boolean login(String accountNumber, String pin) {
        this.activeAccount = bankService.authenticate(accountNumber, pin);
        this.sessionWithdrawnTotal = 0.0;
        return true;
    }

    public void logout() {
        this.activeAccount = null;
        this.sessionWithdrawnTotal = 0.0;
    }

    public boolean isLoggedIn() {
        return activeAccount != null;
    }

    public Account getActiveAccount() {
        if (!isLoggedIn()) {
            throw new IllegalStateException("No active account logged into ATM session.");
        }
        return activeAccount;
    }

    public double checkBalance() {
        Account account = getActiveAccount();
        account.addBalanceInquiryRecord();
        return account.getBalance();
    }

    public synchronized void withdrawCash(double amount) {
        Account account = getActiveAccount();

        if (amount <= 0 || amount % 10 != 0) {
            throw new IllegalArgumentException("Withdrawal amount must be a multiple of $10.");
        }

        if (sessionWithdrawnTotal + amount > DAILY_WITHDRAWAL_LIMIT) {
            double remainingLimit = DAILY_WITHDRAWAL_LIMIT - sessionWithdrawnTotal;
            throw new IllegalArgumentException("Daily withdrawal limit exceeded ($1,000.00 max). Remaining limit: $" + String.format("%.2f", Math.max(0, remainingLimit)));
        }

        if (amount > account.getBalance()) {
            throw new IllegalStateException("Insufficient account balance. Available: $" + String.format("%.2f", account.getBalance()));
        }

        if (!atmVault.canDispense(amount)) {
            throw new IllegalStateException("ATM Vault cannot dispense this exact amount with current bill inventory. Available Vault Cash: $" + String.format("%.2f", atmVault.getTotalCashAvailable()));
        }

        // Perform withdrawal from account
        account.withdraw(amount, "ATM Cash Withdrawal");

        // Dispense cash from vault
        boolean dispensed = atmVault.dispense(amount);
        if (!dispensed) {
            // Revert if bill combination fails
            account.deposit(amount, "Reversal: ATM Dispense Failure");
            throw new IllegalStateException("Failed to dispense bill denominations.");
        }

        sessionWithdrawnTotal += amount;
    }

    public void depositCash(double amount) {
        Account account = getActiveAccount();

        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive.");
        }

        account.deposit(amount, "ATM Envelope Cash Deposit");
        // Replenish vault dynamically
        atmVault.replenish((int) (amount / 100), 0, 0, 0);
    }

    public void transferFunds(String targetAccountNumber, double amount) {
        Account account = getActiveAccount();

        if (targetAccountNumber == null || targetAccountNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Target account number is required.");
        }

        bankService.transfer(account.getAccountNumber(), targetAccountNumber.trim(), amount);
    }

    public List<Transaction> getMiniStatement() {
        Account account = getActiveAccount();
        List<Transaction> all = account.getTransactions();
        int fromIndex = Math.max(0, all.size() - 10);
        return all.subList(fromIndex, all.size());
    }

    public ATMVault getAtmVault() {
        return atmVault;
    }
}
