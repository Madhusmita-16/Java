package com.riddle.airiddlegame.service;

import com.riddle.airiddlegame.dto.request.LoginRequest;
import com.riddle.airiddlegame.dto.request.RegisterRequest;
import com.riddle.airiddlegame.dto.response.AuthResponse;
import com.riddle.airiddlegame.entity.Role;
import com.riddle.airiddlegame.entity.User;
import com.riddle.airiddlegame.repository.ScoreRepository;
import com.riddle.airiddlegame.repository.UserRepository;
import com.riddle.airiddlegame.security.JwtTokenProvider;
import com.riddle.airiddlegame.security.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ScoreRepository scoreRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthService authService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = new User("testplayer", "test@riddle.com", "encodedPassword", Role.ROLE_USER);
        sampleUser.setId(10L);
    }

    @Test
    @DisplayName("Register user - Success flow")
    void testRegister_Success() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testplayer");
        request.setEmail("test@riddle.com");
        request.setPassword("password123");

        when(userRepository.existsByUsername("testplayer")).thenReturn(false);
        when(userRepository.existsByEmail("test@riddle.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(tokenProvider.generateToken(authentication)).thenReturn("mocked-jwt-token");

        AuthResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals("mocked-jwt-token", response.getAccessToken());
        assertEquals("testplayer", response.getUsername());
        assertEquals("ROLE_USER", response.getRole());
        verify(scoreRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Register user - Duplicate username throws exception")
    void testRegister_DuplicateUsername() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testplayer");
        request.setEmail("test@riddle.com");
        request.setPassword("password123");

        when(userRepository.existsByUsername("testplayer")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Login user - Success flow")
    void testLogin_Success() {
        LoginRequest request = new LoginRequest();
        request.setUsernameOrEmail("testplayer");
        request.setPassword("password123");

        UserPrincipal principal = UserPrincipal.create(sampleUser);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(principal);
        when(tokenProvider.generateToken(authentication)).thenReturn("mocked-jwt-token");
        when(userRepository.findById(10L)).thenReturn(Optional.of(sampleUser));

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("mocked-jwt-token", response.getAccessToken());
        assertEquals(10L, response.getId());
        assertEquals("testplayer", response.getUsername());
    }
}
