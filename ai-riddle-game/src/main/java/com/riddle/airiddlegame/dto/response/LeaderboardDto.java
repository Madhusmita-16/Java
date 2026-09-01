package com.riddle.airiddlegame.dto.response;

public class LeaderboardDto {
    private Integer rank;
    private Long userId;
    private String username;
    private Integer highestScore;
    private Integer totalGamesPlayed;
    private Double averageAccuracy;

    public LeaderboardDto() {
    }

    public LeaderboardDto(Integer rank, Long userId, String username, Integer highestScore, Integer totalGamesPlayed, Double averageAccuracy) {
        this.rank = rank;
        this.userId = userId;
        this.username = username;
        this.highestScore = highestScore;
        this.totalGamesPlayed = totalGamesPlayed;
        this.averageAccuracy = averageAccuracy;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
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
}
