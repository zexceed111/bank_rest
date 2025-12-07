package com.example.bankcards.controller;

import com.example.bankcards.dto.CardCreateRequest;
import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.dto.PageResponse;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.service.CardService;
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
@RequestMapping("/api/cards")
@Tag(name = "Cards", description = "API для управления картами")
@SecurityRequirement(name = "bearerAuth")
public class CardController {
    
    @Autowired
    private CardService cardService;
    
    @PostMapping
    @Operation(summary = "Создать карту", description = "Создание новой карты для текущего пользователя")
    public ResponseEntity<CardResponse> createCard(
            @Valid @RequestBody CardCreateRequest request,
            Authentication authentication) {
        CardResponse response = cardService.createCard(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Получить карту по ID", description = "Получение информации о карте по её идентификатору")
    public ResponseEntity<CardResponse> getCardById(
            @PathVariable Long id,
            Authentication authentication) {
        CardResponse response = cardService.getCardById(id, authentication.getName());
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    @Operation(summary = "Получить список карт", description = "Получение списка карт текущего пользователя с пагинацией и фильтрацией")
    public ResponseEntity<PageResponse<CardResponse>> getUserCards(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) CardStatus status,
            Authentication authentication) {
        PageResponse<CardResponse> response = cardService.getUserCards(
                authentication.getName(), page, size, search, status);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{id}/block")
    @Operation(summary = "Заблокировать карту", description = "Запрос на блокировку карты")
    public ResponseEntity<CardResponse> blockCard(
            @PathVariable Long id,
            Authentication authentication) {
        CardResponse response = cardService.blockCard(id, authentication.getName());
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{id}/activate")
    @Operation(summary = "Активировать карту", description = "Активация заблокированной карты")
    public ResponseEntity<CardResponse> activateCard(
            @PathVariable Long id,
            Authentication authentication) {
        CardResponse response = cardService.activateCard(id, authentication.getName());
        return ResponseEntity.ok(response);
    }
}
