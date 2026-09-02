package com.riddle.airiddlegame.service;

import com.riddle.airiddlegame.ai.AIRiddleEngine;
import com.riddle.airiddlegame.dto.request.GameStartRequest;
import com.riddle.airiddlegame.dto.request.SubmitAnswerRequest;
import com.riddle.airiddlegame.dto.response.AnswerResultDto;
import com.riddle.airiddlegame.dto.response.QuestionDto;
import com.riddle.airiddlegame.entity.*;
import com.riddle.airiddlegame.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {

    @Mock
    private GameRepository gameRepository;

    @Mock
    private GameQuestionRepository gameQuestionRepository;

    @Mock
    private RiddleRepository riddleRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ScoreRepository scoreRepository;

    @Mock
    private AIRiddleEngine aiRiddleEngine;

    @InjectMocks
    private GameService gameService;

    private User sampleUser;
    private Category sampleCategory;
    private Riddle sampleRiddle;
    private Game sampleGame;

    @BeforeEach
    void setUp() {
        sampleUser = new User("player1", "player1@riddle.com", "password", Role.ROLE_USER);
        sampleUser.setId(1L);

        sampleCategory = new Category();
        sampleCategory.setId(10L);
        sampleCategory.setName("Logic");

        sampleRiddle = new Riddle();
        sampleRiddle.setId(100L);
        sampleRiddle.setQuestion("What has to be broken before you can use it?");
        sampleRiddle.setCorrectAnswer("An egg");
        sampleRiddle.setCategory(sampleCategory);
        sampleRiddle.setDifficulty("Medium");
        sampleRiddle.setBasePoints(100);

        sampleGame = new Game(sampleUser, "Medium", "Logic", 1);
        sampleGame.setId(50L);
        sampleGame.setStatus(Game.GameStatus.IN_PROGRESS);
    }

    @Test
    @DisplayName("Start game - Success flow")
    void testStartGame_Success() {
        GameStartRequest request = new GameStartRequest();
        request.setDifficulty("Medium");
        request.setCategoryName("Logic");
        request.setTotalQuestions(1);

        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(categoryRepository.findByName("Logic")).thenReturn(Optional.of(sampleCategory));
        when(riddleRepository.findByCategoryId(10L)).thenReturn(List.of(sampleRiddle));
        when(gameRepository.save(any(Game.class))).thenReturn(sampleGame);
        when(aiRiddleEngine.selectNextRiddle(any(), any(), any())).thenReturn(sampleRiddle);

        Game game = gameService.startGame(1L, request);

        assertNotNull(game);
        assertEquals("Medium", game.getDifficulty());
        verify(gameQuestionRepository, times(1)).saveAll(any());
    }

    @Test
    @DisplayName("Get question - Returns DTO with options")
    void testGetQuestion_Success() {
        GameQuestion gq = new GameQuestion(sampleGame, sampleRiddle, 1);

        when(gameRepository.findById(50L)).thenReturn(Optional.of(sampleGame));
        when(gameQuestionRepository.findByGameIdAndQuestionOrder(50L, 1)).thenReturn(Optional.of(gq));

        QuestionDto questionDto = gameService.getQuestion(50L, 1L);

        assertNotNull(questionDto);
        assertEquals("What has to be broken before you can use it?", questionDto.getQuestion());
        assertEquals("Logic", questionDto.getCategory());
    }

    @Test
    @DisplayName("Submit answer - Correct answer with speed bonus & finishes game")
    void testSubmitAnswer_CorrectAndCompletesGame() {
        GameQuestion gq = new GameQuestion(sampleGame, sampleRiddle, 1);
        sampleGame.setQuestions(List.of(gq));

        SubmitAnswerRequest request = new SubmitAnswerRequest();
        request.setAnswer("An egg");
        request.setResponseTimeSeconds(5); // Speed bonus triggered!

        when(gameRepository.findById(50L)).thenReturn(Optional.of(sampleGame));
        when(gameQuestionRepository.findByGameIdAndQuestionOrder(50L, 1)).thenReturn(Optional.of(gq));
        when(aiRiddleEngine.evaluateAnswer(sampleRiddle, "An egg"))
                .thenReturn(new AIRiddleEngine.AnswerEvaluationResult(true, "An egg", "Spot on!"));
        when(gameQuestionRepository.findByGameIdOrderByQuestionOrderAsc(50L)).thenReturn(List.of(gq));
        when(scoreRepository.findByUserId(1L)).thenReturn(Optional.empty());

        AnswerResultDto result = gameService.submitAnswer(50L, 1L, request);

        assertTrue(result.getIsCorrect());
        assertTrue(result.getIsGameOver());
        // Base 100 * 1.5 (Medium) * 1.2 (Speed bonus <= 10s) = 180 pts
        assertEquals(180, result.getPointsAwarded());
        verify(scoreRepository, times(1)).save(any());
    }
}
