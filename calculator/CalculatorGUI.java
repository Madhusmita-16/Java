import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CalculatorGUI {
    private JFrame frame;
    private JTextField displayField;
    private JTextArea historyArea;
    private CalculationHistory history;
    private String firstValue = "";
    private String operator = "";
    private boolean startNewValue = true;

    public CalculatorGUI() {
        history = new CalculationHistory();
        createInterface();
    }

    private void createInterface() {
        frame = new JFrame("Smart Calculator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(420, 620);
        frame.setMinimumSize(new Dimension(420, 620));
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout(10, 10));
        frame.setResizable(true);

        JPanel displayPanel = createDisplayPanel();
        JPanel buttonPanel = createButtonPanel();
        JPanel historyPanel = createHistoryPanel();

        frame.add(displayPanel, BorderLayout.NORTH);
        frame.add(buttonPanel, BorderLayout.CENTER);
        frame.add(historyPanel, BorderLayout.EAST);

        frame.setVisible(true);
    }

    private JPanel createDisplayPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(new EmptyBorder(12, 12, 0, 12));
        panel.setBackground(new Color(245, 245, 245));

        displayField = new JTextField("0");
        displayField.setFont(new Font("Segoe UI", Font.BOLD, 28));
        displayField.setHorizontalAlignment(SwingConstants.RIGHT);
        displayField.setEditable(false);
        displayField.setBackground(Color.WHITE);
        displayField.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));

        panel.add(displayField, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new GridLayout(6, 4, 6, 6));
        panel.setBorder(new EmptyBorder(0, 10, 10, 10));
        panel.setBackground(new Color(245, 245, 245));

        String[] buttons = {
                "C", "Del", "%", "÷",
                "7", "8", "9", "×",
                "4", "5", "6", "-",
                "1", "2", "3", "+",
                "+/-", "0", ".", "=",
                "x²", "√", "xʸ", "1/x"
        };

        for (String label : buttons) {
            JButton button = createButton(label);
            panel.add(button);
        }

        return panel;
    }

    private JPanel createHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(new EmptyBorder(12, 0, 12, 12));
        panel.setBackground(new Color(245, 245, 245));
        panel.setPreferredSize(new Dimension(220, 0));

        JLabel historyLabel = new JLabel("History");
        historyLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        historyLabel.setBorder(new EmptyBorder(0, 0, 8, 0));

        historyArea = new JTextArea();
        historyArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        historyArea.setEditable(false);
        historyArea.setLineWrap(true);
        historyArea.setWrapStyleWord(true);
        historyArea.setBackground(new Color(250, 250, 250));
        historyArea.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        historyArea.setText(history.getFormattedHistory());

        JButton clearHistory = new JButton("Clear History");
        clearHistory.setFont(new Font("Segoe UI", Font.BOLD, 14));
        clearHistory.setBackground(new Color(38, 166, 154));
        clearHistory.setForeground(Color.WHITE);
        clearHistory.setFocusPainted(false);
        clearHistory.addActionListener(e -> {
            history.clearHistory();
            updateHistoryArea();
        });

        panel.add(historyLabel, BorderLayout.NORTH);
        panel.add(new JScrollPane(historyArea), BorderLayout.CENTER);
        panel.add(clearHistory, BorderLayout.SOUTH);
        return panel;
    }

    private JButton createButton(String label) {
        JButton button = new JButton(label);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setBackground(new Color(248, 248, 248));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(new Color(215, 215, 215), 1));
        button.setPreferredSize(new Dimension(80, 60));
        button.addActionListener(new CalculatorButtonListener());
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(230, 240, 255));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(248, 248, 248));
            }
        });
        return button;
    }

    private void updateHistoryArea() {
        historyArea.setText(history.getFormattedHistory());
    }

    private void updateDisplay(String value) {
        displayField.setText(value);
    }

    private class CalculatorButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String command = ((JButton) e.getSource()).getText();
            try {
                handleButton(command);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleButton(String command) {
        switch (command) {
            case "C" -> clearAll();
            case "⌫", "Del" -> deleteLast();
            case "+/-" -> toggleSign();
            case "%" -> applyPercentage();
            case "x²" -> applySquare();
            case "√" -> applySquareRoot();
            case "xʸ" -> applyPower();
            case "1/x" -> applyReciprocal();
            case "+", "-", "×", "÷" -> setOperator(command);
            case "=" -> calculateResult();
            default -> appendDigit(command);
        }
    }

    private void clearAll() {
        firstValue = "";
        operator = "";
        startNewValue = true;
        updateDisplay("0");
    }

    private void deleteLast() {
        String text = displayField.getText();
        if (text.length() > 1) {
            updateDisplay(text.substring(0, text.length() - 1));
        } else {
            updateDisplay("0");
        }
    }

    private void toggleSign() {
        String text = displayField.getText();
        if (text.equals("0")) {
            return;
        }
        if (text.startsWith("-")) {
            updateDisplay(text.substring(1));
        } else {
            updateDisplay("-" + text);
        }
    }

    private void applyPercentage() {
        double value = parseDisplay();
        double result = Calculator.percentage(value);
        String expression = value + "%";
        saveResult(expression, result);
    }

    private void applySquare() {
        double value = parseDisplay();
        double result = Calculator.square(value);
        String expression = "sqr(" + value + ")";
        saveResult(expression, result);
    }

    private void applySquareRoot() {
        double value = parseDisplay();
        double result = Calculator.squareRoot(value);
        String expression = "√(" + value + ")";
        saveResult(expression, result);
    }

    private void applyPower() {
        if (operator.isEmpty()) {
            firstValue = displayField.getText();
            operator = "^";
            startNewValue = true;
            return;
        }
        calculateResult();
    }

    private void applyReciprocal() {
        double value = parseDisplay();
        double result = Calculator.reciprocal(value);
        String expression = "1/" + value;
        saveResult(expression, result);
    }

    private void setOperator(String command) {
        if (!operator.isEmpty()) {
            calculateResult();
        }
        firstValue = displayField.getText();
        operator = command;
        startNewValue = true;
    }

    private void calculateResult() {
        if (operator.isEmpty()) {
            return;
        }

        double value1 = parseNumber(firstValue);
        double value2 = parseDisplay();
        double result;
        String expression;

        switch (operator) {
            case "+" -> {
                result = Calculator.add(value1, value2);
                expression = firstValue + " + " + value2;
            }
            case "-" -> {
                result = Calculator.subtract(value1, value2);
                expression = firstValue + " - " + value2;
            }
            case "×" -> {
                result = Calculator.multiply(value1, value2);
                expression = firstValue + " × " + value2;
            }
            case "÷" -> {
                result = Calculator.divide(value1, value2);
                expression = firstValue + " ÷ " + value2;
            }
            case "^" -> {
                result = Calculator.power(value1, value2);
                expression = firstValue + " ^ " + value2;
            }
            default -> throw new IllegalStateException("Unknown operator: " + operator);
        }

        saveResult(expression, result);
        operator = "";
        startNewValue = true;
    }

    private void appendDigit(String digit) {
        if (startNewValue) {
            updateDisplay("0");
            startNewValue = false;
        }

        String currentText = displayField.getText();
        if (currentText.equals("0") && !digit.equals(".")) {
            updateDisplay(digit);
            return;
        }

        if (digit.equals(".") && currentText.contains(".")) {
            return;
        }

        updateDisplay(currentText + digit);
    }

    private double parseDisplay() {
        return parseNumber(displayField.getText());
    }

    private double parseNumber(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid input. Please enter a valid number.");
        }
    }

    private void saveResult(String expression, double result) {
        String resultText = formatResult(result);
        updateDisplay(resultText);
        history.addCalculation(new Calculation(expression, resultText));
        updateHistoryArea();
    }

    private String formatResult(double value) {
        if (value == (long) value) {
            return String.format("%d", (long) value);
        }
        return String.format("%.8f", value).replaceAll("0+$", "").replaceAll("\\.$", "");
    }
}
