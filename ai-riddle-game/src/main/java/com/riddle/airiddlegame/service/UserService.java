package com.riddle.airiddlegame.service;

import com.riddle.airiddlegame.dto.response.UserHistoryDto;
import com.riddle.airiddlegame.entity.Game;
import com.riddle.airiddlegame.entity.Score;
import com.riddle.airiddlegame.entity.User;
import com.riddle.airiddlegame.exception.ResourceNotFoundException;
import com.riddle.airiddlegame.repository.GameRepository;
import com.riddle.airiddlegame.repository.ScoreRepository;
import com.riddle.airiddlegame.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final GameRepository gameRepository;
    private final ScoreRepository scoreRepository;

    public UserService(UserRepository userRepository, GameRepository gameRepository, ScoreRepository scoreRepository) {
        this.userRepository = userRepository;
        this.gameRepository = gameRepository;
        this.scoreRepository = scoreRepository;
    }

    public UserHistoryDto getUserHistory(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Score score = scoreRepository.findByUserId(userId)
                .orElseGet(() -> new Score(user));

        List<Game> games = gameRepository.findByUserIdOrderByStartedAtDesc(userId);

        UserHistoryDto historyDto = new UserHistoryDto();
        historyDto.setUserId(user.getId());
        historyDto.setUsername(user.getUsername());
        historyDto.setHighestScore(score.getHighestScore());
        historyDto.setTotalGamesPlayed(score.getTotalGamesPlayed());
        historyDto.setAverageAccuracy(score.getAverageAccuracy());

        List<UserHistoryDto.GameHistoryItemDto> gameItems = games.stream().map(g -> {
            UserHistoryDto.GameHistoryItemDto item = new UserHistoryDto.GameHistoryItemDto();
            item.setGameId(g.getId());
            item.setDifficulty(g.getDifficulty());
            item.setCategoryName(g.getCategoryName());
            item.setStatus(g.getStatus().name());
            item.setScore(g.getTotalScore());
            item.setAccuracy(g.getAccuracy());
            item.setStartedAt(g.getStartedAt());
            return item;
        }).collect(Collectors.toList());

        historyDto.setGames(gameItems);
        return historyDto;
    }
}
