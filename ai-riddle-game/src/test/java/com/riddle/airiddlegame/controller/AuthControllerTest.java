package com.riddle.airiddlegame.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.riddle.airiddlegame.dto.request.LoginRequest;
import com.riddle.airiddlegame.dto.request.RegisterRequest;
import com.riddle.airiddlegame.dto.response.AuthResponse;
import com.riddle.airiddlegame.security.CustomUserDetailsService;
import com.riddle.airiddlegame.security.JwtTokenProvider;
import com.riddle.airiddlegame.service.AuthService;
import com.riddle.airiddlegame.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtTokenProvider tokenProvider;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("POST /api/auth/register - Success returns token")
    void testRegisterUser() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newplayer");
        request.setEmail("new@riddle.com");
        request.setPassword("password123");

        AuthResponse authResponse = new AuthResponse("jwt-token-123", 1L, "newplayer", "new@riddle.com", "ROLE_USER");

        when(authService.register(any(RegisterRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").value("jwt-token-123"))
                .andExpect(jsonPath("$.data.username").value("newplayer"));
    }

    @Test
    @DisplayName("POST /api/auth/login - Success returns JWT token")
    void testLoginUser() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsernameOrEmail("newplayer");
        request.setPassword("password123");

        AuthResponse authResponse = new AuthResponse("jwt-token-456", 1L, "newplayer", "new@riddle.com", "ROLE_USER");

        when(authService.login(any(LoginRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").value("jwt-token-456"));
    }
}
