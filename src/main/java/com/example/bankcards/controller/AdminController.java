package com.example.bankcards.controller;

import com.example.bankcards.dto.CardCreateRequest;
import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.dto.PageResponse;
import com.example.bankcards.dto.UserCreateRequest;
import com.example.bankcards.dto.UserResponse;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin", description = "API для администратора")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private CardService cardService;
    
    // Управление пользователями
    @PostMapping("/users")
    @Operation(summary = "Создать пользователя", description = "Создание нового пользователя (только для администратора)")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserCreateRequest request) {
        UserResponse response = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/users")
    @Operation(summary = "Получить всех пользователей", description = "Получение списка всех пользователей")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/users/{id}")
    @Operation(summary = "Получить пользователя по ID", description = "Получение информации о пользователе")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        UserResponse response = userService.getUserById(id);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/users/{id}")
    @Operation(summary = "Удалить пользователя", description = "Удаление пользователя")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
    
    // Управление картами
    @PostMapping("/cards")
    @Operation(summary = "Создать карту для пользователя", description = "Создание карты для указанного пользователя")
    public ResponseEntity<CardResponse> createCard(
            @RequestParam Long userId,
            @Valid @RequestBody CardCreateRequest request) {
        CardResponse response = cardService.adminCreateCard(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/cards")
    @Operation(summary = "Получить все карты", description = "Получение списка всех карт с пагинацией")
    public ResponseEntity<PageResponse<CardResponse>> getAllCards(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) CardStatus status) {
        PageResponse<CardResponse> response = cardService.getAllCards(page, size, search, status);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/cards/{id}/block")
    @Operation(summary = "Заблокировать карту", description = "Блокировка карты администратором")
    public ResponseEntity<CardResponse> blockCard(@PathVariable Long id) {
        CardResponse response = cardService.adminBlockCard(id);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/cards/{id}/activate")
    @Operation(summary = "Активировать карту", description = "Активация карты администратором")
    public ResponseEntity<CardResponse> activateCard(@PathVariable Long id) {
        CardResponse response = cardService.adminActivateCard(id);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/cards/{id}")
    @Operation(summary = "Удалить карту", description = "Удаление карты администратором")
    public ResponseEntity<Void> deleteCard(@PathVariable Long id) {
        cardService.adminDeleteCard(id);
        return ResponseEntity.noContent().build();
    }
}
