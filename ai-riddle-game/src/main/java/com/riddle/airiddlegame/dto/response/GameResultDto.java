package com.riddle.airiddlegame.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public class GameResultDto {
    private Long gameId;
    private String username;
    private String difficulty;
    private String categoryName;
    private Integer totalScore;
    private Integer totalQuestions;
    private Integer correctCount;
    private Double accuracy;
    private String performanceRank; // e.g. "Grand Master", "Riddle Master", "Novice"
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
    private List<QuestionReviewDto> questionReviews;

    public static class QuestionReviewDto {
        private Integer questionOrder;
        private String question;
        private String userAnswer;
        private String correctAnswer;
        private Boolean isCorrect;
        private Boolean hintUsed;
        private Integer pointsAwarded;
        private Integer responseTimeSeconds;

        public QuestionReviewDto() {
        }

        public Integer getQuestionOrder() {
            return questionOrder;
        }

        public void setQuestionOrder(Integer questionOrder) {
            this.questionOrder = questionOrder;
        }

        public String getQuestion() {
            return question;
        }

        public void setQuestion(String question) {
            this.question = question;
        }

        public String getUserAnswer() {
            return userAnswer;
        }

        public void setUserAnswer(String userAnswer) {
            this.userAnswer = userAnswer;
        }

        public String getCorrectAnswer() {
            return correctAnswer;
        }

        public void setCorrectAnswer(String correctAnswer) {
            this.correctAnswer = correctAnswer;
        }

        public Boolean getIsCorrect() {
            return isCorrect;
        }

        public void setIsCorrect(Boolean isCorrect) {
            this.isCorrect = isCorrect;
        }

        public Boolean getHintUsed() {
            return hintUsed;
        }

        public void setHintUsed(Boolean hintUsed) {
            this.hintUsed = hintUsed;
        }

        public Integer getPointsAwarded() {
            return pointsAwarded;
        }

        public void setPointsAwarded(Integer pointsAwarded) {
            this.pointsAwarded = pointsAwarded;
        }

        public Integer getResponseTimeSeconds() {
            return responseTimeSeconds;
        }

        public void setResponseTimeSeconds(Integer responseTimeSeconds) {
            this.responseTimeSeconds = responseTimeSeconds;
        }
    }

    public GameResultDto() {
    }

    // Getters and Setters
    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public Integer getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(Integer totalScore) {
        this.totalScore = totalScore;
    }

    public Integer getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(Integer totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public Integer getCorrectCount() {
        return correctCount;
    }

    public void setCorrectCount(Integer correctCount) {
        this.correctCount = correctCount;
    }

    public Double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(Double accuracy) {
        this.accuracy = accuracy;
    }

    public String getPerformanceRank() {
        return performanceRank;
    }

    public void setPerformanceRank(String performanceRank) {
        this.performanceRank = performanceRank;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(LocalDateTime finishedAt) {
        this.finishedAt = finishedAt;
    }

    public List<QuestionReviewDto> getQuestionReviews() {
        return questionReviews;
    }

    public void setQuestionReviews(List<QuestionReviewDto> questionReviews) {
        this.questionReviews = questionReviews;
    }
}
