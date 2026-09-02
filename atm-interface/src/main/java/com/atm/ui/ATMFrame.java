package com.atm.ui;

import com.atm.model.Account;
import com.atm.model.Transaction;
import com.atm.service.ATMService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ATMFrame extends JFrame {

    private final ATMService atmService;
    private final CardLayout cardLayout;
    private final JPanel mainScreenPanel;

    // Screen LCD components
    private JLabel lcdStatusLabel;
    private JTextArea lcdMessageArea;

    // Login Screen inputs
    private JTextField loginAccInput;
    private JPasswordField loginPinInput;

    // Dashboard Info
    private JLabel dashWelcomeLabel;
    private JLabel dashAccNumLabel;
    private JLabel dashBalanceLabel;

    // Keypad Active Field reference
    private JTextField activeKeypadTarget;

    // Colors
    private static final Color BG_DARK = new Color(13, 17, 23);
    private static final Color PANEL_BG = new Color(22, 27, 34);
    private static final Color LCD_BG = new Color(9, 13, 22);
    private static final Color ACCENT_CYAN = new Color(88, 166, 255);
    private static final Color ACCENT_GREEN = new Color(46, 160, 67);
    private static final Color ACCENT_GOLD = new Color(210, 153, 34);
    private static final Color ACCENT_RED = new Color(248, 81, 73);
    private static final Color TEXT_LIGHT = new Color(240, 246, 252);
    private static final Color TEXT_MUTED = new Color(139, 148, 158);

    public ATMFrame(ATMService atmService) {
        this.atmService = atmService;
        this.cardLayout = new CardLayout();
        this.mainScreenPanel = new JPanel(cardLayout);

        setTitle("NEXUS BANK — NextGen ATM Terminal");
        setSize(950, 720);
        setMinimumSize(new Dimension(850, 650));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_DARK);

        initUI();
    }

    private void initUI() {
        JPanel rootPanel = new JPanel(new BorderLayout(15, 15));
        rootPanel.setBackground(BG_DARK);
        rootPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // --- Top Header ---
        JPanel headerPanel = createHeaderPanel();
        rootPanel.add(headerPanel, BorderLayout.NORTH);

        // --- Center Screen & Keypad Split ---
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        centerPanel.setOpaque(false);

        // Left Column: Electronic LCD Screen Panel
        JPanel lcdScreenContainer = createLCDScreenPanel();
        centerPanel.add(lcdScreenContainer);

        // Right Column: Tactile Hardware Keypad & Quick Cash Panel
        JPanel keypadContainer = createKeypadPanel();
        centerPanel.add(keypadContainer);

        rootPanel.add(centerPanel, BorderLayout.CENTER);

        // --- Bottom Status Bar ---
        JPanel footerPanel = createFooterPanel();
        rootPanel.add(footerPanel, BorderLayout.SOUTH);

        add(rootPanel);

        // Show initial login card
        showScreenCard("LOGIN");
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PANEL_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(48, 54, 61), 1, true),
                new EmptyBorder(12, 20, 12, 20)
        ));

        JLabel titleLabel = new JLabel("🏦 NEXUS GLOBAL BANK ATM");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(ACCENT_CYAN);

        JLabel subTitleLabel = new JLabel("Terminal ID: #ATM-8092 • Secure 256-bit Encrypted Session");
        subTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subTitleLabel.setForeground(TEXT_MUTED);

        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(subTitleLabel, BorderLayout.EAST);
        return panel;
    }

    private JPanel createLCDScreenPanel() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(LCD_BG);
        container.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(ACCENT_CYAN, 2, true),
                new EmptyBorder(15, 15, 15, 15)
        ));

        // LCD Status Header
        lcdStatusLabel = new JLabel("● SYSTEM READY — INSERT ACCOUNT & PIN", SwingConstants.CENTER);
        lcdStatusLabel.setFont(new Font("Monospaced", Font.BOLD, 13));
        lcdStatusLabel.setForeground(ACCENT_GREEN);
        container.add(lcdStatusLabel, BorderLayout.NORTH);

        // Screen Cards
        mainScreenPanel.setOpaque(false);
        mainScreenPanel.add(createLoginCard(), "LOGIN");
        mainScreenPanel.add(createMenuCard(), "MENU");
        mainScreenPanel.add(createWithdrawCard(), "WITHDRAW");
        mainScreenPanel.add(createDepositCard(), "DEPOSIT");
        mainScreenPanel.add(createTransferCard(), "TRANSFER");
        mainScreenPanel.add(createStatementCard(), "STATEMENT");

        container.add(mainScreenPanel, BorderLayout.CENTER);

        return container;
    }

    private JPanel createLoginCard() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel welcomeTitle = new JLabel("Welcome to Nexus ATM", SwingConstants.CENTER);
        welcomeTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        welcomeTitle.setForeground(TEXT_LIGHT);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(welcomeTitle, gbc);

        JLabel subHint = new JLabel("Demo Acc: 1001 (PIN: 1234) | Acc: 1002 (PIN: 5678)", SwingConstants.CENTER);
        subHint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        subHint.setForeground(ACCENT_GOLD);
        gbc.gridy = 1;
        panel.add(subHint, gbc);

        gbc.gridwidth = 1;

        // Account Number Input
        JLabel accLabel = new JLabel("Account Number:");
        accLabel.setForeground(TEXT_LIGHT);
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(accLabel, gbc);

        loginAccInput = new JTextField(12);
        styleTextField(loginAccInput);
        loginAccInput.setText("1001");
        loginAccInput.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) { activeKeypadTarget = loginAccInput; }
        });
        gbc.gridx = 1;
        panel.add(loginAccInput, gbc);

        // PIN Input
        JLabel pinLabel = new JLabel("4-Digit PIN:");
        pinLabel.setForeground(TEXT_LIGHT);
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(pinLabel, gbc);

        loginPinInput = new JPasswordField(12);
        styleTextField(loginPinInput);
        loginPinInput.setText("1234");
        loginPinInput.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) { activeKeypadTarget = loginPinInput; }
        });
        gbc.gridx = 1;
        panel.add(loginPinInput, gbc);

        // Submit Login Button
        JButton loginBtn = createStyledButton("AUTHENTICATE & ENTER", ACCENT_CYAN);
        loginBtn.addActionListener(e -> handleLogin());
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; gbc.insets = new Insets(15, 8, 8, 8);
        panel.add(loginBtn, gbc);

        activeKeypadTarget = loginAccInput;
        return panel;
    }

    private JPanel createMenuCard() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);

        // User Info Header
        JPanel infoPanel = new JPanel(new GridLayout(3, 1, 4, 4));
        infoPanel.setOpaque(false);
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(48, 54, 61), 1, true),
                new EmptyBorder(8, 12, 8, 12)
        ));

        dashWelcomeLabel = new JLabel("Welcome, Valued Customer");
        dashWelcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        dashWelcomeLabel.setForeground(TEXT_LIGHT);

        dashAccNumLabel = new JLabel("Account: #XXXX");
        dashAccNumLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dashAccNumLabel.setForeground(TEXT_MUTED);

        dashBalanceLabel = new JLabel("Available Balance: $0.00");
        dashBalanceLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        dashBalanceLabel.setForeground(ACCENT_GREEN);

        infoPanel.add(dashWelcomeLabel);
        infoPanel.add(dashAccNumLabel);
        infoPanel.add(dashBalanceLabel);

        panel.add(infoPanel, BorderLayout.NORTH);

        // Actions Grid
        JPanel grid = new JPanel(new GridLayout(3, 2, 10, 10));
        grid.setOpaque(false);

        JButton btnWithdraw = createStyledButton("💵 WITHDRAW CASH", ACCENT_CYAN);
        btnWithdraw.addActionListener(e -> showScreenCard("WITHDRAW"));

        JButton btnDeposit = createStyledButton("📥 DEPOSIT CASH", ACCENT_GREEN);
        btnDeposit.addActionListener(e -> showScreenCard("DEPOSIT"));

        JButton btnTransfer = createStyledButton("🔄 FUND TRANSFER", ACCENT_GOLD);
        btnTransfer.addActionListener(e -> showScreenCard("TRANSFER"));

        JButton btnStatement = createStyledButton("📋 MINI STATEMENT", ACCENT_CYAN);
        btnStatement.addActionListener(e -> {
            updateStatementTable();
            showScreenCard("STATEMENT");
        });

        JButton btnBalance = createStyledButton("🔍 CHECK BALANCE", new Color(137, 87, 229));
        btnBalance.addActionListener(e -> {
            double bal = atmService.checkBalance();
            updateDashboardInfo();
            JOptionPane.showMessageDialog(this,
                    "Current Available Balance: " + formatCurrency(bal),
                    "Balance Inquiry", JOptionPane.INFORMATION_MESSAGE);
        });

        JButton btnLogout = createStyledButton("🔴 EXIT / LOGOUT", ACCENT_RED);
        btnLogout.addActionListener(e -> {
            atmService.logout();
            showScreenCard("LOGIN");
            updateLcdStatus("● SYSTEM READY — INSERT ACCOUNT & PIN", ACCENT_GREEN);
        });

        grid.add(btnWithdraw);
        grid.add(btnDeposit);
        grid.add(btnTransfer);
        grid.add(btnStatement);
        grid.add(btnBalance);
        grid.add(btnLogout);

        panel.add(grid, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createWithdrawCard() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);

        JLabel title = new JLabel("Fast Cash Withdrawal", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(TEXT_LIGHT);
        panel.add(title, BorderLayout.NORTH);

        // Quick Cash Amounts
        JPanel quickGrid = new JPanel(new GridLayout(3, 2, 8, 8));
        quickGrid.setOpaque(false);

        int[] quickAmounts = {20, 50, 100, 200, 500};
        for (int amt : quickAmounts) {
            JButton qBtn = createStyledButton("$" + amt, ACCENT_CYAN);
            qBtn.addActionListener(e -> processWithdrawal(amt));
            quickGrid.add(qBtn);
        }

        panel.add(quickGrid, BorderLayout.CENTER);

        // Custom Amount Row
        JPanel customRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        customRow.setOpaque(false);

        JLabel customLabel = new JLabel("Custom Amount ($):");
        customLabel.setForeground(TEXT_LIGHT);

        JTextField customInput = new JTextField(8);
        styleTextField(customInput);
        customInput.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) { activeKeypadTarget = customInput; }
        });

        JButton customBtn = createStyledButton("WITHDRAW", ACCENT_GREEN);
        customBtn.addActionListener(e -> {
            try {
                double val = Double.parseDouble(customInput.getText().trim());
                processWithdrawal(val);
                customInput.setText("");
            } catch (NumberFormatException ex) {
                showErrorDialog("Please enter a valid numeric withdrawal amount.");
            }
        });

        JButton backBtn = createStyledButton("BACK", ACCENT_RED);
        backBtn.addActionListener(e -> showScreenCard("MENU"));

        customRow.add(customLabel);
        customRow.add(customInput);
        customRow.add(customBtn);
        customRow.add(backBtn);

        panel.add(customRow, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createDepositCard() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Cash Deposit Simulation", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(ACCENT_GREEN);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(title, gbc);

        JLabel amtLabel = new JLabel("Deposit Amount ($):");
        amtLabel.setForeground(TEXT_LIGHT);
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        panel.add(amtLabel, gbc);

        JTextField depInput = new JTextField(10);
        styleTextField(depInput);
        depInput.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) { activeKeypadTarget = depInput; }
        });
        gbc.gridx = 1;
        panel.add(depInput, gbc);

        JButton depBtn = createStyledButton("DEPOSIT CASH ENVELOPE", ACCENT_GREEN);
        depBtn.addActionListener(e -> {
            try {
                double amt = Double.parseDouble(depInput.getText().trim());
                atmService.depositCash(amt);
                updateDashboardInfo();
                depInput.setText("");
                showReceiptDialog("DEPOSIT", amt, "Cash Deposit", null);
                showScreenCard("MENU");
            } catch (Exception ex) {
                showErrorDialog(ex.getMessage());
            }
        });

        JButton backBtn = createStyledButton("CANCEL", ACCENT_RED);
        backBtn.addActionListener(e -> showScreenCard("MENU"));

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        panel.add(depBtn, gbc);

        gbc.gridy = 3;
        panel.add(backBtn, gbc);

        return panel;
    }

    private JPanel createTransferCard() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Fund Transfer to Account", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(ACCENT_GOLD);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(title, gbc);

        JLabel targetLabel = new JLabel("Recipient Account #:");
        targetLabel.setForeground(TEXT_LIGHT);
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        panel.add(targetLabel, gbc);

        JTextField targetInput = new JTextField(12);
        styleTextField(targetInput);
        targetInput.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) { activeKeypadTarget = targetInput; }
        });
        gbc.gridx = 1;
        panel.add(targetInput, gbc);

        JLabel amtLabel = new JLabel("Transfer Amount ($):");
        amtLabel.setForeground(TEXT_LIGHT);
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(amtLabel, gbc);

        JTextField transferAmtInput = new JTextField(12);
        styleTextField(transferAmtInput);
        transferAmtInput.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) { activeKeypadTarget = transferAmtInput; }
        });
        gbc.gridx = 1;
        panel.add(transferAmtInput, gbc);

        JButton sendBtn = createStyledButton("SEND TRANSFER NOW", ACCENT_GOLD);
        sendBtn.addActionListener(e -> {
            try {
                String targetAcc = targetInput.getText().trim();
                double amt = Double.parseDouble(transferAmtInput.getText().trim());
                atmService.transferFunds(targetAcc, amt);
                updateDashboardInfo();
                targetInput.setText("");
                transferAmtInput.setText("");
                showReceiptDialog("TRANSFER", amt, "Fund Transfer to Account #" + targetAcc, targetAcc);
                showScreenCard("MENU");
            } catch (Exception ex) {
                showErrorDialog(ex.getMessage());
            }
        });

        JButton backBtn = createStyledButton("CANCEL", ACCENT_RED);
        backBtn.addActionListener(e -> showScreenCard("MENU"));

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        panel.add(sendBtn, gbc);

        gbc.gridy = 4;
        panel.add(backBtn, gbc);

        return panel;
    }

    private DefaultTableModel statementTableModel;

    private JPanel createStatementCard() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setOpaque(false);

        JLabel title = new JLabel("Recent Transaction History (Mini Statement)", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 15));
        title.setForeground(TEXT_LIGHT);
        panel.add(title, BorderLayout.NORTH);

        String[] cols = {"Date / Time", "Type", "Amount ($)", "Balance ($)"};
        statementTableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };

        JTable table = new JTable(statementTableModel);
        table.setBackground(PANEL_BG);
        table.setForeground(TEXT_LIGHT);
        table.setGridColor(new Color(48, 54, 61));
        table.getTableHeader().setBackground(new Color(33, 38, 45));
        table.getTableHeader().setForeground(ACCENT_CYAN);
        table.setRowHeight(22);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setOpaque(false);
        scroll.getViewport().setBackground(PANEL_BG);
        panel.add(scroll, BorderLayout.CENTER);

        JButton backBtn = createStyledButton("RETURN TO MAIN MENU", ACCENT_CYAN);
        backBtn.addActionListener(e -> showScreenCard("MENU"));
        panel.add(backBtn, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createKeypadPanel() {
        JPanel container = new JPanel(new BorderLayout(10, 10));
        container.setBackground(PANEL_BG);
        container.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(48, 54, 61), 1, true),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel keypadHeader = new JLabel("TOUCH KEYPAD", SwingConstants.CENTER);
        keypadHeader.setFont(new Font("Segoe UI", Font.BOLD, 14));
        keypadHeader.setForeground(TEXT_MUTED);
        container.add(keypadHeader, BorderLayout.NORTH);

        // 3x4 Grid Keypad
        JPanel grid = new JPanel(new GridLayout(4, 3, 8, 8));
        grid.setOpaque(false);

        String[] keys = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "CLEAR", "0", "OK"};
        for (String k : keys) {
            JButton btn;
            if ("CLEAR".equals(k)) {
                btn = createStyledButton("CLEAR", ACCENT_GOLD);
                btn.addActionListener(e -> {
                    if (activeKeypadTarget != null) activeKeypadTarget.setText("");
                });
            } else if ("OK".equals(k)) {
                btn = createStyledButton("OK", ACCENT_GREEN);
                btn.addActionListener(e -> {
                    if (atmService.isLoggedIn()) {
                        showScreenCard("MENU");
                    } else {
                        handleLogin();
                    }
                });
            } else {
                btn = createStyledButton(k, TEXT_LIGHT);
                btn.setFont(new Font("Segoe UI", Font.BOLD, 18));
                btn.addActionListener(e -> {
                    if (activeKeypadTarget != null) {
                        activeKeypadTarget.setText(activeKeypadTarget.getText() + k);
                    }
                });
            }
            grid.add(btn);
        }

        container.add(grid, BorderLayout.CENTER);

        // Bottom Vault Cash Info
        JLabel vaultInfo = new JLabel("ATM Vault Cash: $50,000.00 Available", SwingConstants.CENTER);
        vaultInfo.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        vaultInfo.setForeground(ACCENT_GREEN);
        container.add(vaultInfo, BorderLayout.SOUTH);

        return container;
    }

    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        JLabel foot = new JLabel("Powered by Nexus Bank ATM Core v1.0 • 24/7 Customer Hotline: 1-800-555-NEXUS", SwingConstants.CENTER);
        foot.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        foot.setForeground(TEXT_MUTED);
        panel.add(foot, BorderLayout.CENTER);
        return panel;
    }

    private void handleLogin() {
        String acc = loginAccInput.getText().trim();
        String pin = new String(loginPinInput.getPassword()).trim();

        try {
            atmService.login(acc, pin);
            updateDashboardInfo();
            showScreenCard("MENU");
            updateLcdStatus("● SESSION ACTIVE — ACCOUNT #" + acc, ACCENT_GREEN);
        } catch (Exception ex) {
            updateLcdStatus("✖ AUTHENTICATION FAILED: " + ex.getMessage(), ACCENT_RED);
            showErrorDialog(ex.getMessage());
        }
    }

    private void processWithdrawal(double amount) {
        try {
            atmService.withdrawCash(amount);
            updateDashboardInfo();
            showReceiptDialog("WITHDRAWAL", amount, "ATM Cash Withdrawal", null);
            showScreenCard("MENU");
        } catch (Exception ex) {
            showErrorDialog(ex.getMessage());
        }
    }

    private void updateDashboardInfo() {
        if (!atmService.isLoggedIn()) return;
        Account acc = atmService.getActiveAccount();
        dashWelcomeLabel.setText("Welcome, " + acc.getHolderName() + " (" + acc.getAccountType() + ")");
        dashAccNumLabel.setText("Account Number: #" + acc.getAccountNumber());
        dashBalanceLabel.setText("Available Balance: " + formatCurrency(acc.getBalance()));
    }

    private void updateStatementTable() {
        statementTableModel.setRowCount(0);
        if (!atmService.isLoggedIn()) return;

        List<Transaction> txns = atmService.getMiniStatement();
        for (Transaction t : txns) {
            statementTableModel.addRow(new Object[]{
                    t.getFormattedTimestamp(),
                    t.getType(),
                    formatCurrency(t.getAmount()),
                    formatCurrency(t.getBalanceAfter())
            });
        }
    }

    private void showReceiptDialog(String type, double amount, String desc, String targetAcc) {
        Account acc = atmService.getActiveAccount();

        StringBuilder receipt = new StringBuilder();
        receipt.append("=========================================\n");
        receipt.append("         NEXUS GLOBAL BANK ATM           \n");
        receipt.append("            TRANSACTION RECEIPT          \n");
        receipt.append("=========================================\n");
        receipt.append(String.format("Terminal ID  : #ATM-8092\n"));
        receipt.append(String.format("Account #    : %s (%s)\n", acc.getAccountNumber(), acc.getHolderName()));
        receipt.append(String.format("Date / Time  : %s\n", java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
        receipt.append("-----------------------------------------\n");
        receipt.append(String.format("Operation    : %s\n", type));
        receipt.append(String.format("Amount       : %s\n", formatCurrency(amount)));
        if (targetAcc != null) {
            receipt.append(String.format("Target Acc # : %s\n", targetAcc));
        }
        receipt.append(String.format("New Balance  : %s\n", formatCurrency(acc.getBalance())));
        receipt.append("-----------------------------------------\n");
        receipt.append("      Thank you for banking with Nexus!   \n");
        receipt.append("=========================================\n");

        JTextArea area = new JTextArea(receipt.toString());
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));
        area.setBackground(new Color(250, 250, 210));
        area.setForeground(Color.BLACK);
        area.setEditable(false);
        area.setBorder(new EmptyBorder(10, 10, 10, 10));

        JOptionPane.showMessageDialog(this, new JScrollPane(area), "ATM Transaction Receipt", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showScreenCard(String cardName) {
        cardLayout.show(mainScreenPanel, cardName);
    }

    private void updateLcdStatus(String msg, Color color) {
        lcdStatusLabel.setText(msg);
        lcdStatusLabel.setForeground(color);
    }

    private void showErrorDialog(String msg) {
        JOptionPane.showMessageDialog(this, msg, "ATM Error", JOptionPane.ERROR_MESSAGE);
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(bg.darker(), 1, true),
                new EmptyBorder(8, 12, 8, 12)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void styleTextField(JTextField tf) {
        tf.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tf.setBackground(PANEL_BG);
        tf.setForeground(TEXT_LIGHT);
        tf.setCaretColor(ACCENT_CYAN);
        tf.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(ACCENT_CYAN, 1),
                new EmptyBorder(4, 8, 4, 8)
        ));
    }

    private String formatCurrency(double val) {
        return NumberFormat.getCurrencyInstance(Locale.US).format(val);
    }
}
