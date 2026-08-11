import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TicTacToeGUI {
    private JFrame frame;
    private JTextField player1Field;
    private JTextField player2Field;
    private JLabel statusLabel;
    private JButton[][] cells;
    private JButton startButton;
    private JButton restartButton;
    private JButton newGameButton;
    private JButton exitButton;
    private JLabel scoreLabel;
    private Game game;
    private ScoreBoard scoreBoard;

    public TicTacToeGUI() {
        scoreBoard = new ScoreBoard();
        createMainFrame();
    }

    private void createMainFrame() {
        frame = new JFrame("Tic Tac Toe");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(520, 640);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout(12, 12));
        frame.setResizable(false);

        JPanel headerPanel = createHeaderPanel();
        JPanel centerPanel = createCenterPanel();
        JPanel footerPanel = createFooterPanel();

        frame.add(headerPanel, BorderLayout.NORTH);
        frame.add(centerPanel, BorderLayout.CENTER);
        frame.add(footerPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(16, 16, 0, 16));
        panel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Tic Tac Toe", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(new Color(55, 71, 79));

        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        inputPanel.setBackground(Color.WHITE);

        inputPanel.add(new JLabel("Player 1 (X):", SwingConstants.RIGHT));
        player1Field = new JTextField();
        inputPanel.add(player1Field);

        inputPanel.add(new JLabel("Player 2 (O):", SwingConstants.RIGHT));
        player2Field = new JTextField();
        inputPanel.add(player2Field);

        panel.add(inputPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBorder(new EmptyBorder(8, 16, 8, 16));
        panel.setBackground(new Color(250, 250, 250));

        JPanel boardPanel = new JPanel(new GridLayout(3, 3, 10, 10));
        boardPanel.setBackground(new Color(250, 250, 250));

        cells = new JButton[3][3];
        Font cellFont = new Font("Segoe UI", Font.BOLD, 32);

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                JButton button = new JButton(" ");
                button.setFont(cellFont);
                button.setFocusPainted(false);
                button.setBackground(new Color(255, 255, 255));
                button.setOpaque(true);
                button.setBorder(BorderFactory.createLineBorder(new Color(187, 187, 187), 2));
                final int currentRow = row;
                final int currentCol = col;
                button.addActionListener(e -> handleCellSelection(currentRow, currentCol));
                button.addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseEntered(java.awt.event.MouseEvent e) {
                        if (button.isEnabled()) {
                            button.setBackground(new Color(230, 245, 255));
                        }
                    }

                    @Override
                    public void mouseExited(java.awt.event.MouseEvent e) {
                        if (button.isEnabled()) {
                            button.setBackground(Color.WHITE);
                        }
                    }
                });
                cells[row][col] = button;
                boardPanel.add(button);
            }
        }

        statusLabel = new JLabel("Enter player names and click Start Game", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        statusLabel.setBorder(new EmptyBorder(12, 0, 12, 0));
        statusLabel.setForeground(new Color(48, 63, 159));

        panel.add(statusLabel, BorderLayout.NORTH);
        panel.add(boardPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(0, 16, 16, 16));
        panel.setBackground(Color.WHITE);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 12, 0));
        buttonPanel.setBackground(Color.WHITE);

        startButton = createMenuButton("Start Game");
        startButton.addActionListener(e -> initializeGame());
        buttonPanel.add(startButton);

        restartButton = createMenuButton("Restart Game");
        restartButton.setEnabled(false);
        restartButton.addActionListener(e -> restartGame());
        buttonPanel.add(restartButton);

        newGameButton = createMenuButton("New Game");
        newGameButton.setEnabled(false);
        newGameButton.addActionListener(e -> resetToNewGame());
        buttonPanel.add(newGameButton);

        exitButton = createMenuButton("Exit");
        exitButton.addActionListener(e -> confirmAndExit());

        scoreLabel = new JLabel("Player 1: 0    Player 2: 0    Draws: 0", SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        scoreLabel.setForeground(new Color(84, 110, 122));

        panel.add(buttonPanel, BorderLayout.NORTH);
        panel.add(scoreLabel, BorderLayout.CENTER);
        panel.add(exitButton, BorderLayout.SOUTH);
        return panel;
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(new Color(38, 166, 154));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        button.setOpaque(true);
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(29, 151, 134));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(38, 166, 154));
            }

            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(0, 121, 107));
            }

            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(38, 166, 154));
            }
        });
        return button;
    }

    private void initializeGame() {
        String player1Name = player1Field.getText().trim();
        String player2Name = player2Field.getText().trim();

        if (player1Name.isEmpty() || player2Name.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please enter both player names.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (player1Name.equalsIgnoreCase(player2Name)) {
            JOptionPane.showMessageDialog(frame, "Player names must be different.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
            return;
        }

        game = new Game(new Player(player1Name, 'X'), new Player(player2Name, 'O'));
        restartButton.setEnabled(true);
        newGameButton.setEnabled(true);
        startButton.setEnabled(false);
        player1Field.setEnabled(false);
        player2Field.setEnabled(false);
        updateBoardView();
        updateStatusLabel("Current Turn: " + game.getCurrentPlayer().getName() + " (" + game.getCurrentPlayer().getSymbol() + ")");
    }

    private void handleCellSelection(int row, int col) {
        if (game == null) {
            JOptionPane.showMessageDialog(frame, "Start the game before choosing a cell.", "Game Not Started", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        try {
            game.playMove(row, col);
            updateBoardView();
            if (game.getStatus() == GameStatus.PLAYER_WON) {
                handleWin();
            } else if (game.getStatus() == GameStatus.DRAW) {
                handleDraw();
            } else {
                updateStatusLabel("Current Turn: " + game.getCurrentPlayer().getName() + " (" + game.getCurrentPlayer().getSymbol() + ")");
            }
        } catch (IllegalArgumentException | IllegalStateException ex) {
            JOptionPane.showMessageDialog(frame, ex.getMessage(), "Invalid Move", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void handleWin() {
        highlightWinningCells();
        disableBoard();
        if (game.getCurrentPlayer() == game.getPlayer1()) {
            scoreBoard.recordPlayer1Win();
        } else {
            scoreBoard.recordPlayer2Win();
        }
        updateScoreLabel();
        updateStatusLabel("Winner: " + game.getCurrentPlayer().getName() + " (" + game.getCurrentPlayer().getSymbol() + ")");
        JOptionPane.showMessageDialog(frame, game.getCurrentPlayer().getName() + " wins!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleDraw() {
        disableBoard();
        scoreBoard.recordDraw();
        updateScoreLabel();
        updateStatusLabel("Game Draw");
        JOptionPane.showMessageDialog(frame, "The game is a draw!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
    }

    private void resetToNewGame() {
        int choice = JOptionPane.showConfirmDialog(frame, "Start a new game and reset the board?", "New Game Confirmation", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            player1Field.setEnabled(true);
            player2Field.setEnabled(true);
            startButton.setEnabled(true);
            restartButton.setEnabled(false);
            newGameButton.setEnabled(false);
            clearBoard();
            updateStatusLabel("Enter player names and click Start Game");
        }
    }

    private void restartGame() {
        if (game == null) {
            return;
        }
        int choice = JOptionPane.showConfirmDialog(frame, "Restart the current game?", "Restart Confirmation", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            game.reset();
            clearBoard();
            updateStatusLabel("Current Turn: " + game.getCurrentPlayer().getName() + " (" + game.getCurrentPlayer().getSymbol() + ")");
            enableBoard();
        }
    }

    private void confirmAndExit() {
        int choice = JOptionPane.showConfirmDialog(frame, "Are you sure you want to exit?", "Exit Confirmation", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            frame.dispose();
        }
    }

    private void updateBoardView() {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                char symbol = game.getBoard().getCell(row, col);
                JButton button = cells[row][col];
                button.setText(symbol == ' ' ? "" : String.valueOf(symbol));
                button.setEnabled(symbol == ' ' && game.getStatus() == GameStatus.PLAYING);
                button.setBackground(symbol == ' ' ? Color.WHITE : new Color(236, 239, 241));
            }
        }
    }

    private void disableBoard() {
        for (JButton[] rowButtons : cells) {
            for (JButton button : rowButtons) {
                button.setEnabled(false);
            }
        }
    }

    private void enableBoard() {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (game.getBoard().isCellEmpty(row, col)) {
                    cells[row][col].setEnabled(true);
                    cells[row][col].setBackground(Color.WHITE);
                }
            }
        }
    }

    private void clearBoard() {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                JButton button = cells[row][col];
                button.setText("");
                button.setEnabled(false);
                button.setBackground(Color.WHITE);
                button.setBorder(BorderFactory.createLineBorder(new Color(187, 187, 187), 2));
            }
        }
    }

    private void highlightWinningCells() {
        int[][] winningCells = game.getWinningCells();
        for (int[] cell : winningCells) {
            JButton button = cells[cell[0]][cell[1]];
            button.setBackground(new Color(255, 235, 59));
            button.setBorder(BorderFactory.createLineBorder(new Color(255, 193, 7), 3));
        }
    }

    private void updateStatusLabel(String message) {
        statusLabel.setText(message);
    }

    private void updateScoreLabel() {
        scoreLabel.setText(String.format("%s: %d    %s: %d    Draws: %d",
                game.getPlayer1().getName(), scoreBoard.getPlayer1Wins(),
                game.getPlayer2().getName(), scoreBoard.getPlayer2Wins(),
                scoreBoard.getDraws()));
    }
}
