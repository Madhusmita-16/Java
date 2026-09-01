package com.riddle.airiddlegame.controller;

import com.riddle.airiddlegame.dto.response.ApiResponse;
import com.riddle.airiddlegame.dto.response.RiddleDto;
import com.riddle.airiddlegame.service.RiddleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/riddles")
public class RiddleController {

    private final RiddleService riddleService;

    public RiddleController(RiddleService riddleService) {
        this.riddleService = riddleService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<RiddleDto>>> getAllRiddles(
            @RequestParam(name = "difficulty", required = false) String difficulty,
            @RequestParam(name = "categoryId", required = false) Long categoryId) {
        List<RiddleDto> riddles = riddleService.getAllRiddles(difficulty, categoryId);
        return ResponseEntity.ok(ApiResponse.success(riddles));
    }

    @GetMapping("/public")
    public ResponseEntity<ApiResponse<List<RiddleDto>>> getPublicRiddles() {
        List<RiddleDto> riddles = riddleService.getAllRiddles(null, null);
        return ResponseEntity.ok(ApiResponse.success(riddles));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RiddleDto>> getRiddleById(@PathVariable("id") Long id) {
        RiddleDto riddle = riddleService.getRiddleById(id);
        return ResponseEntity.ok(ApiResponse.success(riddle));
    }
}
