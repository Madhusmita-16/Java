public class ScoreBoard {
    private int player1Wins;
    private int player2Wins;
    private int draws;

    public void recordPlayer1Win() {
        player1Wins++;
    }

    public void recordPlayer2Win() {
        player2Wins++;
    }

    public void recordDraw() {
        draws++;
    }

    public int getPlayer1Wins() {
        return player1Wins;
    }

    public int getPlayer2Wins() {
        return player2Wins;
    }

    public int getDraws() {
        return draws;
    }
}
