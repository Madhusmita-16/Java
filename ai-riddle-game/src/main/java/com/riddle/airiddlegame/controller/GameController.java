package com.riddle.airiddlegame.controller;

import com.riddle.airiddlegame.dto.request.GameStartRequest;
import com.riddle.airiddlegame.dto.request.SubmitAnswerRequest;
import com.riddle.airiddlegame.dto.response.*;
import com.riddle.airiddlegame.entity.Game;
import com.riddle.airiddlegame.security.UserPrincipal;
import com.riddle.airiddlegame.service.GameService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/game")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping("/start")
    public ResponseEntity<ApiResponse<Map<String, Object>>> startGame(@RequestBody(required = false) GameStartRequest request,
                                                                      @AuthenticationPrincipal UserPrincipal currentUser) {
        if (request == null) {
            request = new GameStartRequest();
        }
        Game game = gameService.startGame(currentUser.getId(), request);

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("gameId", game.getId());
        responseData.put("difficulty", game.getDifficulty());
        responseData.put("categoryName", game.getCategoryName());
        responseData.put("totalQuestions", game.getTotalQuestions());
        responseData.put("status", game.getStatus().name());

        return ResponseEntity.ok(ApiResponse.success("Game started successfully!", responseData));
    }

    @GetMapping("/{id}/question")
    public ResponseEntity<ApiResponse<QuestionDto>> getQuestion(@PathVariable("id") Long gameId,
                                                                 @AuthenticationPrincipal UserPrincipal currentUser) {
        QuestionDto question = gameService.getQuestion(gameId, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(question));
    }

    @GetMapping("/{id}/hint")
    public ResponseEntity<ApiResponse<Map<String, String>>> getHint(@PathVariable("id") Long gameId,
                                                                    @AuthenticationPrincipal UserPrincipal currentUser) {
        String hint = gameService.requestHint(gameId, currentUser.getId());
        Map<String, String> data = new HashMap<>();
        data.put("hint", hint);
        return ResponseEntity.ok(ApiResponse.success("Hint retrieved (- penalty applied to question score)", data));
    }

    @PostMapping("/{id}/answer")
    public ResponseEntity<ApiResponse<AnswerResultDto>> submitAnswer(@PathVariable("id") Long gameId,
                                                                     @RequestBody SubmitAnswerRequest request,
                                                                     @AuthenticationPrincipal UserPrincipal currentUser) {
        AnswerResultDto result = gameService.submitAnswer(gameId, currentUser.getId(), request);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/{id}/result")
    public ResponseEntity<ApiResponse<GameResultDto>> getResult(@PathVariable("id") Long gameId,
                                                                 @AuthenticationPrincipal UserPrincipal currentUser) {
        GameResultDto result = gameService.getGameResult(gameId, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(result));
    }
}
