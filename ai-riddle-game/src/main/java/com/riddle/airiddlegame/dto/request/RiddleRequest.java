package com.riddle.airiddlegame.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class RiddleRequest {

    @NotBlank(message = "Question is required")
    private String question;

    private List<String> options; // optional for multiple choice

    @NotBlank(message = "Correct answer is required")
    private String correctAnswer;

    private List<String> altAnswers; // list of synonyms / keywords

    @NotBlank(message = "Hint is required")
    private String hint;

    @NotBlank(message = "Difficulty is required")
    private String difficulty; // Easy, Medium, Hard

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    private Integer basePoints = 100;

    public RiddleRequest() {
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

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public List<String> getAltAnswers() {
        return altAnswers;
    }

    public void setAltAnswers(List<String> altAnswers) {
        this.altAnswers = altAnswers;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getBasePoints() {
        return basePoints;
    }

    public void setBasePoints(Integer basePoints) {
        this.basePoints = basePoints;
    }
}
