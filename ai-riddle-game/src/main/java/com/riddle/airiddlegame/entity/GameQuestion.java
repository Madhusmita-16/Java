package com.riddle.airiddlegame.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "game_questions")
public class GameQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "riddle_id", nullable = false)
    private Riddle riddle;

    @Column(name = "user_answer")
    private String userAnswer;

    @Column(name = "is_correct")
    private Boolean isCorrect;

    @Column(name = "hint_used", nullable = false)
    private Boolean hintUsed = false;

    @Column(name = "points_awarded", nullable = false)
    private Integer pointsAwarded = 0;

    @Column(name = "response_time_seconds")
    private Integer responseTimeSeconds;

    @Column(name = "question_order", nullable = false)
    private Integer questionOrder;

    public GameQuestion() {
    }

    public GameQuestion(Game game, Riddle riddle, Integer questionOrder) {
        this.game = game;
        this.riddle = riddle;
        this.questionOrder = questionOrder;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Riddle getRiddle() {
        return riddle;
    }

    public void setRiddle(Riddle riddle) {
        this.riddle = riddle;
    }

    public String getUserAnswer() {
        return userAnswer;
    }

    public void setUserAnswer(String userAnswer) {
        this.userAnswer = userAnswer;
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

    public Integer getQuestionOrder() {
        return questionOrder;
    }

    public void setQuestionOrder(Integer questionOrder) {
        this.questionOrder = questionOrder;
    }
}
