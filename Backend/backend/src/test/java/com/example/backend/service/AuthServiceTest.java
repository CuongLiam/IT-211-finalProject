package com.example.backend.service;

import com.example.backend.dto.request.LoginRequest;
import com.example.backend.dto.request.RefreshTokenRequest;
import com.example.backend.dto.request.RegisterRequest;
import com.example.backend.dto.response.AuthResponse;
import com.example.backend.entity.User;
import com.example.backend.entity.enums.Role;
import com.example.backend.repository.PasswordResetTokenRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.security.CustomUserDetailsService;
import com.example.backend.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * FR-12: Unit tests cho AuthService (5 test cases).
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RedisTokenBlacklistService redisTokenBlacklistService;
    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private UserDetails mockUserDetails;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .fullName("Test Student")
                .email("student@example.com")
                .password("$2a$12$encodedPassword")
                .role(Role.STUDENT)
                .enabled(true)
                .build();

        mockUserDetails = org.springframework.security.core.userdetails.User
                .withUsername("student@example.com")
                .password("$2a$12$encodedPassword")
                .authorities("ROLE_STUDENT")
                .build();
    }

    // ── Test 1: Register thành công ──
    @Test
    @DisplayName("register - should create new student and return tokens")
    void register_success() {
        RegisterRequest request = new RegisterRequest();
        request.setFullName("New Student");
        request.setEmail("new@example.com");
        request.setPassword("password123");

        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("$2a$12$encoded");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(2L);
            return u;
        });
        when(customUserDetailsService.loadUserByUsername("new@example.com")).thenReturn(mockUserDetails);
        when(jwtUtil.generateAccessToken(mockUserDetails)).thenReturn("access-token");
        when(jwtUtil.generateRefreshToken(mockUserDetails)).thenReturn("refresh-token");

        AuthResponse result = authService.register(request);

        assertThat(result).isNotNull();
        assertThat(result.getAccessToken()).isEqualTo("access-token");
        assertThat(result.getRefreshToken()).isEqualTo("refresh-token");
        assertThat(result.getTokenType()).isEqualTo("Bearer");
        verify(userRepository).save(any(User.class));
    }

    // ── Test 2: Register với email đã tồn tại ──
    @Test
    @DisplayName("register - should throw CONFLICT when email already exists")
    void register_duplicateEmail_throwsConflict() {
        RegisterRequest request = new RegisterRequest();
        request.setFullName("Duplicate");
        request.setEmail("existing@example.com");
        request.setPassword("password123");

        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Email already exists");
    }

    // ── Test 3: Login thành công ──
    @Test
    @DisplayName("login - should authenticate and return tokens")
    void login_success() {
        LoginRequest request = new LoginRequest();
        request.setEmail("student@example.com");
        request.setPassword("password123");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userRepository.findByEmail("student@example.com")).thenReturn(Optional.of(testUser));
        when(customUserDetailsService.loadUserByUsername("student@example.com")).thenReturn(mockUserDetails);
        when(jwtUtil.generateAccessToken(mockUserDetails)).thenReturn("access-token");
        when(jwtUtil.generateRefreshToken(mockUserDetails)).thenReturn("refresh-token");

        AuthResponse result = authService.login(request);

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("student@example.com");
        assertThat(result.getRole()).isEqualTo("STUDENT");
    }

    // ── Test 4: Login sai mật khẩu ──
    @Test
    @DisplayName("login - should throw UNAUTHORIZED when credentials are wrong")
    void login_wrongPassword_throwsUnauthorized() {
        LoginRequest request = new LoginRequest();
        request.setEmail("student@example.com");
        request.setPassword("wrongpassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Email or password is incorrect");
    }

    // ── Test 5: RefreshToken với token đã bị revoke ──
    @Test
    @DisplayName("refreshToken - should throw UNAUTHORIZED when token is revoked")
    void refreshToken_revokedToken_throwsUnauthorized() {
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("revoked-refresh-token");

        when(redisTokenBlacklistService.isBlacklisted("revoked-refresh-token")).thenReturn(true);

        assertThatThrownBy(() -> authService.refreshToken(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Refresh token has been revoked");
    }
}
