package com.atm;

import com.atm.cli.ATMConsoleApp;
import com.atm.model.ATMVault;
import com.atm.service.ATMService;
import com.atm.service.BankService;
import com.atm.ui.ATMFrame;

import javax.swing.*;

public class ATMApplication {

    public static void main(String[] args) {
        boolean cliMode = false;
        for (String arg : args) {
            if ("--cli".equalsIgnoreCase(arg) || "-c".equalsIgnoreCase(arg)) {
                cliMode = true;
                break;
            }
        }

        BankService bankService = new BankService();
        // Initialize ATM Cash Vault ($50,000 total inventory)
        ATMVault atmVault = new ATMVault(300, 200, 400, 200);
        ATMService atmService = new ATMService(bankService, atmVault);

        if (cliMode) {
            ATMConsoleApp consoleApp = new ATMConsoleApp(atmService);
            consoleApp.start();
        } else {
            SwingUtilities.invokeLater(() -> {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception ignored) {
                }
                ATMFrame frame = new ATMFrame(atmService);
                frame.setVisible(true);
            });
        }
    }
}
