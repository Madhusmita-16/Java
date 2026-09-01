package com.riddle.airiddlegame.controller;

import com.riddle.airiddlegame.dto.response.ApiResponse;
import com.riddle.airiddlegame.dto.response.LeaderboardDto;
import com.riddle.airiddlegame.service.LeaderboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/leaderboard")
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    public LeaderboardController(LeaderboardService leaderboardService) {
        this.leaderboardService = leaderboardService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<LeaderboardDto>>> getLeaderboard() {
        List<LeaderboardDto> leaderboard = leaderboardService.getTopLeaderboard();
        return ResponseEntity.ok(ApiResponse.success("Leaderboard retrieved successfully", leaderboard));
    }
}
