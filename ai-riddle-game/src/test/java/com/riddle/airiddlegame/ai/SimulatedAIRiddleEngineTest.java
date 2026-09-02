package com.riddle.airiddlegame.ai;

import com.riddle.airiddlegame.entity.Category;
import com.riddle.airiddlegame.entity.Game;
import com.riddle.airiddlegame.entity.GameQuestion;
import com.riddle.airiddlegame.entity.Riddle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SimulatedAIRiddleEngineTest {

    private SimulatedAIRiddleEngine engine;
    private Riddle sampleRiddle;
    private Category category;

    @BeforeEach
    void setUp() {
        engine = new SimulatedAIRiddleEngine();

        category = new Category();
        category.setId(1L);
        category.setName("Logic");
        category.setDescription("Puzzles and logical reasoning");

        sampleRiddle = new Riddle();
        sampleRiddle.setId(101L);
        sampleRiddle.setQuestion("What has hands but cannot clap?");
        sampleRiddle.setCorrectAnswer("Clock");
        sampleRiddle.setOptionsJson("[\"Clock\", \"Mirror\", \"Watch\", \"Statue\"]");
        sampleRiddle.setAltAnswersJson("[\"wall clock\", \"a clock\", \"alarm clock\"]");
        sampleRiddle.setHint("It tells time.");
        sampleRiddle.setDifficulty("Medium");
        sampleRiddle.setCategory(category);
        sampleRiddle.setBasePoints(150);
    }

    @Test
    @DisplayName("Evaluate answer - Exact match & article normalization")
    void testEvaluateAnswer_ExactAndNormalizedMatch() {
        var res1 = engine.evaluateAnswer(sampleRiddle, "Clock");
        assertTrue(res1.isCorrect());
        assertEquals("Clock", res1.getCorrectAnswer());

        var res2 = engine.evaluateAnswer(sampleRiddle, "a clock");
        assertTrue(res2.isCorrect());

        var res3 = engine.evaluateAnswer(sampleRiddle, "  THE CLOCK! ");
        assertTrue(res3.isCorrect());
    }

    @Test
    @DisplayName("Evaluate answer - Multiple choice letter and option number match")
    void testEvaluateAnswer_OptionsMatching() {
        // Option 'A' is "Clock"
        var resA = engine.evaluateAnswer(sampleRiddle, "A");
        assertTrue(resA.isCorrect());

        var resAFormatted = engine.evaluateAnswer(sampleRiddle, "A.");
        assertTrue(resAFormatted.isCorrect());

        var resOptionA = engine.evaluateAnswer(sampleRiddle, "Option A");
        assertTrue(resOptionA.isCorrect());

        var res1 = engine.evaluateAnswer(sampleRiddle, "1");
        assertTrue(res1.isCorrect());

        // Option 'B' is "Mirror" which is incorrect
        var resB = engine.evaluateAnswer(sampleRiddle, "B");
        assertFalse(resB.isCorrect());
    }

    @Test
    @DisplayName("Evaluate answer - Synonym alternative answers match")
    void testEvaluateAnswer_AltAnswersMatch() {
        var res1 = engine.evaluateAnswer(sampleRiddle, "wall clock");
        assertTrue(res1.isCorrect());

        var res2 = engine.evaluateAnswer(sampleRiddle, "alarm clock");
        assertTrue(res2.isCorrect());
    }

    @Test
    @DisplayName("Evaluate answer - Typo tolerance via Levenshtein distance")
    void testEvaluateAnswer_TypoTolerance() {
        // "Clok" has 1 edit distance from "Clock"
        var resTypo = engine.evaluateAnswer(sampleRiddle, "clok");
        assertTrue(resTypo.isCorrect());
        assertTrue(resTypo.getFeedback().contains("Minor typo"));
    }

    @Test
    @DisplayName("Evaluate answer - Incorrect answer and empty string")
    void testEvaluateAnswer_IncorrectAndEmpty() {
        var resEmpty = engine.evaluateAnswer(sampleRiddle, "");
        assertFalse(resEmpty.isCorrect());

        var resWrong = engine.evaluateAnswer(sampleRiddle, "Banana");
        assertFalse(resWrong.isCorrect());
    }

    @Test
    @DisplayName("Generate hint - Custom hint vs AI fallback hint")
    void testGenerateHint() {
        String hint1 = engine.generateHint(sampleRiddle);
        assertEquals("It tells time.", hint1);

        sampleRiddle.setHint(null);
        String hint2 = engine.generateHint(sampleRiddle);
        assertTrue(hint2.contains("starts with 'C'"));
        assertTrue(hint2.contains("Logic"));
    }

    @Test
    @DisplayName("Select next riddle - Adaptive difficulty scaling")
    void testSelectNextRiddle_AdaptiveDifficulty() {
        Riddle easyRiddle = new Riddle();
        easyRiddle.setId(102L);
        easyRiddle.setDifficulty("Easy");

        Riddle hardRiddle = new Riddle();
        hardRiddle.setId(103L);
        hardRiddle.setDifficulty("Hard");

        List<Riddle> candidates = List.of(sampleRiddle, easyRiddle, hardRiddle);

        Game game = new Game();
        game.setDifficulty("Medium");
        List<GameQuestion> askedQuestions = new ArrayList<>();

        // 3 consecutive correct answers -> escalate to Hard
        for (int i = 0; i < 3; i++) {
            GameQuestion gq = new GameQuestion();
            gq.setIsCorrect(true);
            askedQuestions.add(gq);
        }
        game.setQuestions(askedQuestions);

        Riddle selected = engine.selectNextRiddle(game, candidates, List.of());
        assertNotNull(selected);
        assertEquals("Hard", selected.getDifficulty());
    }
}
