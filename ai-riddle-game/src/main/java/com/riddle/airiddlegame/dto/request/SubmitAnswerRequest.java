package com.riddle.airiddlegame.dto.request;

public class SubmitAnswerRequest {

    private String answer;
    private Integer responseTimeSeconds;

    public SubmitAnswerRequest() {
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public Integer getResponseTimeSeconds() {
        return responseTimeSeconds;
    }

    public void setResponseTimeSeconds(Integer responseTimeSeconds) {
        this.responseTimeSeconds = responseTimeSeconds;
    }
}
