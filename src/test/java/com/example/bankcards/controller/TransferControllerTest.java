package com.example.bankcards.controller;

import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.dto.TransferResponse;
import com.example.bankcards.service.TransferService;
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
class TransferControllerTest {

    @Mock
    private TransferService transferService;

    @InjectMocks
    private TransferController transferController;

    @Test
    void transfer_shouldReturn201AndBody() {
        TransferRequest request = mock(TransferRequest.class);
        Authentication auth = mock(Authentication.class);
        TransferResponse transferResponse = mock(TransferResponse.class);

        when(auth.getName()).thenReturn("user");
        when(transferService.transferBetweenOwnCards(request, "user"))
                .thenReturn(transferResponse);

        ResponseEntity<TransferResponse> responseEntity =
                transferController.transfer(request, auth);

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertSame(transferResponse, responseEntity.getBody());
        verify(transferService).transferBetweenOwnCards(request, "user");
    }

    @Test
    void transfer_whenBadRequest_shouldThrow() {
        TransferRequest request = mock(TransferRequest.class);
        Authentication auth = mock(Authentication.class);

        when(auth.getName()).thenReturn("user");
        when(transferService.transferBetweenOwnCards(request, "user"))
                .thenThrow(new IllegalArgumentException("Insufficient balance"));

        assertThrows(IllegalArgumentException.class,
                () -> transferController.transfer(request, auth));
    }
}
