package com.atm.cli;

import com.atm.model.Account;
import com.atm.model.Transaction;
import com.atm.service.ATMService;

import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class ATMConsoleApp {

    private final ATMService atmService;
    private final Scanner scanner;

    public ATMConsoleApp(ATMService atmService) {
        this.atmService = atmService;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        System.out.println("=================================================");
        System.out.println("     NEXUS GLOBAL BANK - ATM CONSOLE TERMINAL    ");
        System.out.println("=================================================");

        while (true) {
            if (!atmService.isLoggedIn()) {
                if (!showLoginMenu()) {
                    break;
                }
            } else {
                showMainMenu();
            }
        }

        System.out.println("\nThank you for using Nexus ATM. Goodbye!");
    }

    private boolean showLoginMenu() {
        System.out.println("\n--- ATM SYSTEM READY ---");
        System.out.print("Enter Account Number (or 'exit' to quit): ");
        String acc = scanner.nextLine().trim();
        if (acc.equalsIgnoreCase("exit") || acc.equalsIgnoreCase("quit")) {
            return false;
        }

        System.out.print("Enter 4-Digit PIN: ");
        String pin = scanner.nextLine().trim();

        try {
            atmService.login(acc, pin);
            Account a = atmService.getActiveAccount();
            System.out.printf("\n[SUCCESS] Welcome, %s! (Account #%s)\n", a.getHolderName(), a.getAccountNumber());
        } catch (Exception ex) {
            System.out.println("[ERROR] " + ex.getMessage());
        }

        return true;
    }

    private void showMainMenu() {
        Account a = atmService.getActiveAccount();
        System.out.println("\n-------------------------------------------------");
        System.out.printf("  ACCOUNT: #%s (%s) | BAL: %s\n",
                a.getAccountNumber(), a.getHolderName(), formatRupees(a.getBalance()));
        System.out.println("-------------------------------------------------");
        System.out.println("1. Check Balance");
        System.out.println("2. Withdraw Cash");
        System.out.println("3. Deposit Cash");
        System.out.println("4. Transfer Funds");
        System.out.println("5. Print Mini Statement");
        System.out.println("6. Logout / Eject Card");
        System.out.print("Select Operation (1-6): ");

        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1" -> {
                double bal = atmService.checkBalance();
                System.out.println("\n-> Current Available Balance: " + formatRupees(bal));
            }
            case "2" -> {
                System.out.print("Enter Withdrawal Amount (₹): ");
                try {
                    double amt = Double.parseDouble(scanner.nextLine().trim());
                    atmService.withdrawCash(amt);
                    System.out.println("[SUCCESS] Cash dispensed! New Balance: " + formatRupees(atmService.getActiveAccount().getBalance()));
                } catch (Exception ex) {
                    System.out.println("[ERROR] " + ex.getMessage());
                }
            }
            case "3" -> {
                System.out.print("Enter Envelope Deposit Amount (₹): ");
                try {
                    double amt = Double.parseDouble(scanner.nextLine().trim());
                    atmService.depositCash(amt);
                    System.out.println("[SUCCESS] Deposit completed! New Balance: " + formatRupees(atmService.getActiveAccount().getBalance()));
                } catch (Exception ex) {
                    System.out.println("[ERROR] " + ex.getMessage());
                }
            }
            case "4" -> {
                System.out.print("Enter Recipient Account Number: ");
                String targetAcc = scanner.nextLine().trim();
                System.out.print("Enter Transfer Amount (₹): ");
                try {
                    double amt = Double.parseDouble(scanner.nextLine().trim());
                    atmService.transferFunds(targetAcc, amt);
                    System.out.println("[SUCCESS] Transfer successful! New Balance: " + formatRupees(atmService.getActiveAccount().getBalance()));
                } catch (Exception ex) {
                    System.out.println("[ERROR] " + ex.getMessage());
                }
            }
            case "5" -> {
                System.out.println("\n--- MINI STATEMENT (RECENT TRANSACTIONS) ---");
                List<Transaction> txns = atmService.getMiniStatement();
                for (Transaction t : txns) {
                    System.out.println(t);
                }
            }
            case "6" -> {
                atmService.logout();
                System.out.println("\n[INFO] Logged out successfully.");
            }
            default -> System.out.println("[INVALID] Invalid option selected.");
        }
    }

    private String formatRupees(double val) {
        return "₹" + String.format(Locale.US, "%,.2f", val);
    }
}
