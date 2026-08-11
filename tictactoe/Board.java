public class Board {
    private final char[][] grid;
    private static final int SIZE = 3;

    public Board() {
        grid = new char[SIZE][SIZE];
        reset();
    }

    public boolean isCellEmpty(int row, int col) {
        return grid[row][col] == ' ';
    }

    public void setCell(int row, int col, char symbol) {
        if (!isCellEmpty(row, col)) {
            throw new IllegalArgumentException("Cell is already taken.");
        }
        grid[row][col] = symbol;
    }

    public char getCell(int row, int col) {
        return grid[row][col];
    }

    public void reset() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                grid[row][col] = ' ';
            }
        }
    }

    public boolean isFull() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (grid[row][col] == ' ') {
                    return false;
                }
            }
        }
        return true;
    }

    public char[][] getGridCopy() {
        char[][] copy = new char[SIZE][SIZE];
        for (int row = 0; row < SIZE; row++) {
            System.arraycopy(grid[row], 0, copy[row], 0, SIZE);
        }
        return copy;
    }
}
