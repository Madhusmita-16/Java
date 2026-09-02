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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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

    // High-Tech Modern Dark ATM Palette
    private static final Color CONSOLE_BG = new Color(15, 23, 42);          // Deep Slate Metallic
    private static final Color FRAME_PANEL = new Color(30, 41, 59);          // Dark Titanium Panel
    private static final Color LCD_OLED_BG = new Color(3, 7, 18);           // Midnight OLED Screen
    private static final Color CARD_BG = new Color(17, 24, 39);             // Dark Glossy Card
    private static final Color BORDER_GLOW = new Color(2, 132, 199);        // Cyan Glow Border

    private static final Color TEXT_BRIGHT = new Color(248, 250, 252);       // Bright White
    private static final Color TEXT_MUTED = new Color(148, 163, 184);        // Cool Gray Text
    private static final Color TEXT_CYAN = new Color(56, 189, 248);          // Glowing Cyan Text
    private static final Color TEXT_GREEN = new Color(74, 222, 128);         // Glowing Emerald Text
    private static final Color TEXT_GOLD = new Color(250, 204, 21);          // Vibrant Gold Text

    // 3D Button Gradient Base Colors
    private static final Color BTN_PRIMARY = new Color(37, 99, 235);        // Sapphire Blue
    private static final Color BTN_SUCCESS = new Color(5, 150, 105);        // Emerald Green
    private static final Color BTN_WARNING = new Color(234, 88, 12);        // Tangerine Orange
    private static final Color BTN_DANGER = new Color(220, 38, 38);         // Crimson Red
    private static final Color BTN_PURPLE = new Color(147, 51, 234);        // Vivid Purple
    private static final Color BTN_INDIGO = new Color(79, 70, 229);         // Electric Indigo
    private static final Color BTN_STEEL = new Color(51, 65, 85);           // Metallic Steel Keycap

    public ATMFrame(ATMService atmService) {
        this.atmService = atmService;
        this.cardLayout = new CardLayout();
        this.mainScreenPanel = new JPanel(cardLayout);

        setTitle("NEXUS BANK - NextGen ATM Kiosk Terminal");
        setSize(1000, 760);
        setMinimumSize(new Dimension(900, 700));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(CONSOLE_BG);

        initUI();
    }

    private void initUI() {
        JPanel rootPanel = new JPanel(new BorderLayout(15, 15)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, CONSOLE_BG, 0, getHeight(), new Color(30, 41, 59));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        rootPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Top Kiosk Metallic Header
        JPanel headerPanel = createHeaderPanel();
        rootPanel.add(headerPanel, BorderLayout.NORTH);

        // Center Split: LCD Digital Display Screen & Hardware Keypad Kiosk
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        centerPanel.setOpaque(false);

        // Left: OLED Digital Screen Display Frame
        JPanel lcdScreenContainer = createLCDScreenPanel();
        centerPanel.add(lcdScreenContainer);

        // Right: Tactile Hardware Keypad & Cash Slot Frame
        JPanel keypadContainer = createKeypadPanel();
        centerPanel.add(keypadContainer);

        rootPanel.add(centerPanel, BorderLayout.CENTER);

        // Bottom Kiosk Terminal Footer
        JPanel footerPanel = createFooterPanel();
        rootPanel.add(footerPanel, BorderLayout.SOUTH);

        add(rootPanel);

        showScreenCard("LOGIN");
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(FRAME_PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_GLOW, 2, true),
                new EmptyBorder(12, 20, 12, 20)
        ));

        JLabel titleLabel = new JLabel("NEXUS GLOBAL BANK ATM");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(TEXT_CYAN);

        JLabel subTitleLabel = new JLabel("Terminal #ATM-8092  |  SYSTEM ONLINE  |  256-Bit Encrypted Session");
        subTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        subTitleLabel.setForeground(TEXT_GREEN);

        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(subTitleLabel, BorderLayout.EAST);
        return panel;
    }

    private JPanel createLCDScreenPanel() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(LCD_OLED_BG);
        container.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_GLOW, 3, true),
                new EmptyBorder(15, 15, 15, 15)
        ));

        // LCD Header Bar
        JPanel statusHeaderBar = new JPanel(new BorderLayout());
        statusHeaderBar.setBackground(new Color(30, 27, 75));
        statusHeaderBar.setBorder(new EmptyBorder(8, 12, 8, 12));

        lcdStatusLabel = new JLabel("SYSTEM READY - INSERT ACCOUNT NUMBER & PIN", SwingConstants.CENTER);
        lcdStatusLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lcdStatusLabel.setForeground(TEXT_CYAN);
        statusHeaderBar.add(lcdStatusLabel, BorderLayout.CENTER);

        container.add(statusHeaderBar, BorderLayout.NORTH);

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
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel welcomeTitle = new JLabel("Welcome to Nexus ATM", SwingConstants.CENTER);
        welcomeTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        welcomeTitle.setForeground(TEXT_BRIGHT);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(welcomeTitle, gbc);

        JLabel subHint = new JLabel("Demo Accounts: 1001 (PIN: 1234) | 1002 (PIN: 5678)", SwingConstants.CENTER);
        subHint.setFont(new Font("Segoe UI", Font.BOLD, 12));
        subHint.setForeground(TEXT_GOLD);
        gbc.gridy = 1;
        panel.add(subHint, gbc);

        gbc.gridwidth = 1;

        JLabel accLabel = new JLabel("Account Number:");
        accLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        accLabel.setForeground(TEXT_MUTED);
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

        JLabel pinLabel = new JLabel("4-Digit PIN:");
        pinLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        pinLabel.setForeground(TEXT_MUTED);
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

        AtmButton loginBtn = new AtmButton("AUTHENTICATE & ENTER", BTN_PRIMARY, Color.WHITE, 14);
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginBtn.setPreferredSize(new Dimension(200, 45));
        loginBtn.addActionListener(e -> handleLogin());

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; gbc.insets = new Insets(18, 10, 10, 10);
        panel.add(loginBtn, gbc);

        activeKeypadTarget = loginAccInput;
        return panel;
    }

    private JPanel createMenuCard() {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setOpaque(false);

        // Account Header Card
        JPanel infoPanel = new JPanel(new GridLayout(3, 1, 4, 4));
        infoPanel.setBackground(CARD_BG);
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_GLOW, 1, true),
                new EmptyBorder(12, 16, 12, 16)
        ));

        dashWelcomeLabel = new JLabel("Welcome, Valued Customer");
        dashWelcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        dashWelcomeLabel.setForeground(TEXT_BRIGHT);

        dashAccNumLabel = new JLabel("Account Number: #XXXX");
        dashAccNumLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        dashAccNumLabel.setForeground(TEXT_MUTED);

        dashBalanceLabel = new JLabel("Available Balance: ₹0.00");
        dashBalanceLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        dashBalanceLabel.setForeground(TEXT_GREEN);

        infoPanel.add(dashWelcomeLabel);
        infoPanel.add(dashAccNumLabel);
        infoPanel.add(dashBalanceLabel);

        panel.add(infoPanel, BorderLayout.NORTH);

        // 6 Action Grid Buttons
        JPanel grid = new JPanel(new GridLayout(3, 2, 12, 12));
        grid.setOpaque(false);

        AtmButton btnWithdraw = new AtmButton("WITHDRAW CASH", BTN_PRIMARY);
        btnWithdraw.addActionListener(e -> showScreenCard("WITHDRAW"));

        AtmButton btnDeposit = new AtmButton("DEPOSIT CASH", BTN_SUCCESS);
        btnDeposit.addActionListener(e -> showScreenCard("DEPOSIT"));

        AtmButton btnTransfer = new AtmButton("FUND TRANSFER", BTN_WARNING);
        btnTransfer.addActionListener(e -> showScreenCard("TRANSFER"));

        AtmButton btnStatement = new AtmButton("MINI STATEMENT", BTN_INDIGO);
        btnStatement.addActionListener(e -> {
            updateStatementTable();
            showScreenCard("STATEMENT");
        });

        AtmButton btnBalance = new AtmButton("CHECK BALANCE", BTN_PURPLE);
        btnBalance.addActionListener(e -> {
            double bal = atmService.checkBalance();
            updateDashboardInfo();
            JOptionPane.showMessageDialog(this,
                    "Current Available Balance: " + formatRupees(bal),
                    "Balance Inquiry", JOptionPane.INFORMATION_MESSAGE);
        });

        AtmButton btnLogout = new AtmButton("EXIT / LOGOUT", BTN_DANGER);
        btnLogout.addActionListener(e -> {
            atmService.logout();
            showScreenCard("LOGIN");
            updateLcdStatus("SYSTEM READY - INSERT ACCOUNT NUMBER & PIN", TEXT_CYAN);
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
        title.setForeground(TEXT_BRIGHT);
        panel.add(title, BorderLayout.NORTH);

        JPanel quickGrid = new JPanel(new GridLayout(3, 2, 8, 8));
        quickGrid.setOpaque(false);

        int[] quickAmounts = {500, 1000, 2000, 5000, 10000};
        Color[] quickColors = {
                new Color(13, 148, 136),  // Teal
                new Color(37, 99, 235),   // Blue
                new Color(79, 70, 229),   // Indigo
                new Color(147, 51, 234),  // Violet
                new Color(225, 29, 72)    // Rose
        };

        for (int i = 0; i < quickAmounts.length; i++) {
            int amt = quickAmounts[i];
            Color c = quickColors[i];
            AtmButton qBtn = new AtmButton("₹" + String.format("%,d", amt), c);
            qBtn.addActionListener(e -> processWithdrawal(amt));
            quickGrid.add(qBtn);
        }

        panel.add(quickGrid, BorderLayout.CENTER);

        JPanel customRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        customRow.setOpaque(false);

        JLabel customLabel = new JLabel("Custom Amount (₹):");
        customLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        customLabel.setForeground(TEXT_MUTED);

        JTextField customInput = new JTextField(8);
        styleTextField(customInput);
        customInput.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) { activeKeypadTarget = customInput; }
        });

        AtmButton customBtn = new AtmButton("WITHDRAW", BTN_SUCCESS);
        customBtn.addActionListener(e -> {
            try {
                double val = Double.parseDouble(customInput.getText().trim());
                processWithdrawal(val);
                customInput.setText("");
            } catch (NumberFormatException ex) {
                showErrorDialog("Please enter a valid numeric withdrawal amount.");
            }
        });

        AtmButton backBtn = new AtmButton("BACK", BTN_DANGER);
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
        title.setForeground(TEXT_GREEN);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(title, gbc);

        JLabel amtLabel = new JLabel("Deposit Amount (₹):");
        amtLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        amtLabel.setForeground(TEXT_MUTED);
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        panel.add(amtLabel, gbc);

        JTextField depInput = new JTextField(10);
        styleTextField(depInput);
        depInput.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) { activeKeypadTarget = depInput; }
        });
        gbc.gridx = 1;
        panel.add(depInput, gbc);

        AtmButton depBtn = new AtmButton("DEPOSIT CASH ENVELOPE", BTN_SUCCESS);
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

        AtmButton backBtn = new AtmButton("CANCEL", BTN_DANGER);
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
        title.setForeground(TEXT_GOLD);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(title, gbc);

        JLabel targetLabel = new JLabel("Recipient Account #:");
        targetLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        targetLabel.setForeground(TEXT_MUTED);
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
        amtLabel.setForeground(TEXT_MUTED);
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(amtLabel, gbc);

        JTextField transferAmtInput = new JTextField(12);
        styleTextField(transferAmtInput);
        transferAmtInput.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) { activeKeypadTarget = transferAmtInput; }
        });
        gbc.gridx = 1;
        panel.add(transferAmtInput, gbc);

        AtmButton sendBtn = new AtmButton("SEND TRANSFER NOW", BTN_WARNING);
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

        AtmButton backBtn = new AtmButton("CANCEL", BTN_DANGER);
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
        title.setForeground(TEXT_BRIGHT);
        panel.add(title, BorderLayout.NORTH);

        String[] cols = {"Date / Time", "Type", "Amount (₹)", "Balance (₹)"};
        statementTableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };

        JTable table = new JTable(statementTableModel);
        table.setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.setBackground(CARD_BG);
        table.setForeground(TEXT_BRIGHT);
        table.setGridColor(new Color(51, 65, 85));
        table.getTableHeader().setBackground(new Color(30, 27, 75));
        table.getTableHeader().setForeground(TEXT_CYAN);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.setRowHeight(26);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.setDefaultRenderer(Object.class, centerRenderer);

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(CARD_BG);
        panel.add(scroll, BorderLayout.CENTER);

        AtmButton backBtn = new AtmButton("RETURN TO MAIN MENU", BTN_PRIMARY);
        backBtn.addActionListener(e -> showScreenCard("MENU"));
        panel.add(backBtn, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createKeypadPanel() {
        JPanel container = new JPanel(new BorderLayout(12, 12));
        container.setBackground(FRAME_PANEL);
        container.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_GLOW, 2, true),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel keypadHeader = new JLabel("TOUCH HARDWARE KEYPAD", SwingConstants.CENTER);
        keypadHeader.setFont(new Font("Segoe UI", Font.BOLD, 14));
        keypadHeader.setForeground(TEXT_CYAN);
        container.add(keypadHeader, BorderLayout.NORTH);

        // 3x4 Metallic Hardware Keypad Grid
        JPanel grid = new JPanel(new GridLayout(4, 3, 10, 10));
        grid.setOpaque(false);

        String[] keys = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "CLEAR", "0", "OK"};
        for (String k : keys) {
            AtmButton btn;
            if ("CLEAR".equals(k)) {
                btn = new AtmButton("CLEAR", BTN_WARNING, Color.WHITE, 12);
                btn.addActionListener(e -> {
                    if (activeKeypadTarget != null) activeKeypadTarget.setText("");
                });
            } else if ("OK".equals(k)) {
                btn = new AtmButton("OK", BTN_SUCCESS, Color.WHITE, 12);
                btn.addActionListener(e -> {
                    if (atmService.isLoggedIn()) {
                        showScreenCard("MENU");
                    } else {
                        handleLogin();
                    }
                });
            } else {
                btn = new AtmButton(k, BTN_STEEL, TEXT_BRIGHT, 12);
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

        // Bottom Hardware Slots Simulation Panel
        JPanel hardwareTray = new JPanel(new GridLayout(2, 1, 6, 6));
        hardwareTray.setOpaque(false);

        JPanel cashTraySlot = new JPanel(new BorderLayout());
        cashTraySlot.setBackground(new Color(15, 23, 42));
        cashTraySlot.setBorder(new LineBorder(new Color(51, 65, 85), 2, true));
        JLabel vaultCashLabel = new JLabel("CASH DISPENSER TRAY - VAULT: ₹5,00,000 AVAILABLE", SwingConstants.CENTER);
        vaultCashLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        vaultCashLabel.setForeground(TEXT_GREEN);
        cashTraySlot.add(vaultCashLabel, BorderLayout.CENTER);

        JPanel cardSlot = new JPanel(new BorderLayout());
        cardSlot.setBackground(new Color(15, 23, 42));
        cardSlot.setBorder(new LineBorder(new Color(51, 65, 85), 2, true));
        JLabel cardSlotLabel = new JLabel("CHIP / CONTACTLESS CARD READER SLOT", SwingConstants.CENTER);
        cardSlotLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        cardSlotLabel.setForeground(TEXT_MUTED);
        cardSlot.add(cardSlotLabel, BorderLayout.CENTER);

        hardwareTray.add(cashTraySlot);
        hardwareTray.add(cardSlot);

        container.add(hardwareTray, BorderLayout.SOUTH);
        return container;
    }

    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        JLabel foot = new JLabel("Powered by Nexus Bank ATM Core v1.0 | 24/7 Customer Support: 1800-555-NEXUS", SwingConstants.CENTER);
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
            updateLcdStatus("SESSION ACTIVE - ACCOUNT #" + acc, TEXT_GREEN);
        } catch (Exception ex) {
            updateLcdStatus("AUTHENTICATION FAILED: " + ex.getMessage(), new Color(248, 113, 113));
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

    private void styleTextField(JTextField tf) {
        tf.setFont(new Font("Segoe UI", Font.BOLD, 16));
        tf.setBackground(CARD_BG);
        tf.setForeground(TEXT_BRIGHT);
        tf.setCaretColor(TEXT_CYAN);
        tf.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_GLOW, 2, true),
                new EmptyBorder(8, 12, 8, 12)
        ));
    }

    private String formatRupees(double val) {
        return "₹" + String.format(Locale.US, "%,.2f", val);
    }

    // Custom 3D Anti-Aliased ATM Button Component
    private static class AtmButton extends JButton {
        private final Color topColor;
        private final Color bottomColor;
        private final Color hoverTop;
        private final Color hoverBottom;
        private final int cornerRadius;
        private boolean isHovered = false;
        private boolean isPressed = false;

        public AtmButton(String text, Color baseColor, Color textColor, int cornerRadius) {
            super(text);
            this.cornerRadius = cornerRadius;

            this.topColor = baseColor.brighter();
            this.bottomColor = baseColor.darker();
            this.hoverTop = baseColor.brighter().brighter();
            this.hoverBottom = baseColor;

            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setForeground(textColor);
            setFont(new Font("Segoe UI", Font.BOLD, 13));
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { isHovered = true; repaint(); }
                public void mouseExited(MouseEvent e) { isHovered = false; isPressed = false; repaint(); }
                public void mousePressed(MouseEvent e) { isPressed = true; repaint(); }
                public void mouseReleased(MouseEvent e) { isPressed = false; repaint(); }
            });
        }

        public AtmButton(String text, Color baseColor) {
            this(text, baseColor, Color.WHITE, 12);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            // Draw Drop Shadow
            if (!isPressed) {
                g2.setColor(new Color(0, 0, 0, 80));
                g2.fillRoundRect(2, 4, w - 4, h - 4, cornerRadius, cornerRadius);
            }

            // Select Gradient
            Color t = isPressed ? bottomColor : (isHovered ? hoverTop : topColor);
            Color b = isPressed ? topColor : (isHovered ? hoverBottom : bottomColor);

            int offsetY = isPressed ? 2 : 0;
            GradientPaint gp = new GradientPaint(0, offsetY, t, 0, h - 2 + offsetY, b);
            g2.setPaint(gp);
            g2.fillRoundRect(0, offsetY, w - 2, h - 2 - offsetY, cornerRadius, cornerRadius);

            // Inner Highlight Bezel
            g2.setColor(new Color(255, 255, 255, isHovered ? 120 : 60));
            g2.drawRoundRect(1, 1 + offsetY, w - 4, h - 4 - offsetY, cornerRadius - 2, cornerRadius - 2);

            // Outer Border Ring
            g2.setColor(isHovered ? t.brighter() : new Color(0, 0, 0, 100));
            g2.drawRoundRect(0, offsetY, w - 2, h - 2 - offsetY, cornerRadius, cornerRadius);

            g2.dispose();
            super.paintComponent(g);
        }
    }
}
