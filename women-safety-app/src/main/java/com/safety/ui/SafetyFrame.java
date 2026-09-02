package com.safety.ui;

import com.safety.model.Contact;
import com.safety.model.IncidentRecord;
import com.safety.model.SafeZone;
import com.safety.service.SafetyService;
import com.safety.ui.components.SafetyButton;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

public class SafetyFrame extends JFrame {

    private final SafetyService safetyService;
    private final CardLayout cardLayout;
    private final JPanel mainContentPanel;

    // Header Components
    private JLabel statusHeaderLabel;

    // SOS Emergency Card
    private JLabel sosStatusLabel;
    private JLabel countdownLabel;
    private Timer sosCountdownTimer;
    private int countdownSeconds = 3;

    // GPS Location Card
    private JLabel gpsCoordsLabel;
    private JLabel gpsSpeedLabel;
    private JLabel safeZoneNameLabel;
    private JLabel safeZoneLevelLabel;
    private JLabel safeZoneDescLabel;

    // Table Models
    private DefaultTableModel contactsTableModel;
    private DefaultTableModel incidentsTableModel;

    // Strobe Light State
    private Timer strobeTimer;
    private boolean strobeToggle = false;

    // Modern Dark Glassmorphic Theme Palette
    private static final Color BG_DARK = new Color(15, 23, 42);             // Deep Slate Midnight
    private static final Color PANEL_DARK = new Color(30, 41, 59);          // Dark Titanium Container
    private static final Color CARD_BG = new Color(17, 24, 39);             // Dark Glossy Card
    private static final Color BORDER_GLOW = new Color(2, 132, 199);        // Cyan Glow Border

    private static final Color TEXT_BRIGHT = new Color(248, 250, 252);       // Bright White
    private static final Color TEXT_MUTED = new Color(148, 163, 184);        // Slate Muted Text
    private static final Color TEXT_CYAN = new Color(56, 189, 248);          // Glowing Cyan
    private static final Color TEXT_GREEN = new Color(74, 222, 128);         // Glowing Emerald
    private static final Color TEXT_RED = new Color(248, 113, 113);          // Crimson Red Text
    private static final Color TEXT_GOLD = new Color(250, 204, 21);          // Vibrant Gold

    // Vibrant 3D Button Base Colors
    private static final Color BTN_SOS_RED = new Color(220, 38, 38);        // Vivid Red SOS
    private static final Color BTN_DANGER = new Color(220, 38, 38);         // Crimson Red Danger
    private static final Color BTN_PRIMARY = new Color(37, 99, 235);        // Royal Blue
    private static final Color BTN_SUCCESS = new Color(5, 150, 105);        // Emerald Green
    private static final Color BTN_WARNING = new Color(234, 88, 12);        // Tangerine Orange
    private static final Color BTN_PURPLE = new Color(147, 51, 234);        // Vivid Purple
    private static final Color BTN_INDIGO = new Color(79, 70, 229);         // Electric Indigo

    public SafetyFrame(SafetyService safetyService) {
        this.safetyService = safetyService;
        this.cardLayout = new CardLayout();
        this.mainContentPanel = new JPanel(cardLayout);

        setTitle("AEGIS GUARD - Women Safety Master Terminal");
        setSize(1040, 780);
        setMinimumSize(new Dimension(920, 700));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_DARK);

        initUI();
    }

    private void initUI() {
        JPanel rootPanel = new JPanel(new BorderLayout(15, 15)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, BG_DARK, 0, getHeight(), new Color(30, 41, 59));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        rootPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Header Panel
        JPanel headerPanel = createHeaderPanel();
        rootPanel.add(headerPanel, BorderLayout.NORTH);

        // Center Split: Left Navigation & Right Screen Cards
        JPanel centerPanel = new JPanel(new BorderLayout(15, 0));
        centerPanel.setOpaque(false);

        // Left Navigation Menu Sidebar
        JPanel navSidebar = createNavSidebar();
        centerPanel.add(navSidebar, BorderLayout.WEST);

        // Right Main Content Cards
        mainContentPanel.setOpaque(false);
        mainContentPanel.add(createSosCard(), "SOS");
        mainContentPanel.add(createFakeCallCard(), "FAKE_CALL");
        mainContentPanel.add(createGpsCard(), "GPS");
        mainContentPanel.add(createContactsCard(), "CONTACTS");
        mainContentPanel.add(createSirenCard(), "SIREN");
        mainContentPanel.add(createIncidentsCard(), "INCIDENTS");

        centerPanel.add(mainContentPanel, BorderLayout.CENTER);
        rootPanel.add(centerPanel, BorderLayout.CENTER);

        // Footer Bar
        JPanel footerPanel = createFooterPanel();
        rootPanel.add(footerPanel, BorderLayout.SOUTH);

        add(rootPanel);
        showCard("SOS");
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PANEL_DARK);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_GLOW, 2, true),
                new EmptyBorder(12, 20, 12, 20)
        ));

        JLabel titleLabel = new JLabel("AEGIS GUARD - WOMEN SAFETY MASTER");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(TEXT_CYAN);

        statusHeaderLabel = new JLabel("STATUS: ACTIVE  |  GPS LOCKED  |  256-BIT ENCRYPTED VAULT");
        statusHeaderLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        statusHeaderLabel.setForeground(TEXT_GREEN);

        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(statusHeaderLabel, BorderLayout.EAST);
        return panel;
    }

    private JPanel createNavSidebar() {
        JPanel sidebar = new JPanel(new GridLayout(6, 1, 10, 10));
        sidebar.setPreferredSize(new Dimension(240, 0));
        sidebar.setOpaque(false);

        SafetyButton btnSos = new SafetyButton("EMERGENCY SOS", BTN_SOS_RED);
        btnSos.addActionListener(e -> showCard("SOS"));

        SafetyButton btnFakeCall = new SafetyButton("FAKE CALL ESCAPE", BTN_WARNING);
        btnFakeCall.addActionListener(e -> showCard("FAKE_CALL"));

        SafetyButton btnGps = new SafetyButton("LIVE GPS & SAFE ZONES", BTN_SUCCESS);
        btnGps.addActionListener(e -> showCard("GPS"));

        SafetyButton btnContacts = new SafetyButton("CONTACTS VAULT", BTN_PRIMARY);
        btnContacts.addActionListener(e -> {
            refreshContactsTable();
            showCard("CONTACTS");
        });

        SafetyButton btnSiren = new SafetyButton("SIREN & STROBE ALARM", BTN_PURPLE);
        btnSiren.addActionListener(e -> showCard("SIREN"));

        SafetyButton btnIncidents = new SafetyButton("INCIDENT LOGS", BTN_INDIGO);
        btnIncidents.addActionListener(e -> {
            refreshIncidentsTable();
            showCard("INCIDENTS");
        });

        sidebar.add(btnSos);
        sidebar.add(btnFakeCall);
        sidebar.add(btnGps);
        sidebar.add(btnContacts);
        sidebar.add(btnSiren);
        sidebar.add(btnIncidents);

        return sidebar;
    }

    private JPanel createSosCard() {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_GLOW, 2, true),
                new EmptyBorder(20, 20, 20, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("ONE-TOUCH EMERGENCY SOS", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(TEXT_BRIGHT);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        card.add(title, gbc);

        sosStatusLabel = new JLabel("READY - Press SOS button to send location alert to contacts", SwingConstants.CENTER);
        sosStatusLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        sosStatusLabel.setForeground(TEXT_MUTED);
        gbc.gridy = 1;
        card.add(sosStatusLabel, gbc);

        countdownLabel = new JLabel("", SwingConstants.CENTER);
        countdownLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        countdownLabel.setForeground(TEXT_RED);
        gbc.gridy = 2;
        card.add(countdownLabel, gbc);

        // Huge Central SOS Button
        SafetyButton mainSosBtn = new SafetyButton("EMERGENCY SOS", BTN_SOS_RED, Color.WHITE, 24);
        mainSosBtn.setFont(new Font("Segoe UI", Font.BOLD, 26));
        mainSosBtn.setPreferredSize(new Dimension(320, 100));
        mainSosBtn.addActionListener(e -> startSosCountdown());

        gbc.gridy = 3; gbc.insets = new Insets(20, 10, 20, 10);
        card.add(mainSosBtn, gbc);

        // Cancel SOS Button
        SafetyButton cancelSosBtn = new SafetyButton("CANCEL / DEACTIVATE ALARM", BTN_SUCCESS);
        cancelSosBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cancelSosBtn.addActionListener(e -> cancelSosAlert());

        gbc.gridy = 4; gbc.insets = new Insets(10, 10, 10, 10);
        card.add(cancelSosBtn, gbc);

        return card;
    }

    private void startSosCountdown() {
        if (sosCountdownTimer != null && sosCountdownTimer.isRunning()) return;

        countdownSeconds = 3;
        sosStatusLabel.setText("ALERT ACTIVATING! Press CANCEL within countdown to abort.");
        sosStatusLabel.setForeground(TEXT_GOLD);
        countdownLabel.setText("DISPATCHING IN: " + countdownSeconds + "s");

        sosCountdownTimer = new Timer(1000, e -> {
            countdownSeconds--;
            if (countdownSeconds > 0) {
                countdownLabel.setText("DISPATCHING IN: " + countdownSeconds + "s");
            } else {
                sosCountdownTimer.stop();
                executeSosDispatch();
            }
        });
        sosCountdownTimer.start();
    }

    private void executeSosDispatch() {
        String msg = safetyService.triggerSosAlert();
        countdownLabel.setText("SOS EMERGENCY DISPATCHED!");
        sosStatusLabel.setText("SIREN ACTIVE - Alerts sent to " + safetyService.getContacts().size() + " emergency contacts!");
        sosStatusLabel.setForeground(TEXT_RED);
        statusHeaderLabel.setText("EMERGENCY SOS ACTIVE  |  SIREN ALARM ON");
        statusHeaderLabel.setForeground(TEXT_RED);

        JOptionPane.showMessageDialog(this,
                "EMERGENCY SOS ACTIVATED!\n\n" + msg + "\n\nSiren Alarm is currently sounding.",
                "Emergency SOS Dispatched", JOptionPane.ERROR_MESSAGE);
    }

    private void cancelSosAlert() {
        if (sosCountdownTimer != null && sosCountdownTimer.isRunning()) {
            sosCountdownTimer.stop();
        }
        safetyService.cancelSosAlert();
        countdownLabel.setText("");
        sosStatusLabel.setText("READY - Press SOS button to send location alert to contacts");
        sosStatusLabel.setForeground(TEXT_MUTED);
        statusHeaderLabel.setText("STATUS: ACTIVE  |  GPS LOCKED  |  256-BIT ENCRYPTED VAULT");
        statusHeaderLabel.setForeground(TEXT_GREEN);
    }

    private JPanel createFakeCallCard() {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_GLOW, 2, true),
                new EmptyBorder(20, 20, 20, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("DISCREET FAKE CALL ESCAPE GENERATOR", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(TEXT_GOLD);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        card.add(title, gbc);

        JLabel sub = new JLabel("Simulate an incoming phone call to give you a natural excuse to exit uncomfortable situations.", SwingConstants.CENTER);
        sub.setFont(new Font("Segoe UI", Font.BOLD, 12));
        sub.setForeground(TEXT_MUTED);
        gbc.gridy = 1;
        card.add(sub, gbc);

        JLabel callerLabel = new JLabel("Caller Identity:");
        callerLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        callerLabel.setForeground(TEXT_BRIGHT);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1;
        card.add(callerLabel, gbc);

        String[] callers = {"Dad", "Police Inspector Vijay", "Office Boss", "Home Security Operator", "Dr. Sharma"};
        JComboBox<String> callerCombo = new JComboBox<>(callers);
        callerCombo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        callerCombo.setBackground(PANEL_DARK);
        callerCombo.setForeground(TEXT_BRIGHT);
        gbc.gridx = 1;
        card.add(callerCombo, gbc);

        SafetyButton triggerCallBtn = new SafetyButton("TRIGGER INCOMING FAKE CALL NOW", BTN_WARNING, Color.WHITE, 14);
        triggerCallBtn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        triggerCallBtn.setPreferredSize(new Dimension(280, 50));
        triggerCallBtn.addActionListener(e -> {
            String selected = (String) callerCombo.getSelectedItem();
            String callerName = safetyService.triggerFakeCall(selected);
            showIncomingCallDialog(callerName);
        });

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; gbc.insets = new Insets(25, 10, 10, 10);
        card.add(triggerCallBtn, gbc);

        return card;
    }

    private void showIncomingCallDialog(String callerName) {
        JDialog callDialog = new JDialog(this, "INCOMING CALL", true);
        callDialog.setSize(380, 480);
        callDialog.setLocationRelativeTo(this);
        callDialog.setLayout(new BorderLayout());

        JPanel callPanel = new JPanel(new GridBagLayout());
        callPanel.setBackground(new Color(15, 23, 42));
        callPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel callType = new JLabel("INCOMING PHONE CALL", SwingConstants.CENTER);
        callType.setFont(new Font("Segoe UI", Font.BOLD, 12));
        callType.setForeground(TEXT_CYAN);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        callPanel.add(callType, gbc);

        JLabel callerNameLabel = new JLabel(callerName, SwingConstants.CENTER);
        callerNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        callerNameLabel.setForeground(TEXT_BRIGHT);
        gbc.gridy = 1;
        callPanel.add(callerNameLabel, gbc);

        JLabel mobileNum = new JLabel("+91 98765 43210", SwingConstants.CENTER);
        mobileNum.setFont(new Font("Segoe UI", Font.BOLD, 14));
        mobileNum.setForeground(TEXT_MUTED);
        gbc.gridy = 2;
        callPanel.add(mobileNum, gbc);

        JLabel ringStatus = new JLabel("Ringing...", SwingConstants.CENTER);
        ringStatus.setFont(new Font("Segoe UI", Font.BOLD, 16));
        ringStatus.setForeground(TEXT_GREEN);
        gbc.gridy = 3;
        callPanel.add(ringStatus, gbc);

        gbc.gridwidth = 1;

        SafetyButton acceptBtn = new SafetyButton("ACCEPT CALL", BTN_SUCCESS);
        acceptBtn.addActionListener(e -> {
            ringStatus.setText("Call Connected - 00:01");
            JOptionPane.showMessageDialog(callDialog,
                    "Fake Call Connected with " + callerName + ".\nYou can now discreetly excuse yourself and exit safely.",
                    "Fake Call Connected", JOptionPane.INFORMATION_MESSAGE);
            callDialog.dispose();
        });

        SafetyButton declineBtn = new SafetyButton("DECLINE", BTN_DANGER);
        declineBtn.addActionListener(e -> callDialog.dispose());

        gbc.gridx = 0; gbc.gridy = 4; gbc.insets = new Insets(30, 10, 10, 10);
        callPanel.add(acceptBtn, gbc);

        gbc.gridx = 1;
        callPanel.add(declineBtn, gbc);

        callDialog.add(callPanel);
        callDialog.setVisible(true);
    }

    private JPanel createGpsCard() {
        JPanel card = new JPanel(new BorderLayout(15, 15));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_GLOW, 2, true),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel title = new JLabel("LIVE GPS & SAFE ZONE GEOFENCE MONITOR", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(TEXT_BRIGHT);
        card.add(title, BorderLayout.NORTH);

        // Center Info Grid
        JPanel infoGrid = new JPanel(new GridLayout(5, 1, 8, 8));
        infoGrid.setOpaque(false);

        gpsCoordsLabel = new JLabel("Coordinates: " + safetyService.getFormattedCoordinates(), SwingConstants.CENTER);
        gpsCoordsLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        gpsCoordsLabel.setForeground(TEXT_CYAN);

        gpsSpeedLabel = new JLabel("Current Movement Speed: 0.0 km/h", SwingConstants.CENTER);
        gpsSpeedLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gpsSpeedLabel.setForeground(TEXT_MUTED);

        SafeZone zone = safetyService.getCurrentSafeZoneStatus();
        safeZoneNameLabel = new JLabel("Current Zone: " + zone.getName(), SwingConstants.CENTER);
        safeZoneNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        safeZoneNameLabel.setForeground(TEXT_GREEN);

        safeZoneLevelLabel = new JLabel("Security Level: " + zone.getLevel(), SwingConstants.CENTER);
        safeZoneLevelLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        safeZoneLevelLabel.setForeground(TEXT_GREEN);

        safeZoneDescLabel = new JLabel(zone.getDescription(), SwingConstants.CENTER);
        safeZoneDescLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        safeZoneDescLabel.setForeground(TEXT_MUTED);

        infoGrid.add(gpsCoordsLabel);
        infoGrid.add(gpsSpeedLabel);
        infoGrid.add(safeZoneNameLabel);
        infoGrid.add(safeZoneLevelLabel);
        infoGrid.add(safeZoneDescLabel);

        card.add(infoGrid, BorderLayout.CENTER);

        // Simulation Movement Controls
        JPanel simPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        simPanel.setOpaque(false);

        SafetyButton btnCampus = new SafetyButton("Move to Campus (Safe Zone)", BTN_SUCCESS);
        btnCampus.addActionListener(e -> updateGpsSimulation(28.6139, 77.2090, 0.0));

        SafetyButton btnStation = new SafetyButton("Move to Metro Station (Safe Zone)", BTN_PRIMARY);
        btnStation.addActionListener(e -> updateGpsSimulation(28.6289, 77.2190, 12.5));

        SafetyButton btnDarkZone = new SafetyButton("Move to Dark Alley (High Risk)", BTN_SOS_RED);
        btnDarkZone.addActionListener(e -> updateGpsSimulation(28.6500, 77.2500, 4.2));

        simPanel.add(btnCampus);
        simPanel.add(btnStation);
        simPanel.add(btnDarkZone);

        card.add(simPanel, BorderLayout.SOUTH);
        return card;
    }

    private void updateGpsSimulation(double lat, double lng, double speed) {
        safetyService.updateGpsLocation(lat, lng, speed);
        gpsCoordsLabel.setText("Coordinates: " + safetyService.getFormattedCoordinates());
        gpsSpeedLabel.setText(String.format("Current Movement Speed: %.1f km/h", speed));

        SafeZone zone = safetyService.getCurrentSafeZoneStatus();
        safeZoneNameLabel.setText("Current Zone: " + zone.getName());
        safeZoneLevelLabel.setText("Security Level: " + zone.getLevel());
        safeZoneDescLabel.setText(zone.getDescription());

        if (zone.getLevel() == SafeZone.SecurityLevel.SAFE) {
            safeZoneNameLabel.setForeground(TEXT_GREEN);
            safeZoneLevelLabel.setForeground(TEXT_GREEN);
        } else if (zone.getLevel() == SafeZone.SecurityLevel.CAUTION) {
            safeZoneNameLabel.setForeground(TEXT_GOLD);
            safeZoneLevelLabel.setForeground(TEXT_GOLD);
        } else {
            safeZoneNameLabel.setForeground(TEXT_RED);
            safeZoneLevelLabel.setForeground(TEXT_RED);
        }
    }

    private JPanel createContactsCard() {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_GLOW, 2, true),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel title = new JLabel("TRUSTED EMERGENCY CONTACTS VAULT", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(TEXT_BRIGHT);
        card.add(title, BorderLayout.NORTH);

        String[] cols = {"ID", "Name", "Phone Number", "Relation", "Primary Status"};
        contactsTableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };

        JTable table = new JTable(contactsTableModel);
        table.setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.setBackground(CARD_BG);
        table.setForeground(TEXT_BRIGHT);
        table.setGridColor(new Color(51, 65, 85));
        table.getTableHeader().setBackground(new Color(30, 27, 75));
        table.getTableHeader().setForeground(TEXT_CYAN);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.setRowHeight(26);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        table.setDefaultRenderer(Object.class, center);

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(CARD_BG);
        card.add(scroll, BorderLayout.CENTER);

        // Control Buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        actionPanel.setOpaque(false);

        SafetyButton addBtn = new SafetyButton("ADD TRUSTED CONTACT", BTN_SUCCESS);
        addBtn.addActionListener(e -> showAddContactDialog());

        SafetyButton removeBtn = new SafetyButton("REMOVE SELECTED CONTACT", BTN_DANGER);
        removeBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                String id = (String) contactsTableModel.getValueAt(row, 0);
                safetyService.removeContact(id);
                refreshContactsTable();
            } else {
                JOptionPane.showMessageDialog(this, "Please select a contact from table to remove.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            }
        });

        actionPanel.add(addBtn);
        actionPanel.add(removeBtn);

        card.add(actionPanel, BorderLayout.SOUTH);
        return card;
    }

    private void showAddContactDialog() {
        JTextField nameField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField relationField = new JTextField();
        JCheckBox primaryCheck = new JCheckBox("Mark as Primary Emergency Contact");

        Object[] message = {
                "Full Name:", nameField,
                "Phone Number:", phoneField,
                "Relation (Family/Friend/Police):", relationField,
                primaryCheck
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Add Emergency Contact", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                Contact c = new Contact(nameField.getText().trim(), phoneField.getText().trim(),
                        relationField.getText().trim(), primaryCheck.isSelected());
                safetyService.addContact(c);
                refreshContactsTable();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void refreshContactsTable() {
        contactsTableModel.setRowCount(0);
        List<Contact> list = safetyService.getContacts();
        for (Contact c : list) {
            contactsTableModel.addRow(new Object[]{
                    c.getId(), c.getName(), c.getPhoneNumber(), c.getRelation(), c.isPrimary() ? "PRIMARY" : "SECONDARY"
            });
        }
    }

    private JPanel createSirenCard() {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_GLOW, 2, true),
                new EmptyBorder(20, 20, 20, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("HIGH-DECIBEL SIREN & VISUAL STROBE ALARM", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(TEXT_BRIGHT);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        card.add(title, gbc);

        SafetyButton sirenBtn = new SafetyButton("TOGGLE AUDIO SIREN ALARM", BTN_SOS_RED, Color.WHITE, 16);
        sirenBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        sirenBtn.setPreferredSize(new Dimension(300, 60));
        sirenBtn.addActionListener(e -> safetyService.toggleSirenAlarm());

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        card.add(sirenBtn, gbc);

        SafetyButton strobeBtn = new SafetyButton("TOGGLE VISUAL STROBE LIGHT", BTN_PURPLE, Color.WHITE, 16);
        strobeBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        strobeBtn.setPreferredSize(new Dimension(300, 60));
        strobeBtn.addActionListener(e -> toggleStrobeLight());

        gbc.gridy = 2;
        card.add(strobeBtn, gbc);

        return card;
    }

    private void toggleStrobeLight() {
        if (strobeTimer != null && strobeTimer.isRunning()) {
            strobeTimer.stop();
            mainContentPanel.setBackground(CARD_BG);
        } else {
            strobeTimer = new Timer(200, e -> {
                strobeToggle = !strobeToggle;
                mainContentPanel.setBackground(strobeToggle ? Color.RED : Color.WHITE);
            });
            strobeTimer.start();
        }
    }

    private JPanel createIncidentsCard() {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_GLOW, 2, true),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel title = new JLabel("INCIDENT LOG LEDGER & AUDIT TRAIL", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(TEXT_BRIGHT);
        card.add(title, BorderLayout.NORTH);

        String[] cols = {"Incident ID", "Timestamp", "Event Type", "GPS Location", "Event Details"};
        incidentsTableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };

        JTable table = new JTable(incidentsTableModel);
        table.setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.setBackground(CARD_BG);
        table.setForeground(TEXT_BRIGHT);
        table.setGridColor(new Color(51, 65, 85));
        table.getTableHeader().setBackground(new Color(30, 27, 75));
        table.getTableHeader().setForeground(TEXT_CYAN);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.setRowHeight(24);

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(CARD_BG);
        card.add(scroll, BorderLayout.CENTER);

        return card;
    }

    private void refreshIncidentsTable() {
        incidentsTableModel.setRowCount(0);
        List<IncidentRecord> logs = safetyService.getIncidentLogs();
        for (IncidentRecord r : logs) {
            incidentsTableModel.addRow(new Object[]{
                    r.getId(), r.getFormattedTimestamp(), r.getType(), r.getLocationCoords(), r.getDetails()
            });
        }
    }

    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        JLabel foot = new JLabel("Aegis Guard Women Safety Platform v1.0 | National Emergency Hotline: 112 | Women Helpline: 1091", SwingConstants.CENTER);
        foot.setFont(new Font("Segoe UI", Font.BOLD, 12));
        foot.setForeground(TEXT_MUTED);
        panel.add(foot, BorderLayout.CENTER);
        return panel;
    }

    private void showCard(String name) {
        cardLayout.show(mainContentPanel, name);
    }
}
