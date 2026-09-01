package com.riddle.airiddlegame.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "riddles")
public class Riddle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String question;

    @Column(name = "options_json", columnDefinition = "TEXT")
    private String optionsJson; // JSON array string e.g. ["Option A", "Option B", "Option C", "Option D"]

    @Column(name = "correct_answer", nullable = false)
    private String correctAnswer;

    @Column(name = "alt_answers_json", columnDefinition = "TEXT")
    private String altAnswersJson; // JSON array of valid keyword/synonym variations e.g. ["clock", "wall clock"]

    @Column(columnDefinition = "TEXT")
    private String hint;

    @Column(nullable = false, length = 20)
    private String difficulty; // Easy, Medium, Hard

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "base_points", nullable = false)
    private Integer basePoints = 100;

    public Riddle() {
    }

    public Riddle(String question, String optionsJson, String correctAnswer, String altAnswersJson, String hint, String difficulty, Category category, Integer basePoints) {
        this.question = question;
        this.optionsJson = optionsJson;
        this.correctAnswer = correctAnswer;
        this.altAnswersJson = altAnswersJson;
        this.hint = hint;
        this.difficulty = difficulty;
        this.category = category;
        this.basePoints = basePoints != null ? basePoints : 100;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getOptionsJson() {
        return optionsJson;
    }

    public void setOptionsJson(String optionsJson) {
        this.optionsJson = optionsJson;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public String getAltAnswersJson() {
        return altAnswersJson;
    }

    public void setAltAnswersJson(String altAnswersJson) {
        this.altAnswersJson = altAnswersJson;
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

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Integer getBasePoints() {
        return basePoints;
    }

    public void setBasePoints(Integer basePoints) {
        this.basePoints = basePoints;
    }
}
