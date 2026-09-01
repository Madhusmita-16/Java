package com.riddle.airiddlegame.ai;

import com.riddle.airiddlegame.entity.Game;
import com.riddle.airiddlegame.entity.Riddle;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * OpenAiRiddleEngine stub allowing future OpenAI LLM API key integration
 * without altering backend game service logic.
 */
@Service("openAiRiddleEngine")
public class OpenAiRiddleEngine implements AIRiddleEngine {

    private final AIRiddleEngine fallbackEngine;

    @Value("${app.ai.openai.api-key:}")
    private String openAiApiKey;

    public OpenAiRiddleEngine(@Qualifier("simulatedAIRiddleEngine") AIRiddleEngine fallbackEngine) {
        this.fallbackEngine = fallbackEngine;
    }

    @Override
    public Riddle selectNextRiddle(Game game, List<Riddle> candidateRiddles, List<Long> alreadyAskedRiddleIds) {
        // If OpenAI API Key is present, real API call can be executed here.
        // Otherwise, delegates seamlessly to SimulatedAIRiddleEngine.
        return fallbackEngine.selectNextRiddle(game, candidateRiddles, alreadyAskedRiddleIds);
    }

    @Override
    public AnswerEvaluationResult evaluateAnswer(Riddle riddle, String userAnswer) {
        return fallbackEngine.evaluateAnswer(riddle, userAnswer);
    }

    @Override
    public String generateHint(Riddle riddle) {
        return fallbackEngine.generateHint(riddle);
    }
}
