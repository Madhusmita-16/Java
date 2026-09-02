package com.riddle.airiddlegame.ai;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.riddle.airiddlegame.entity.Game;
import com.riddle.airiddlegame.entity.GameQuestion;
import com.riddle.airiddlegame.entity.Riddle;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service("simulatedAIRiddleEngine")
public class SimulatedAIRiddleEngine implements AIRiddleEngine {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Random random = new Random();

    @Override
    public Riddle selectNextRiddle(Game game, List<Riddle> candidateRiddles, List<Long> alreadyAskedRiddleIds) {
        // Filter out riddles already asked in this game
        List<Riddle> unasked = candidateRiddles.stream()
                .filter(r -> !alreadyAskedRiddleIds.contains(r.getId()))
                .collect(Collectors.toList());

        if (unasked.isEmpty()) {
            // Fallback: pick any candidate if all asked
            unasked = candidateRiddles;
        }

        if (unasked.isEmpty()) {
            return null;
        }

        // Adaptive difficulty logic:
        // Analyze player's recent performance in this game session
        List<GameQuestion> askedQuestions = game.getQuestions();
        if (askedQuestions != null && !askedQuestions.isEmpty()) {
            int recentQuestions = Math.min(askedQuestions.size(), 3);
            long correctCount = askedQuestions.subList(askedQuestions.size() - recentQuestions, askedQuestions.size())
                    .stream()
                    .filter(gq -> Boolean.TRUE.equals(gq.getIsCorrect()))
                    .count();

            double recentAccuracy = (double) correctCount / recentQuestions;

            // Target difficulty based on recent performance
            String targetDifficulty = game.getDifficulty();
            if (recentAccuracy >= 0.8) {
                // Performing very well -> escalate difficulty if available
                if ("Easy".equalsIgnoreCase(targetDifficulty)) targetDifficulty = "Medium";
                else if ("Medium".equalsIgnoreCase(targetDifficulty)) targetDifficulty = "Hard";
            } else if (recentAccuracy < 0.4) {
                // Struggling -> soften difficulty if available
                if ("Hard".equalsIgnoreCase(targetDifficulty)) targetDifficulty = "Medium";
                else if ("Medium".equalsIgnoreCase(targetDifficulty)) targetDifficulty = "Easy";
            }

            final String desiredDifficulty = targetDifficulty;
            List<Riddle> matchingDifficulty = unasked.stream()
                    .filter(r -> desiredDifficulty.equalsIgnoreCase(r.getDifficulty()))
                    .collect(Collectors.toList());

            if (!matchingDifficulty.isEmpty()) {
                unasked = matchingDifficulty;
            }
        }

        // Intelligent Randomization: Pick random from top candidates
        return unasked.get(random.nextInt(unasked.size()));
    }

    @Override
    public AnswerEvaluationResult evaluateAnswer(Riddle riddle, String userAnswer) {
        if (!StringUtils.hasText(userAnswer)) {
            return new AnswerEvaluationResult(false, riddle.getCorrectAnswer(), "No answer provided.");
        }

        String normUser = normalize(userAnswer);
        String normCorrect = normalize(riddle.getCorrectAnswer());

        // 1. Direct normalized match
        if (normUser.equals(normCorrect)) {
            return new AnswerEvaluationResult(true, riddle.getCorrectAnswer(), "Spot on! Brilliant answer.");
        }

        // 2. Options JSON matching (if user picked A, B, C, D or exact option string)
        if (StringUtils.hasText(riddle.getOptionsJson())) {
            try {
                List<String> options = objectMapper.readValue(riddle.getOptionsJson(), new TypeReference<List<String>>() {});
                String cleanedUser = userAnswer.trim().replaceAll("[^a-zA-Z0-9]", "");
                for (int i = 0; i < options.size(); i++) {
                    String optText = options.get(i);
                    String normOpt = normalize(optText);
                    String optionLetter = String.valueOf((char) ('A' + i));
                    String optionNumber = String.valueOf(i + 1);

                    // Match option index letter ("A", "a", "A.") or number ("1", "2")
                    boolean isLetterMatch = cleanedUser.equalsIgnoreCase(optionLetter) || cleanedUser.equalsIgnoreCase("option" + optionLetter);
                    boolean isNumberMatch = cleanedUser.equals(optionNumber);

                    if ((isLetterMatch || isNumberMatch) && normOpt.equals(normCorrect)) {
                        return new AnswerEvaluationResult(true, riddle.getCorrectAnswer(), "Correct option selected!");
                    }
                    if (normUser.equals(normOpt) && normOpt.equals(normCorrect)) {
                        return new AnswerEvaluationResult(true, riddle.getCorrectAnswer(), "Correct option text matched!");
                    }
                }
            } catch (Exception ignored) {
            }
        }

        // 3. Alternate answers matching (synonyms / variations)
        if (StringUtils.hasText(riddle.getAltAnswersJson())) {
            try {
                List<String> altAnswers = objectMapper.readValue(riddle.getAltAnswersJson(), new TypeReference<List<String>>() {});
                for (String alt : altAnswers) {
                    if (normUser.equals(normalize(alt))) {
                        return new AnswerEvaluationResult(true, riddle.getCorrectAnswer(), "Correct! Alternative phrasing accepted.");
                    }
                }
            } catch (Exception ignored) {
            }
        }

        // 4. Fuzzy Levenshtein Distance (for typos in longer text answers)
        if (normCorrect.length() >= 4 && computeLevenshteinDistance(normUser, normCorrect) <= 1) {
            return new AnswerEvaluationResult(true, riddle.getCorrectAnswer(), "Correct! (Minor typo accepted)");
        }

        return new AnswerEvaluationResult(false, riddle.getCorrectAnswer(), "Incorrect. Keep trying next time!");
    }

    @Override
    public String generateHint(Riddle riddle) {
        if (StringUtils.hasText(riddle.getHint())) {
            return riddle.getHint();
        }
        // AI Fallback hint generation: reveal category & first letter
        String answer = riddle.getCorrectAnswer();
        char firstChar = answer.charAt(0);
        return String.format("Category: %s. The answer starts with '%c' and is %d letters long.",
                riddle.getCategory().getName(), firstChar, answer.length());
    }

    private String normalize(String str) {
        if (str == null) return "";
        String s = str.trim().toLowerCase();
        // Remove common English articles & punctuation
        s = s.replaceAll("^(the|a|an)\\s+", "");
        s = s.replaceAll("[^a-z0-9]", "");
        return s;
    }

    private int computeLevenshteinDistance(String lhs, String rhs) {
        int[][] dp = new int[lhs.length() + 1][rhs.length() + 1];

        for (int i = 0; i <= lhs.length(); i++) {
            for (int j = 0; j <= rhs.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else {
                    dp[i][j] = Math.min(
                            dp[i - 1][j - 1] + (lhs.charAt(i - 1) == rhs.charAt(j - 1) ? 0 : 1),
                            Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1)
                    );
                }
            }
        }
        return dp[lhs.length()][rhs.length()];
    }
}
