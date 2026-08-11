public class Game {
    private final Board board;
    private final Player player1;
    private final Player player2;
    private Player currentPlayer;
    private GameStatus status;
    private int[][] winningCells;

    public Game(Player player1, Player player2) {
        this.player1 = player1;
        this.player2 = player2;
        this.board = new Board();
        this.currentPlayer = player1;
        this.status = GameStatus.PLAYING;
        this.winningCells = new int[0][0];
    }

    public void reset() {
        board.reset();
        currentPlayer = player1;
        status = GameStatus.PLAYING;
        winningCells = new int[0][0];
    }

    public void playMove(int row, int col) {
        if (status != GameStatus.PLAYING) {
            throw new IllegalStateException("Game is not currently in play.");
        }
        if (!board.isCellEmpty(row, col)) {
            throw new IllegalArgumentException("This cell is already selected.");
        }

        board.setCell(row, col, currentPlayer.getSymbol());
        updateStatus(row, col);
        if (status == GameStatus.PLAYING) {
            swapTurn();
        }
    }

    private void updateStatus(int row, int col) {
        if (hasWinningLine(row, col)) {
            status = GameStatus.PLAYER_WON;
            return;
        }
        if (board.isFull()) {
            status = GameStatus.DRAW;
        }
    }

    private boolean hasWinningLine(int row, int col) {
        char symbol = currentPlayer.getSymbol();

        boolean rowWin = true;
        for (int c = 0; c < 3; c++) {
            if (board.getCell(row, c) != symbol) {
                rowWin = false;
                break;
            }
        }
        if (rowWin) {
            setWinningCells(new int[][]{{row, 0}, {row, 1}, {row, 2}});
            return true;
        }

        boolean colWin = true;
        for (int r = 0; r < 3; r++) {
            if (board.getCell(r, col) != symbol) {
                colWin = false;
                break;
            }
        }
        if (colWin) {
            setWinningCells(new int[][]{{0, col}, {1, col}, {2, col}});
            return true;
        }

        if (row == col) {
            boolean diag1Win = true;
            for (int i = 0; i < 3; i++) {
                if (board.getCell(i, i) != symbol) {
                    diag1Win = false;
                    break;
                }
            }
            if (diag1Win) {
                setWinningCells(new int[][]{{0, 0}, {1, 1}, {2, 2}});
                return true;
            }
        }

        if (row + col == 2) {
            boolean diag2Win = true;
            for (int i = 0; i < 3; i++) {
                if (board.getCell(i, 2 - i) != symbol) {
                    diag2Win = false;
                    break;
                }
            }
            if (diag2Win) {
                setWinningCells(new int[][]{{0, 2}, {1, 1}, {2, 0}});
                return true;
            }
        }

        return false;
    }

    private void setWinningCells(int[][] winningCells) {
        this.winningCells = winningCells;
    }

    private void swapTurn() {
        currentPlayer = currentPlayer == player1 ? player2 : player1;
    }

    public Board getBoard() {
        return board;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public GameStatus getStatus() {
        return status;
    }

    public int[][] getWinningCells() {
        return winningCells;
    }
}
