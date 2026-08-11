# Tic Tac Toe Java

A simple two-player Tic Tac Toe game built with Java Swing.

## Project Folder

`f:\Java\tictactoe`

## Files

- `Main.java` - Starts the application.
- `Game.java` - Controls game flow, turns, and win/draw checks.
- `Player.java` - Stores player name and symbol.
- `Board.java` - Manages the 3×3 board state and move validation.
- `GameStatus.java` - Defines `PLAYING`, `PLAYER_WON`, and `DRAW` states.
- `ScoreBoard.java` - Tracks player wins and draws.
- `TicTacToeGUI.java` - Builds the Swing interface and handles UI events.
- `.gitignore` - Prevents compiled `.class` files from being committed.

## What It Does

- Shows a welcome screen for player names.
- Displays a clickable 3×3 board.
- Alternates turns between X and O.
- Detects wins across rows, columns, and diagonals.
- Detects draws when the board fills.
- Highlights winning cells and freezes the board.
- Supports restart, new game, and exit confirmation.
- Validates empty names and duplicate moves.

## How to Run

From `f:\Java`:

```powershell
javac tictactoe\*.java
java -cp tictactoe Main
```

## Navigation Video

A short navigation video can help you follow the game flow and menu options. If you have a demo video, place it in the project folder and update this link accordingly.

- Demo video: [Watch the navigation video](https://example.com/navigation-demo)

> Tip: Replace the example link with a real video URL or local file path such as `navigation-demo.mp4`.

## Clean Up

To remove compiled files:

```powershell
Remove-Item -Force f:\Java\tictactoe\*.class
```

## Notes

- This project uses Java Swing only, no database required.
- The game is designed for a desktop environment.

Enjoy the game! 🎮
