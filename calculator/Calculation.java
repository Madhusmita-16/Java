public class Calculation {
    private final String expression;
    private final String result;

    public Calculation(String expression, String result) {
        this.expression = expression;
        this.result = result;
    }

    public String getExpression() {
        return expression;
    }

    public String getResult() {
        return result;
    }

    @Override
    public String toString() {
        return expression + " = " + result;
    }
}
