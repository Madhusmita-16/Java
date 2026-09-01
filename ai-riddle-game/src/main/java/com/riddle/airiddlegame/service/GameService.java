package com.riddle.airiddlegame.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.riddle.airiddlegame.ai.AIRiddleEngine;
import com.riddle.airiddlegame.dto.request.GameStartRequest;
import com.riddle.airiddlegame.dto.request.SubmitAnswerRequest;
import com.riddle.airiddlegame.dto.response.AnswerResultDto;
import com.riddle.airiddlegame.dto.response.GameResultDto;
import com.riddle.airiddlegame.dto.response.QuestionDto;
import com.riddle.airiddlegame.entity.*;
import com.riddle.airiddlegame.exception.InvalidGameStateException;
import com.riddle.airiddlegame.exception.ResourceNotFoundException;
import com.riddle.airiddlegame.exception.UnauthorizedAccessException;
import com.riddle.airiddlegame.repository.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GameService {

    private final GameRepository gameRepository;
    private final GameQuestionRepository gameQuestionRepository;
    private final RiddleRepository riddleRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ScoreRepository scoreRepository;
    private final AIRiddleEngine aiRiddleEngine;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public GameService(GameRepository gameRepository,
                       GameQuestionRepository gameQuestionRepository,
                       RiddleRepository riddleRepository,
                       UserRepository userRepository,
                       CategoryRepository categoryRepository,
                       ScoreRepository scoreRepository,
                       @Qualifier("simulatedAIRiddleEngine") AIRiddleEngine aiRiddleEngine) {
        this.gameRepository = gameRepository;
        this.gameQuestionRepository = gameQuestionRepository;
        this.riddleRepository = riddleRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.scoreRepository = scoreRepository;
        this.aiRiddleEngine = aiRiddleEngine;
    }

    @Transactional
    public Game startGame(Long userId, GameStartRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Fetch candidates by category if specified
        List<Riddle> candidates;
        if (request.getCategoryName() != null && !"All".equalsIgnoreCase(request.getCategoryName())) {
            Category category = categoryRepository.findByName(request.getCategoryName())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + request.getCategoryName()));
            candidates = riddleRepository.findByCategoryId(category.getId());
        } else {
            candidates = riddleRepository.findAll();
        }

        if (candidates.isEmpty()) {
            throw new InvalidGameStateException("No riddles available for the selected criteria.");
        }

        int targetQuestions = request.getTotalQuestions() != null ? request.getTotalQuestions() : 5;
        Game game = new Game(user, request.getDifficulty(), request.getCategoryName(), targetQuestions);
        Game savedGame = gameRepository.save(game);

        // Select questions using AI Engine
        List<Long> usedIds = new ArrayList<>();
        List<GameQuestion> gameQuestions = new ArrayList<>();

        for (int i = 1; i <= targetQuestions; i++) {
            Riddle selected = aiRiddleEngine.selectNextRiddle(savedGame, candidates, usedIds);
            if (selected == null) break;

            usedIds.add(selected.getId());
            GameQuestion gq = new GameQuestion(savedGame, selected, i);
            gameQuestions.add(gq);
        }

        if (gameQuestions.isEmpty()) {
            throw new InvalidGameStateException("Failed to generate riddle questions for the game.");
        }

        gameQuestionRepository.saveAll(gameQuestions);
        savedGame.setQuestions(gameQuestions);
        savedGame.setTotalQuestions(gameQuestions.size());

        return gameRepository.save(savedGame);
    }

    public QuestionDto getQuestion(Long gameId, Long userId) {
        Game game = getGameAndValidateUser(gameId, userId);

        if (game.getStatus() != Game.GameStatus.IN_PROGRESS) {
            throw new InvalidGameStateException("Game is already " + game.getStatus());
        }

        int currentOrder = game.getCurrentQuestionIndex() + 1;
        GameQuestion gq = gameQuestionRepository.findByGameIdAndQuestionOrder(gameId, currentOrder)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found for order: " + currentOrder));

        Riddle r = gq.getRiddle();

        QuestionDto dto = new QuestionDto();
        dto.setGameQuestionId(gq.getId());
        dto.setQuestionOrder(gq.getQuestionOrder());
        dto.setTotalQuestions(game.getTotalQuestions());
        dto.setRiddleId(r.getId());
        dto.setQuestion(r.getQuestion());
        dto.setOptions(parseJsonArray(r.getOptionsJson()));
        dto.setCategory(r.getCategory().getName());
        dto.setDifficulty(r.getDifficulty());
        dto.setBasePoints(r.getBasePoints());
        dto.setHintUsed(gq.getHintUsed());
        if (Boolean.TRUE.equals(gq.getHintUsed())) {
            dto.setHintText(aiRiddleEngine.generateHint(r));
        }

        return dto;
    }

    @Transactional
    public String requestHint(Long gameId, Long userId) {
        Game game = getGameAndValidateUser(gameId, userId);
        if (game.getStatus() != Game.GameStatus.IN_PROGRESS) {
            throw new InvalidGameStateException("Game is not in progress.");
        }

        int currentOrder = game.getCurrentQuestionIndex() + 1;
        GameQuestion gq = gameQuestionRepository.findByGameIdAndQuestionOrder(gameId, currentOrder)
                .orElseThrow(() -> new ResourceNotFoundException("Active question not found."));

        gq.setHintUsed(true);
        gameQuestionRepository.save(gq);

        return aiRiddleEngine.generateHint(gq.getRiddle());
    }

    @Transactional
    public AnswerResultDto submitAnswer(Long gameId, Long userId, SubmitAnswerRequest request) {
        Game game = getGameAndValidateUser(gameId, userId);
        if (game.getStatus() != Game.GameStatus.IN_PROGRESS) {
            throw new InvalidGameStateException("Game is already finished.");
        }

        int currentOrder = game.getCurrentQuestionIndex() + 1;
        GameQuestion gq = gameQuestionRepository.findByGameIdAndQuestionOrder(gameId, currentOrder)
                .orElseThrow(() -> new ResourceNotFoundException("Active question not found."));

        Riddle riddle = gq.getRiddle();
        AIRiddleEngine.AnswerEvaluationResult evalResult = aiRiddleEngine.evaluateAnswer(riddle, request.getAnswer());

        int basePoints = riddle.getBasePoints() != null ? riddle.getBasePoints() : 100;
        // Difficulty multiplier
        double diffMultiplier = 1.0;
        if ("Medium".equalsIgnoreCase(game.getDifficulty())) diffMultiplier = 1.5;
        else if ("Hard".equalsIgnoreCase(game.getDifficulty())) diffMultiplier = 2.0;

        int calculatedPoints = 0;
        if (evalResult.isCorrect()) {
            double pts = basePoints * diffMultiplier;
            // Hint penalty: -50% points
            if (Boolean.TRUE.equals(gq.getHintUsed())) {
                pts = pts * 0.5;
            }
            // Speed bonus: if answered within 10 seconds -> +20% bonus
            if (request.getResponseTimeSeconds() != null && request.getResponseTimeSeconds() <= 10) {
                pts = pts * 1.2;
            }
            calculatedPoints = (int) Math.round(pts);
        }

        gq.setUserAnswer(request.getAnswer());
        gq.setIsCorrect(evalResult.isCorrect());
        gq.setPointsAwarded(calculatedPoints);
        gq.setResponseTimeSeconds(request.getResponseTimeSeconds());
        gameQuestionRepository.save(gq);

        // Update game state
        int newTotalScore = game.getTotalScore() + calculatedPoints;
        game.setTotalScore(newTotalScore);
        int newIndex = game.getCurrentQuestionIndex() + 1;
        game.setCurrentQuestionIndex(newIndex);

        boolean isGameOver = newIndex >= game.getTotalQuestions();

        if (isGameOver) {
            game.setStatus(Game.GameStatus.COMPLETED);
            game.setFinishedAt(LocalDateTime.now());

            // Calculate accuracy
            List<GameQuestion> allQuestions = gameQuestionRepository.findByGameIdOrderByQuestionOrderAsc(gameId);
            long correctCount = allQuestions.stream().filter(q -> Boolean.TRUE.equals(q.getIsCorrect())).count();
            double accuracy = ((double) correctCount / allQuestions.size()) * 100.0;
            game.setAccuracy(Math.round(accuracy * 10.0) / 10.0);

            // Update user global leaderboard scores
            updateUserGlobalScore(game.getUser(), newTotalScore, accuracy);
        }

        gameRepository.save(game);

        AnswerResultDto resultDto = new AnswerResultDto();
        resultDto.setIsCorrect(evalResult.isCorrect());
        resultDto.setCorrectAnswer(riddle.getCorrectAnswer());
        resultDto.setPointsAwarded(calculatedPoints);
        resultDto.setTotalScore(newTotalScore);
        resultDto.setExplanation(evalResult.getFeedback());
        resultDto.setIsGameOver(isGameOver);
        resultDto.setNextQuestionOrder(isGameOver ? null : newIndex + 1);

        return resultDto;
    }

    public GameResultDto getGameResult(Long gameId, Long userId) {
        Game game = getGameAndValidateUser(gameId, userId);
        List<GameQuestion> questions = gameQuestionRepository.findByGameIdOrderByQuestionOrderAsc(gameId);

        long correctCount = questions.stream().filter(q -> Boolean.TRUE.equals(q.getIsCorrect())).count();
        double accuracy = questions.isEmpty() ? 0.0 : ((double) correctCount / questions.size()) * 100.0;
        accuracy = Math.round(accuracy * 10.0) / 10.0;

        GameResultDto result = new GameResultDto();
        result.setGameId(game.getId());
        result.setUsername(game.getUser().getUsername());
        result.setDifficulty(game.getDifficulty());
        result.setCategoryName(game.getCategoryName());
        result.setTotalScore(game.getTotalScore());
        result.setTotalQuestions(questions.size());
        result.setCorrectCount((int) correctCount);
        result.setAccuracy(accuracy);
        result.setPerformanceRank(calculatePerformanceRank(accuracy, game.getTotalScore()));
        result.setStartedAt(game.getStartedAt());
        result.setFinishedAt(game.getFinishedAt());

        List<GameResultDto.QuestionReviewDto> reviews = questions.stream().map(gq -> {
            GameResultDto.QuestionReviewDto qDto = new GameResultDto.QuestionReviewDto();
            qDto.setQuestionOrder(gq.getQuestionOrder());
            qDto.setQuestion(gq.getRiddle().getQuestion());
            qDto.setUserAnswer(gq.getUserAnswer());
            qDto.setCorrectAnswer(gq.getRiddle().getCorrectAnswer());
            qDto.setIsCorrect(gq.getIsCorrect());
            qDto.setHintUsed(gq.getHintUsed());
            qDto.setPointsAwarded(gq.getPointsAwarded());
            qDto.setResponseTimeSeconds(gq.getResponseTimeSeconds());
            return qDto;
        }).collect(Collectors.toList());

        result.setQuestionReviews(reviews);
        return result;
    }

    private Game getGameAndValidateUser(Long gameId, Long userId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new ResourceNotFoundException("Game not found with id: " + gameId));
        if (!game.getUser().getId().equals(userId)) {
            throw new UnauthorizedAccessException("You are not authorized to access this game.");
        }
        return game;
    }

    private void updateUserGlobalScore(User user, int scoreAchieved, double accuracyAchieved) {
        Score userScore = scoreRepository.findByUserId(user.getId())
                .orElseGet(() -> new Score(user));

        if (scoreAchieved > userScore.getHighestScore()) {
            userScore.setHighestScore(scoreAchieved);
        }

        int totalGames = userScore.getTotalGamesPlayed() + 1;
        userScore.setTotalGamesPlayed(totalGames);

        // Exponential moving average for accuracy
        double newAvgAccuracy = ((userScore.getAverageAccuracy() * (totalGames - 1)) + accuracyAchieved) / totalGames;
        userScore.setAverageAccuracy(Math.round(newAvgAccuracy * 10.0) / 10.0);

        scoreRepository.save(userScore);
    }

    private String calculatePerformanceRank(double accuracy, int totalScore) {
        if (accuracy >= 90 && totalScore >= 700) return "👑 Riddle Grandmaster";
        if (accuracy >= 80) return "⚡ Master Logician";
        if (accuracy >= 60) return "🧠 Sharp Mind";
        if (accuracy >= 40) return "🔍 Keen Solver";
        return "🌱 Novice Explorer";
    }

    private List<String> parseJsonArray(String json) {
        if (json == null || json.trim().isEmpty()) return Collections.emptyList();
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }
}
