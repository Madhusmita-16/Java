public class Calculator {
    public static double add(double a, double b) {
        return a + b;
    }

    public static double subtract(double a, double b) {
        return a - b;
    }

    public static double multiply(double a, double b) {
        return a * b;
    }

    public static double divide(double a, double b) {
        if (b == 0) {
            throw new ArithmeticException("Cannot divide by zero.");
        }
        return a / b;
    }

    public static double modulus(double a, double b) {
        if (b == 0) {
            throw new ArithmeticException("Cannot calculate modulus with zero.");
        }
        return a % b;
    }

    public static double percentage(double value) {
        return value / 100.0;
    }

    public static double square(double value) {
        return value * value;
    }

    public static double squareRoot(double value) {
        if (value < 0) {
            throw new ArithmeticException("Cannot calculate square root of a negative number.");
        }
        return Math.sqrt(value);
    }

    public static double power(double base, double exponent) {
        return Math.pow(base, exponent);
    }

    public static double reciprocal(double value) {
        if (value == 0) {
            throw new ArithmeticException("Cannot calculate reciprocal of zero.");
        }
        return 1.0 / value;
    }
}
