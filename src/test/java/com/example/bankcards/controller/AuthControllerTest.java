package com.example.bankcards.controller;

import com.example.bankcards.dto.JwtResponse;
import com.example.bankcards.dto.LoginRequest;
import com.example.bankcards.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @Test
    void login_shouldReturnOkWithToken() {
        LoginRequest loginRequest = mock(LoginRequest.class);
        JwtResponse jwtResponse = mock(JwtResponse.class);

        when(authService.login(loginRequest)).thenReturn(jwtResponse);

        ResponseEntity<JwtResponse> responseEntity =
                authController.login(loginRequest);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertSame(jwtResponse, responseEntity.getBody());
        verify(authService).login(loginRequest);
    }

    @Test
    void login_whenAuthFails_shouldThrow() {
        LoginRequest loginRequest = mock(LoginRequest.class);

        when(authService.login(loginRequest))
                .thenThrow(new RuntimeException("Invalid credentials"));

        assertThrows(RuntimeException.class,
                () -> authController.login(loginRequest));
    }
}
