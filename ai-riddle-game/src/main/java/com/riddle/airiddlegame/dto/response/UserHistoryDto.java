package com.riddle.airiddlegame.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public class UserHistoryDto {
    private Long userId;
    private String username;
    private Integer highestScore;
    private Integer totalGamesPlayed;
    private Double averageAccuracy;
    private List<GameHistoryItemDto> games;

    public static class GameHistoryItemDto {
        private Long gameId;
        private String difficulty;
        private String categoryName;
        private String status;
        private Integer score;
        private Double accuracy;
        private LocalDateTime startedAt;

        public GameHistoryItemDto() {
        }

        public Long getGameId() {
            return gameId;
        }

        public void setGameId(Long gameId) {
            this.gameId = gameId;
        }

        public String getDifficulty() {
            return difficulty;
        }

        public void setDifficulty(String difficulty) {
            this.difficulty = difficulty;
        }

        public String getCategoryName() {
            return categoryName;
        }

        public void setCategoryName(String categoryName) {
            this.categoryName = categoryName;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Integer getScore() {
            return score;
        }

        public void setScore(Integer score) {
            this.score = score;
        }

        public Double getAccuracy() {
            return accuracy;
        }

        public void setAccuracy(Double accuracy) {
            this.accuracy = accuracy;
        }

        public LocalDateTime getStartedAt() {
            return startedAt;
        }

        public void setStartedAt(LocalDateTime startedAt) {
            this.startedAt = startedAt;
        }
    }

    public UserHistoryDto() {
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getHighestScore() {
        return highestScore;
    }

    public void setHighestScore(Integer highestScore) {
        this.highestScore = highestScore;
    }

    public Integer getTotalGamesPlayed() {
        return totalGamesPlayed;
    }

    public void setTotalGamesPlayed(Integer totalGamesPlayed) {
        this.totalGamesPlayed = totalGamesPlayed;
    }

    public Double getAverageAccuracy() {
        return averageAccuracy;
    }

    public void setAverageAccuracy(Double averageAccuracy) {
        this.averageAccuracy = averageAccuracy;
    }

    public List<GameHistoryItemDto> getGames() {
        return games;
    }

    public void setGames(List<GameHistoryItemDto> games) {
        this.games = games;
    }
}
