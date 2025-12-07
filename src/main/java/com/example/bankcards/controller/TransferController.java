package com.example.bankcards.controller;

import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.dto.TransferResponse;
import com.example.bankcards.service.TransferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transfers")
@Tag(name = "Transfers", description = "API для переводов между картами")
@SecurityRequirement(name = "bearerAuth")
public class TransferController {
    
    @Autowired
    private TransferService transferService;
    
    @PostMapping
    @Operation(summary = "Перевод между своими картами", description = "Выполнение перевода между картами текущего пользователя")
    public ResponseEntity<TransferResponse> transfer(
            @Valid @RequestBody TransferRequest request,
            Authentication authentication) {
        TransferResponse response = transferService.transferBetweenOwnCards(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
