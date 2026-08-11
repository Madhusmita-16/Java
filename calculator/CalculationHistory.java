import java.util.ArrayList;
import java.util.List;

public class CalculationHistory {
    private final List<Calculation> history;

    public CalculationHistory() {
        history = new ArrayList<>();
    }

    public void addCalculation(Calculation calculation) {
        history.add(0, calculation);
        if (history.size() > 10) {
            history.remove(history.size() - 1);
        }
    }

    public void clearHistory() {
        history.clear();
    }

    public String getFormattedHistory() {
        if (history.isEmpty()) {
            return "No history yet.";
        }
        StringBuilder builder = new StringBuilder();
        for (Calculation calculation : history) {
            builder.append(calculation).append("\n");
        }
        return builder.toString();
    }
}
