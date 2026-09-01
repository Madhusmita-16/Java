package com.riddle.airiddlegame.dto.response;

import java.util.List;

public class QuestionDto {
    private Long gameQuestionId;
    private Integer questionOrder;
    private Integer totalQuestions;
    private Long riddleId;
    private String question;
    private List<String> options;
    private String category;
    private String difficulty;
    private Integer basePoints;
    private Boolean hintUsed;
    private String hintText; // populated only if hint was requested
    private Integer timerSeconds = 30;

    public QuestionDto() {
    }

    public Long getGameQuestionId() {
        return gameQuestionId;
    }

    public void setGameQuestionId(Long gameQuestionId) {
        this.gameQuestionId = gameQuestionId;
    }

    public Integer getQuestionOrder() {
        return questionOrder;
    }

    public void setQuestionOrder(Integer questionOrder) {
        this.questionOrder = questionOrder;
    }

    public Integer getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(Integer totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public Long getRiddleId() {
        return riddleId;
    }

    public void setRiddleId(Long riddleId) {
        this.riddleId = riddleId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public Integer getBasePoints() {
        return basePoints;
    }

    public void setBasePoints(Integer basePoints) {
        this.basePoints = basePoints;
    }

    public Boolean getHintUsed() {
        return hintUsed;
    }

    public void setHintUsed(Boolean hintUsed) {
        this.hintUsed = hintUsed;
    }

    public String getHintText() {
        return hintText;
    }

    public void setHintText(String hintText) {
        this.hintText = hintText;
    }

    public Integer getTimerSeconds() {
        return timerSeconds;
    }

    public void setTimerSeconds(Integer timerSeconds) {
        this.timerSeconds = timerSeconds;
    }
}
