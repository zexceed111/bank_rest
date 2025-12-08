package com.example.bankcards.controller;

import com.example.bankcards.dto.CardCreateRequest;
import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.dto.PageResponse;
import com.example.bankcards.dto.UserCreateRequest;
import com.example.bankcards.dto.UserResponse;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.exception.ResourceNotFoundException;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private CardService cardService;

    @InjectMocks
    private AdminController adminController;

    // ---------- /users ----------

    @Test
    void createUser_shouldReturn201AndBody() {
        UserCreateRequest request = mock(UserCreateRequest.class);
        UserResponse userResponse = mock(UserResponse.class);

        when(userService.createUser(request)).thenReturn(userResponse);

        ResponseEntity<UserResponse> responseEntity = adminController.createUser(request);

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertSame(userResponse, responseEntity.getBody());
        verify(userService).createUser(request);
    }

    @Test
    void createUser_whenServiceThrows_shouldPropagateException() {
        UserCreateRequest request = mock(UserCreateRequest.class);

        when(userService.createUser(request))
                .thenThrow(new IllegalArgumentException("Bad data"));

        assertThrows(IllegalArgumentException.class,
                () -> adminController.createUser(request));
    }

    @Test
    void getAllUsers_shouldReturnOkWithList() {
        List<UserResponse> users = List.of(mock(UserResponse.class));

        when(userService.getAllUsers()).thenReturn(users);

        ResponseEntity<List<UserResponse>> responseEntity =
                adminController.getAllUsers();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(users, responseEntity.getBody());
        verify(userService).getAllUsers();
    }

    @Test
    void getAllUsers_whenEmpty_shouldReturnOkWithEmptyList() {
        when(userService.getAllUsers()).thenReturn(List.of());

        ResponseEntity<List<UserResponse>> responseEntity =
                adminController.getAllUsers();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertTrue(responseEntity.getBody().isEmpty());
    }

    @Test
    void getUserById_shouldReturnOkWithUser() {
        Long id = 1L;
        UserResponse userResponse = mock(UserResponse.class);

        when(userService.getUserById(id)).thenReturn(userResponse);

        ResponseEntity<UserResponse> responseEntity =
                adminController.getUserById(id);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertSame(userResponse, responseEntity.getBody());
        verify(userService).getUserById(id);
    }

    @Test
    void getUserById_whenNotFound_shouldThrow() {
        Long id = 1L;
        when(userService.getUserById(id))
                .thenThrow(new ResourceNotFoundException("User not found"));

        assertThrows(ResourceNotFoundException.class,
                () -> adminController.getUserById(id));
    }

    @Test
    void deleteUser_shouldReturn204AndCallService() {
        Long id = 1L;

        ResponseEntity<Void> responseEntity = adminController.deleteUser(id);

        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
        verify(userService).deleteUser(id);
    }

    @Test
    void deleteUser_whenNotFound_shouldThrow() {
        Long id = 1L;
        doThrow(new ResourceNotFoundException("User not found"))
                .when(userService).deleteUser(id);

        assertThrows(ResourceNotFoundException.class,
                () -> adminController.deleteUser(id));
    }

    // ---------- /cards (admin) ----------

    @Test
    void createCard_shouldReturn201AndBody() {
        Long userId = 10L;
        CardCreateRequest request = mock(CardCreateRequest.class);
        CardResponse cardResponse = mock(CardResponse.class);

        when(cardService.adminCreateCard(request, userId)).thenReturn(cardResponse);

        ResponseEntity<CardResponse> responseEntity =
                adminController.createCard(userId, request);

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertSame(cardResponse, responseEntity.getBody());
        verify(cardService).adminCreateCard(request, userId);
    }

    @Test
    void createCard_whenUserNotFound_shouldThrow() {
        Long userId = 10L;
        CardCreateRequest request = mock(CardCreateRequest.class);

        when(cardService.adminCreateCard(request, userId))
                .thenThrow(new ResourceNotFoundException("User not found"));

        assertThrows(ResourceNotFoundException.class,
                () -> adminController.createCard(userId, request));
    }

    @Test
    void getAllCards_shouldReturnOkWithPage() {
        int page = 0;
        int size = 10;
        String search = "visa";
        CardStatus status = CardStatus.ACTIVE;

        PageResponse<CardResponse> pageResponse = mock(PageResponse.class);

        when(cardService.getAllCards(page, size, search, status))
                .thenReturn(pageResponse);

        ResponseEntity<PageResponse<CardResponse>> responseEntity =
                adminController.getAllCards(page, size, search, status);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertSame(pageResponse, responseEntity.getBody());
        verify(cardService).getAllCards(page, size, search, status);
    }

    @Test
    void getAllCards_whenServiceThrows_shouldPropagate() {
        int page = 0;
        int size = 10;

        when(cardService.getAllCards(page, size, null, null))
                .thenThrow(new IllegalStateException("DB error"));

        assertThrows(IllegalStateException.class,
                () -> adminController.getAllCards(page, size, null, null));
    }

    @Test
    void blockCard_shouldReturnOkWithCard() {
        Long cardId = 5L;
        CardResponse cardResponse = mock(CardResponse.class);

        when(cardService.adminBlockCard(cardId)).thenReturn(cardResponse);

        ResponseEntity<CardResponse> responseEntity =
                adminController.blockCard(cardId);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertSame(cardResponse, responseEntity.getBody());
        verify(cardService).adminBlockCard(cardId);
    }

    @Test
    void blockCard_whenNotFound_shouldThrow() {
        Long cardId = 5L;

        when(cardService.adminBlockCard(cardId))
                .thenThrow(new ResourceNotFoundException("Card not found"));

        assertThrows(ResourceNotFoundException.class,
                () -> adminController.blockCard(cardId));
    }

    @Test
    void activateCard_shouldReturnOkWithCard() {
        Long cardId = 7L;
        CardResponse cardResponse = mock(CardResponse.class);

        when(cardService.adminActivateCard(cardId)).thenReturn(cardResponse);

        ResponseEntity<CardResponse> responseEntity =
                adminController.activateCard(cardId);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertSame(cardResponse, responseEntity.getBody());
        verify(cardService).adminActivateCard(cardId);
    }

    @Test
    void activateCard_whenBadRequest_shouldThrow() {
        Long cardId = 7L;

        when(cardService.adminActivateCard(cardId))
                .thenThrow(new IllegalArgumentException("Cannot activate expired"));

        assertThrows(IllegalArgumentException.class,
                () -> adminController.activateCard(cardId));
    }

    @Test
    void deleteCard_shouldReturn204AndCallService() {
        Long cardId = 9L;

        ResponseEntity<Void> responseEntity = adminController.deleteCard(cardId);

        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
        verify(cardService).adminDeleteCard(cardId);
    }

    @Test
    void deleteCard_whenNotFound_shouldThrow() {
        Long cardId = 9L;
        doThrow(new ResourceNotFoundException("Card not found"))
                .when(cardService).adminDeleteCard(cardId);

        assertThrows(ResourceNotFoundException.class,
                () -> adminController.deleteCard(cardId));
    }
}