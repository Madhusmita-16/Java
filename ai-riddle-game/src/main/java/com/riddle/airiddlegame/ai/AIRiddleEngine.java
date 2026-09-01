package com.riddle.airiddlegame.ai;

import com.riddle.airiddlegame.entity.Game;
import com.riddle.airiddlegame.entity.Riddle;

import java.util.List;

public interface AIRiddleEngine {

    class AnswerEvaluationResult {
        private final boolean isCorrect;
        private final String correctAnswer;
        private final String feedback;

        public AnswerEvaluationResult(boolean isCorrect, String correctAnswer, String feedback) {
            this.isCorrect = isCorrect;
            this.correctAnswer = correctAnswer;
            this.feedback = feedback;
        }

        public boolean isCorrect() {
            return isCorrect;
        }

        public String getCorrectAnswer() {
            return correctAnswer;
        }

        public String getFeedback() {
            return feedback;
        }
    }

    /**
     * Adaptively selects the next riddle for a game based on difficulty, category, and past history.
     */
    Riddle selectNextRiddle(Game game, List<Riddle> candidateRiddles, List<Long> alreadyAskedRiddleIds);

    /**
     * Evaluates a player's answer with intelligent fuzzy keyword and alternative answer matching.
     */
    AnswerEvaluationResult evaluateAnswer(Riddle riddle, String userAnswer);

    /**
     * Generates or retrieves a hint for the given riddle.
     */
    String generateHint(Riddle riddle);
}
