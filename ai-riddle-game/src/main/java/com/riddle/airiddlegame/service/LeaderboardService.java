package com.riddle.airiddlegame.service;

import com.riddle.airiddlegame.dto.response.LeaderboardDto;
import com.riddle.airiddlegame.entity.Score;
import com.riddle.airiddlegame.repository.ScoreRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LeaderboardService {

    private final ScoreRepository scoreRepository;

    public LeaderboardService(ScoreRepository scoreRepository) {
        this.scoreRepository = scoreRepository;
    }

    public List<LeaderboardDto> getTopLeaderboard() {
        List<Score> topScores = scoreRepository.findTopLeaderboard();
        List<LeaderboardDto> leaderboard = new ArrayList<>();

        int rank = 1;
        for (Score score : topScores) {
            leaderboard.add(new LeaderboardDto(
                    rank++,
                    score.getUser().getId(),
                    score.getUser().getUsername(),
                    score.getHighestScore(),
                    score.getTotalGamesPlayed(),
                    score.getAverageAccuracy()
            ));
        }
        return leaderboard;
    }
}
