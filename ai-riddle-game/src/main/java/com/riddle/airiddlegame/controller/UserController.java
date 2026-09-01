package com.riddle.airiddlegame.controller;

import com.riddle.airiddlegame.dto.response.ApiResponse;
import com.riddle.airiddlegame.dto.response.UserHistoryDto;
import com.riddle.airiddlegame.security.UserPrincipal;
import com.riddle.airiddlegame.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<ApiResponse<UserHistoryDto>> getUserHistoryById(@PathVariable("id") Long userId) {
        UserHistoryDto history = userService.getUserHistory(userId);
        return ResponseEntity.ok(ApiResponse.success(history));
    }

    @GetMapping("/me/history")
    public ResponseEntity<ApiResponse<UserHistoryDto>> getMyHistory(@AuthenticationPrincipal UserPrincipal currentUser) {
        UserHistoryDto history = userService.getUserHistory(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(history));
    }
}
