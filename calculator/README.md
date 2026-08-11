# Smart Calculator Java

A beginner-friendly Java Swing calculator application with basic and advanced operations, calculation history, and error handling.

## Project Structure

- `Main.java` - starts the application.
- `Calculator.java` - performs mathematical operations.
- `Calculation.java` - stores individual calculation expressions and results.
- `CalculationHistory.java` - manages and formats recent calculations.
- `CalculatorGUI.java` - builds the Swing user interface and handles events.

## Features

- Basic operations: addition, subtraction, multiplication, division, modulus
- Advanced operations: percentage, square, square root, power, reciprocal
- Decimal input support
- Positive/negative toggle
- Delete/backspace and clear operators
- Calculation history with clear history button
- User-friendly error handling for invalid input and division by zero

## Preview

[![Calculator Preview](F:\Java\calculator\calculator.png)](https://github.com/Madhusmita-16/Java/tree/main/calculator)



## How to Run

From `f:\Java`:

```powershell
javac .\calculator\*.java
java -cp .\calculator Main
```

## Notes

- This application uses Java Swing for the interface.
- No database is required.
- The window is resizable and supports maximize.
