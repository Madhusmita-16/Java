package com.atm.ui;

import com.atm.model.Account;
import com.atm.model.Transaction;
import com.atm.service.ATMService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ATMFrame extends JFrame {

    private final ATMService atmService;
    private final CardLayout cardLayout;
    private final JPanel mainScreenPanel;

    // Screen LCD components
    private JLabel lcdStatusLabel;

    // Login Screen inputs
    private JTextField loginAccInput;
    private JPasswordField loginPinInput;

    // Dashboard Info
    private JLabel dashWelcomeLabel;
    private JLabel dashAccNumLabel;
    private JLabel dashBalanceLabel;

    // Keypad Active Field reference
    private JTextField activeKeypadTarget;

    // High Contrast Light Theme Palette
    private static final Color BG_OUTER = new Color(226, 232, 240);       // Crisp Light Slate
    private static final Color PANEL_BG = new Color(255, 255, 255);       // Pure White
    private static final Color LCD_BG = new Color(248, 250, 252);         // Off-White LCD
    private static final Color BORDER_DARK = new Color(71, 85, 105);       // Dark Slate Border
    private static final Color TEXT_DARK = new Color(15, 23, 42);          // Deep Charcoal Text
    private static final Color TEXT_MUTED = new Color(71, 85, 105);        // Slate Muted Text

    // Vibrant Colorful Button Palette
    private static final Color BTN_PRIMARY = new Color(37, 99, 235);       // Royal Sapphire Blue
    private static final Color BTN_SUCCESS = new Color(5, 150, 105);       // Emerald Green
    private static final Color BTN_WARNING = new Color(234, 88, 12);       // Sunset Orange
    private static final Color BTN_DANGER = new Color(220, 38, 38);        // Crimson Red
    private static final Color BTN_PURPLE = new Color(147, 51, 234);       // Vivid Violet
    private static final Color BTN_INDIGO = new Color(79, 70, 229);        // Electric Indigo

    public ATMFrame(ATMService atmService) {
        this.atmService = atmService;
        this.cardLayout = new CardLayout();
        this.mainScreenPanel = new JPanel(cardLayout);

        setTitle("NEXUS BANK - NextGen ATM Terminal");
        setSize(960, 740);
        setMinimumSize(new Dimension(860, 660));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_OUTER);

        initUI();
    }

    private void initUI() {
        JPanel rootPanel = new JPanel(new BorderLayout(15, 15));
        rootPanel.setBackground(BG_OUTER);
        rootPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // --- Top Header ---
        JPanel headerPanel = createHeaderPanel();
        rootPanel.add(headerPanel, BorderLayout.NORTH);

        // --- Center Screen & Keypad Split ---
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        centerPanel.setOpaque(false);

        // Left Column: High Contrast LCD Screen Panel
        JPanel lcdScreenContainer = createLCDScreenPanel();
        centerPanel.add(lcdScreenContainer);

        // Right Column: Tactile Hardware Keypad Panel
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
                new LineBorder(BORDER_DARK, 2, true),
                new EmptyBorder(12, 20, 12, 20)
        ));

        JLabel titleLabel = new JLabel("NEXUS GLOBAL BANK ATM");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(BTN_PRIMARY);

        JLabel subTitleLabel = new JLabel("Terminal ID: #ATM-8092 | Secure 256-bit Encrypted Session");
        subTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        subTitleLabel.setForeground(TEXT_MUTED);

        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(subTitleLabel, BorderLayout.EAST);
        return panel;
    }

    private JPanel createLCDScreenPanel() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(LCD_BG);
        container.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BTN_PRIMARY, 3, true),
                new EmptyBorder(15, 15, 15, 15)
        ));

        // LCD Status Header Bar
        JPanel statusHeaderBar = new JPanel(new BorderLayout());
        statusHeaderBar.setBackground(TEXT_DARK);
        statusHeaderBar.setBorder(new EmptyBorder(6, 10, 6, 10));

        lcdStatusLabel = new JLabel("SYSTEM READY - INSERT ACCOUNT NUMBER & PIN", SwingConstants.CENTER);
        lcdStatusLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lcdStatusLabel.setForeground(Color.WHITE);
        statusHeaderBar.add(lcdStatusLabel, BorderLayout.CENTER);

        container.add(statusHeaderBar, BorderLayout.NORTH);

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
        welcomeTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        welcomeTitle.setForeground(TEXT_DARK);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(welcomeTitle, gbc);

        JLabel subHint = new JLabel("Demo Accounts: 1001 (PIN: 1234) | 1002 (PIN: 5678)", SwingConstants.CENTER);
        subHint.setFont(new Font("Segoe UI", Font.BOLD, 12));
        subHint.setForeground(BTN_WARNING);
        gbc.gridy = 1;
        panel.add(subHint, gbc);

        gbc.gridwidth = 1;

        // Account Number Input
        JLabel accLabel = new JLabel("Account Number:");
        accLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        accLabel.setForeground(TEXT_DARK);
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
        pinLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        pinLabel.setForeground(TEXT_DARK);
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
        JButton loginBtn = createStyledButton("AUTHENTICATE AND ENTER", BTN_PRIMARY);
        loginBtn.addActionListener(e -> handleLogin());
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; gbc.insets = new Insets(15, 8, 8, 8);
        panel.add(loginBtn, gbc);

        activeKeypadTarget = loginAccInput;
        return panel;
    }

    private JPanel createMenuCard() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);

        // User Info Header Card
        JPanel infoPanel = new JPanel(new GridLayout(3, 1, 4, 4));
        infoPanel.setBackground(PANEL_BG);
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_DARK, 2, true),
                new EmptyBorder(10, 15, 10, 15)
        ));

        dashWelcomeLabel = new JLabel("Welcome, Valued Customer");
        dashWelcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        dashWelcomeLabel.setForeground(TEXT_DARK);

        dashAccNumLabel = new JLabel("Account: #XXXX");
        dashAccNumLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        dashAccNumLabel.setForeground(TEXT_MUTED);

        dashBalanceLabel = new JLabel("Available Balance: ₹0.00");
        dashBalanceLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        dashBalanceLabel.setForeground(BTN_SUCCESS);

        infoPanel.add(dashWelcomeLabel);
        infoPanel.add(dashAccNumLabel);
        infoPanel.add(dashBalanceLabel);

        panel.add(infoPanel, BorderLayout.NORTH);

        // Actions Grid with Vibrant Colour Buttons
        JPanel grid = new JPanel(new GridLayout(3, 2, 10, 10));
        grid.setOpaque(false);

        JButton btnWithdraw = createStyledButton("WITHDRAW CASH", BTN_PRIMARY);
        btnWithdraw.addActionListener(e -> showScreenCard("WITHDRAW"));

        JButton btnDeposit = createStyledButton("DEPOSIT CASH", BTN_SUCCESS);
        btnDeposit.addActionListener(e -> showScreenCard("DEPOSIT"));

        JButton btnTransfer = createStyledButton("FUND TRANSFER", BTN_WARNING);
        btnTransfer.addActionListener(e -> showScreenCard("TRANSFER"));

        JButton btnStatement = createStyledButton("MINI STATEMENT", BTN_INDIGO);
        btnStatement.addActionListener(e -> {
            updateStatementTable();
            showScreenCard("STATEMENT");
        });

        JButton btnBalance = createStyledButton("CHECK BALANCE", BTN_PURPLE);
        btnBalance.addActionListener(e -> {
            double bal = atmService.checkBalance();
            updateDashboardInfo();
            JOptionPane.showMessageDialog(this,
                    "Current Available Balance: " + formatRupees(bal),
                    "Balance Inquiry", JOptionPane.INFORMATION_MESSAGE);
        });

        JButton btnLogout = createStyledButton("EXIT / LOGOUT", BTN_DANGER);
        btnLogout.addActionListener(e -> {
            atmService.logout();
            showScreenCard("LOGIN");
            updateLcdStatus("SYSTEM READY - INSERT ACCOUNT NUMBER & PIN", Color.WHITE);
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
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(TEXT_DARK);
        panel.add(title, BorderLayout.NORTH);

        // Quick Cash Amounts (in Rupees) with distinct vivid color theme per tier
        JPanel quickGrid = new JPanel(new GridLayout(3, 2, 8, 8));
        quickGrid.setOpaque(false);

        int[] quickAmounts = {500, 1000, 2000, 5000, 10000};
        Color[] quickColors = {
                new Color(13, 148, 136),  // Teal
                new Color(37, 99, 235),   // Royal Blue
                new Color(79, 70, 229),   // Indigo
                new Color(147, 51, 234),  // Violet
                new Color(225, 29, 72)    // Rose
        };

        for (int i = 0; i < quickAmounts.length; i++) {
            int amt = quickAmounts[i];
            Color c = quickColors[i];
            JButton qBtn = createStyledButton("₹" + String.format("%,d", amt), c);
            qBtn.addActionListener(e -> processWithdrawal(amt));
            quickGrid.add(qBtn);
        }

        panel.add(quickGrid, BorderLayout.CENTER);

        // Custom Amount Row
        JPanel customRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        customRow.setOpaque(false);

        JLabel customLabel = new JLabel("Custom Amount (₹):");
        customLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        customLabel.setForeground(TEXT_DARK);

        JTextField customInput = new JTextField(8);
        styleTextField(customInput);
        customInput.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) { activeKeypadTarget = customInput; }
        });

        JButton customBtn = createStyledButton("WITHDRAW", BTN_SUCCESS);
        customBtn.addActionListener(e -> {
            try {
                double val = Double.parseDouble(customInput.getText().trim());
                processWithdrawal(val);
                customInput.setText("");
            } catch (NumberFormatException ex) {
                showErrorDialog("Please enter a valid numeric withdrawal amount.");
            }
        });

        JButton backBtn = createStyledButton("BACK", BTN_DANGER);
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
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(BTN_SUCCESS);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(title, gbc);

        JLabel amtLabel = new JLabel("Deposit Amount (₹):");
        amtLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        amtLabel.setForeground(TEXT_DARK);
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        panel.add(amtLabel, gbc);

        JTextField depInput = new JTextField(10);
        styleTextField(depInput);
        depInput.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) { activeKeypadTarget = depInput; }
        });
        gbc.gridx = 1;
        panel.add(depInput, gbc);

        JButton depBtn = createStyledButton("DEPOSIT CASH ENVELOPE", BTN_SUCCESS);
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

        JButton backBtn = createStyledButton("CANCEL", BTN_DANGER);
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
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(BTN_WARNING);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(title, gbc);

        JLabel targetLabel = new JLabel("Recipient Account #:");
        targetLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        targetLabel.setForeground(TEXT_DARK);
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        panel.add(targetLabel, gbc);

        JTextField targetInput = new JTextField(12);
        styleTextField(targetInput);
        targetInput.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) { activeKeypadTarget = targetInput; }
        });
        gbc.gridx = 1;
        panel.add(targetInput, gbc);

        JLabel amtLabel = new JLabel("Transfer Amount (₹):");
        amtLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        amtLabel.setForeground(TEXT_DARK);
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(amtLabel, gbc);

        JTextField transferAmtInput = new JTextField(12);
        styleTextField(transferAmtInput);
        transferAmtInput.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) { activeKeypadTarget = transferAmtInput; }
        });
        gbc.gridx = 1;
        panel.add(transferAmtInput, gbc);

        JButton sendBtn = createStyledButton("SEND TRANSFER NOW", BTN_WARNING);
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

        JButton backBtn = createStyledButton("CANCEL", BTN_DANGER);
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
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(TEXT_DARK);
        panel.add(title, BorderLayout.NORTH);

        String[] cols = {"Date / Time", "Type", "Amount (₹)", "Balance (₹)"};
        statementTableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };

        JTable table = new JTable(statementTableModel);
        table.setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.setBackground(Color.WHITE);
        table.setForeground(TEXT_DARK);
        table.setGridColor(new Color(203, 213, 225));
        table.getTableHeader().setBackground(TEXT_DARK);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.setRowHeight(24);

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(Color.WHITE);
        panel.add(scroll, BorderLayout.CENTER);

        JButton backBtn = createStyledButton("RETURN TO MAIN MENU", BTN_PRIMARY);
        backBtn.addActionListener(e -> showScreenCard("MENU"));
        panel.add(backBtn, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createKeypadPanel() {
        JPanel container = new JPanel(new BorderLayout(10, 10));
        container.setBackground(PANEL_BG);
        container.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_DARK, 2, true),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel keypadHeader = new JLabel("TOUCH KEYPAD", SwingConstants.CENTER);
        keypadHeader.setFont(new Font("Segoe UI", Font.BOLD, 15));
        keypadHeader.setForeground(TEXT_DARK);
        container.add(keypadHeader, BorderLayout.NORTH);

        // 3x4 Grid Keypad with Vibrant Colorful Buttons
        JPanel grid = new JPanel(new GridLayout(4, 3, 8, 8));
        grid.setOpaque(false);

        String[] keys = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "CLEAR", "0", "OK"};
        for (String k : keys) {
            JButton btn;
            if ("CLEAR".equals(k)) {
                btn = createStyledButton("CLEAR", BTN_WARNING, Color.WHITE);
                btn.addActionListener(e -> {
                    if (activeKeypadTarget != null) activeKeypadTarget.setText("");
                });
            } else if ("OK".equals(k)) {
                btn = createStyledButton("OK", BTN_SUCCESS, Color.WHITE);
                btn.addActionListener(e -> {
                    if (atmService.isLoggedIn()) {
                        showScreenCard("MENU");
                    } else {
                        handleLogin();
                    }
                });
            } else {
                Color keyBg = new Color(224, 242, 254); // Soft Vibrant Azure
                Color keyFg = new Color(3, 105, 161);    // Deep Navy
                btn = createStyledButton(k, keyBg, keyFg);
                btn.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(new Color(2, 132, 199), 2, true),
                        new EmptyBorder(8, 12, 8, 12)
                ));
                btn.setFont(new Font("Segoe UI", Font.BOLD, 22));
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
        JLabel vaultInfo = new JLabel("ATM Vault Cash: ₹5,00,000.00 Available", SwingConstants.CENTER);
        vaultInfo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        vaultInfo.setForeground(BTN_SUCCESS);
        container.add(vaultInfo, BorderLayout.SOUTH);

        return container;
    }

    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        JLabel foot = new JLabel("Powered by Nexus Bank ATM Core v1.0 | 24/7 Customer Hotline: 1800-555-NEXUS", SwingConstants.CENTER);
        foot.setFont(new Font("Segoe UI", Font.BOLD, 12));
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
            updateLcdStatus("SESSION ACTIVE - ACCOUNT #" + acc, Color.WHITE);
        } catch (Exception ex) {
            updateLcdStatus("AUTHENTICATION FAILED: " + ex.getMessage(), Color.WHITE);
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
        dashBalanceLabel.setText("Available Balance: " + formatRupees(acc.getBalance()));
    }

    private void updateStatementTable() {
        statementTableModel.setRowCount(0);
        if (!atmService.isLoggedIn()) return;

        List<Transaction> txns = atmService.getMiniStatement();
        for (Transaction t : txns) {
            statementTableModel.addRow(new Object[]{
                    t.getFormattedTimestamp(),
                    t.getType(),
                    formatRupees(t.getAmount()),
                    formatRupees(t.getBalanceAfter())
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
        receipt.append(String.format("Amount       : %s\n", formatRupees(amount)));
        if (targetAcc != null) {
            receipt.append(String.format("Target Acc # : %s\n", targetAcc));
        }
        receipt.append(String.format("New Balance  : %s\n", formatRupees(acc.getBalance())));
        receipt.append("-----------------------------------------\n");
        receipt.append("      Thank you for banking with Nexus!   \n");
        receipt.append("=========================================\n");

        JTextArea area = new JTextArea(receipt.toString());
        area.setFont(new Font("Monospaced", Font.BOLD, 13));
        area.setBackground(new Color(254, 252, 232));
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

    private JButton createStyledButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(true);
        btn.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(bg.darker(), 2, true),
                new EmptyBorder(10, 14, 10, 14)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Interactive Hover Effect
        Color hoverBg = bg.equals(PANEL_BG) || bg.equals(new Color(224, 242, 254)) ? new Color(186, 230, 253) : bg.brighter();
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(hoverBg);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(bg);
            }
        });

        return btn;
    }

    private JButton createStyledButton(String text, Color bg) {
        return createStyledButton(text, bg, Color.WHITE);
    }

    private void styleTextField(JTextField tf) {
        tf.setFont(new Font("Segoe UI", Font.BOLD, 15));
        tf.setBackground(Color.WHITE);
        tf.setForeground(TEXT_DARK);
        tf.setCaretColor(BTN_PRIMARY);
        tf.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_DARK, 2),
                new EmptyBorder(6, 10, 6, 10)
        ));
    }

    private String formatRupees(double val) {
        return "₹" + String.format(Locale.US, "%,.2f", val);
    }
}
