package com.riddle.airiddlegame.dto.response;

public class AnswerResultDto {
    private Boolean isCorrect;
    private String correctAnswer;
    private Integer pointsAwarded;
    private Integer totalScore;
    private String explanation;
    private Boolean isGameOver;
    private Integer nextQuestionOrder;

    public AnswerResultDto() {
    }

    public Boolean getIsCorrect() {
        return isCorrect;
    }

    public void setIsCorrect(Boolean isCorrect) {
        this.isCorrect = isCorrect;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public Integer getPointsAwarded() {
        return pointsAwarded;
    }

    public void setPointsAwarded(Integer pointsAwarded) {
        this.pointsAwarded = pointsAwarded;
    }

    public Integer getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(Integer totalScore) {
        this.totalScore = totalScore;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public Boolean getIsGameOver() {
        return isGameOver;
    }

    public void setIsGameOver(Boolean isGameOver) {
        this.isGameOver = isGameOver;
    }

    public Integer getNextQuestionOrder() {
        return nextQuestionOrder;
    }

    public void setNextQuestionOrder(Integer nextQuestionOrder) {
        this.nextQuestionOrder = nextQuestionOrder;
    }
}
