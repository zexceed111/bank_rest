package com.example.bankcards.controller;

import com.example.bankcards.dto.CardCreateRequest;
import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.dto.PageResponse;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.exception.ResourceNotFoundException;
import com.example.bankcards.service.CardService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardControllerTest {

    @Mock
    private CardService cardService;

    @InjectMocks
    private CardController cardController;

    @Test
    void createCard_shouldReturn201AndBody() {
        CardCreateRequest request = mock(CardCreateRequest.class);
        Authentication auth = mock(Authentication.class);
        CardResponse cardResponse = mock(CardResponse.class);

        when(auth.getName()).thenReturn("user");
        when(cardService.createCard(request, "user")).thenReturn(cardResponse);

        ResponseEntity<CardResponse> responseEntity =
                cardController.createCard(request, auth);

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertSame(cardResponse, responseEntity.getBody());
        verify(cardService).createCard(request, "user");
    }

    @Test
    void createCard_whenServiceThrows_shouldPropagate() {
        CardCreateRequest request = mock(CardCreateRequest.class);
        Authentication auth = mock(Authentication.class);

        when(auth.getName()).thenReturn("user");
        when(cardService.createCard(request, "user"))
                .thenThrow(new IllegalStateException("Something bad"));

        assertThrows(IllegalStateException.class,
                () -> cardController.createCard(request, auth));
    }

    @Test
    void getCardById_shouldReturnOkWithCard() {
        Long id = 1L;
        Authentication auth = mock(Authentication.class);
        CardResponse cardResponse = mock(CardResponse.class);

        when(auth.getName()).thenReturn("user");
        when(cardService.getCardById(id, "user")).thenReturn(cardResponse);

        ResponseEntity<CardResponse> responseEntity =
                cardController.getCardById(id, auth);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertSame(cardResponse, responseEntity.getBody());
        verify(cardService).getCardById(id, "user");
    }

    @Test
    void getCardById_whenNotFound_shouldThrow() {
        Long id = 1L;
        Authentication auth = mock(Authentication.class);

        when(auth.getName()).thenReturn("user");
        when(cardService.getCardById(id, "user"))
                .thenThrow(new ResourceNotFoundException("Card not found"));

        assertThrows(ResourceNotFoundException.class,
                () -> cardController.getCardById(id, auth));
    }

    @Test
    void getUserCards_shouldReturnOkWithPage() {
        int page = 0;
        int size = 10;
        String search = "visa";
        CardStatus status = CardStatus.ACTIVE;
        Authentication auth = mock(Authentication.class);
        PageResponse<CardResponse> pageResponse = mock(PageResponse.class);

        when(auth.getName()).thenReturn("user");
        when(cardService.getUserCards("user", page, size, search, status))
                .thenReturn(pageResponse);

        ResponseEntity<PageResponse<CardResponse>> responseEntity =
                cardController.getUserCards(page, size, search, status, auth);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertSame(pageResponse, responseEntity.getBody());
        verify(cardService).getUserCards("user", page, size, search, status);
    }

    @Test
    void getUserCards_whenServiceThrows_shouldPropagate() {
        int page = 0;
        int size = 10;
        Authentication auth = mock(Authentication.class);

        when(auth.getName()).thenReturn("user");
        when(cardService.getUserCards("user", page, size, null, null))
                .thenThrow(new IllegalArgumentException("Bad paging"));

        assertThrows(IllegalArgumentException.class,
                () -> cardController.getUserCards(page, size, null, null, auth));
    }

    @Test
    void blockCard_shouldReturnOkWithCard() {
        Long id = 3L;
        Authentication auth = mock(Authentication.class);
        CardResponse cardResponse = mock(CardResponse.class);

        when(auth.getName()).thenReturn("user");
        when(cardService.blockCard(id, "user")).thenReturn(cardResponse);

        ResponseEntity<CardResponse> responseEntity =
                cardController.blockCard(id, auth);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertSame(cardResponse, responseEntity.getBody());
        verify(cardService).blockCard(id, "user");
    }

    @Test
    void blockCard_whenNotFound_shouldThrow() {
        Long id = 3L;
        Authentication auth = mock(Authentication.class);

        when(auth.getName()).thenReturn("user");
        when(cardService.blockCard(id, "user"))
                .thenThrow(new ResourceNotFoundException("Card not found"));

        assertThrows(ResourceNotFoundException.class,
                () -> cardController.blockCard(id, auth));
    }

    @Test
    void activateCard_shouldReturnOkWithCard() {
        Long id = 4L;
        Authentication auth = mock(Authentication.class);
        CardResponse cardResponse = mock(CardResponse.class);

        when(auth.getName()).thenReturn("user");
        when(cardService.activateCard(id, "user")).thenReturn(cardResponse);

        ResponseEntity<CardResponse> responseEntity =
                cardController.activateCard(id, auth);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertSame(cardResponse, responseEntity.getBody());
        verify(cardService).activateCard(id, "user");
    }

    @Test
    void activateCard_whenBadRequest_shouldThrow() {
        Long id = 4L;
        Authentication auth = mock(Authentication.class);

        when(auth.getName()).thenReturn("user");
        when(cardService.activateCard(id, "user"))
                .thenThrow(new IllegalArgumentException("Cannot activate"));

        assertThrows(IllegalArgumentException.class,
                () -> cardController.activateCard(id, auth));
    }
}
