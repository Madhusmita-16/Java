package com.riddle.airiddlegame.dto.request;

public class GameStartRequest {

    private String difficulty = "Medium"; // Easy, Medium, Hard
    private String categoryName = "All";  // Category Name or "All"
    private Integer totalQuestions = 5;   // default 5

    public GameStartRequest() {
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

    public Integer getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(Integer totalQuestions) {
        this.totalQuestions = totalQuestions;
    }
}
