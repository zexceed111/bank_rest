package com.example.bankcards.service;

import com.example.bankcards.dto.CardCreateRequest;
import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.dto.PageResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.BadRequestException;
import com.example.bankcards.exception.ResourceNotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.CardMaskUtil;
import com.example.bankcards.util.EncryptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CardService {
    
    @Autowired
    private CardRepository cardRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public CardResponse createCard(CardCreateRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        
        // Генерируем номер карты и шифруем его
        String cardNumber = EncryptionUtil.generateCardNumber();
        String encryptedCardNumber = EncryptionUtil.encrypt(cardNumber);
        String maskedCard = CardMaskUtil.maskCardNumber(cardNumber);
        
        // Шифруем CVV и PIN
        String cvv = EncryptionUtil.generateCVV();
        String encryptedCvv = EncryptionUtil.encrypt(cvv);
        String pin = EncryptionUtil.generatePIN();
        String encryptedPin = EncryptionUtil.encrypt(pin);
        
        Card card = new Card();
        card.setCardNumber(encryptedCardNumber);
        card.setMaskedCard(maskedCard);
        card.setCardHolderName(request.getCardHolderName());
        card.setBalance(BigDecimal.ZERO);
        card.setExpiryDate(request.getExpiryDate());
        card.setStatus(CardStatus.ACTIVE);
        card.setCcv(encryptedCvv);
        card.setPinCode(encryptedPin);
        card.setCardType(request.getCardType());
        card.setUser(user);
        
        // Проверяем срок действия
        if (card.getExpiryDate().isBefore(LocalDate.now())) {
            card.setStatus(CardStatus.EXPIRED);
        }
        
        Card savedCard = cardRepository.save(card);
        return mapToResponse(savedCard);
    }
    
    public CardResponse getCardById(Long id, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        
        Card card = cardRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found with id: " + id));
        
        // Обновляем статус, если карта истекла
        updateCardStatusIfExpired(card);
        
        return mapToResponse(card);
    }
    
    public PageResponse<CardResponse> getUserCards(String username, int page, int size, String search, CardStatus status) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Card> cardPage;
        
        if (search != null && !search.trim().isEmpty()) {
            cardPage = cardRepository.findByUserAndSearch(user, search.trim(), pageable);
        } else if (status != null) {
            cardPage = cardRepository.findByUserAndStatus(user, status, pageable);
        } else {
            cardPage = cardRepository.findByUser(user, pageable);
        }
        
        // Обновляем статусы истекших карт
        cardPage.getContent().forEach(this::updateCardStatusIfExpired);
        
        List<CardResponse> content = cardPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        
        return new PageResponse<>(
                content,
                cardPage.getNumber(),
                cardPage.getSize(),
                cardPage.getTotalElements(),
                cardPage.getTotalPages(),
                cardPage.isLast()
        );
    }
    
    public CardResponse blockCard(Long id, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        
        Card card = cardRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found with id: " + id));
        
        if (card.getStatus() == CardStatus.BLOCKED) {
            throw new BadRequestException("Card is already blocked");
        }
        
        card.setStatus(CardStatus.BLOCKED);
        Card savedCard = cardRepository.save(card);
        return mapToResponse(savedCard);
    }
    
    public CardResponse activateCard(Long id, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        
        Card card = cardRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found with id: " + id));
        
        if (card.getStatus() == CardStatus.ACTIVE) {
            throw new BadRequestException("Card is already active");
        }
        
        if (card.isExpired()) {
            throw new BadRequestException("Cannot activate expired card");
        }
        
        card.setStatus(CardStatus.ACTIVE);
        Card savedCard = cardRepository.save(card);
        return mapToResponse(savedCard);
    }
    
    // Методы для администратора
    public CardResponse adminCreateCard(CardCreateRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        String cardNumber = EncryptionUtil.generateCardNumber();
        String encryptedCardNumber = EncryptionUtil.encrypt(cardNumber);
        String maskedCard = CardMaskUtil.maskCardNumber(cardNumber);
        
        String cvv = EncryptionUtil.generateCVV();
        String encryptedCvv = EncryptionUtil.encrypt(cvv);
        String pin = EncryptionUtil.generatePIN();
        String encryptedPin = EncryptionUtil.encrypt(pin);
        
        Card card = new Card();
        card.setCardNumber(encryptedCardNumber);
        card.setMaskedCard(maskedCard);
        card.setCardHolderName(request.getCardHolderName());
        card.setBalance(BigDecimal.ZERO);
        card.setExpiryDate(request.getExpiryDate());
        card.setStatus(CardStatus.ACTIVE);
        card.setCcv(encryptedCvv);
        card.setPinCode(encryptedPin);
        card.setCardType(request.getCardType());
        card.setUser(user);
        
        if (card.getExpiryDate().isBefore(LocalDate.now())) {
            card.setStatus(CardStatus.EXPIRED);
        }
        
        Card savedCard = cardRepository.save(card);
        return mapToResponse(savedCard);
    }
    
    public CardResponse adminBlockCard(Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found with id: " + cardId));
        
        card.setStatus(CardStatus.BLOCKED);
        Card savedCard = cardRepository.save(card);
        return mapToResponse(savedCard);
    }
    
    public CardResponse adminActivateCard(Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found with id: " + cardId));
        
        if (card.isExpired()) {
            throw new BadRequestException("Cannot activate expired card");
        }
        
        card.setStatus(CardStatus.ACTIVE);
        Card savedCard = cardRepository.save(card);
        return mapToResponse(savedCard);
    }
    
    public void adminDeleteCard(Long cardId) {
        if (!cardRepository.existsById(cardId)) {
            throw new ResourceNotFoundException("Card not found with id: " + cardId);
        }
        cardRepository.deleteById(cardId);
    }
    
    public PageResponse<CardResponse> getAllCards(int page, int size, String search, CardStatus status) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        // Для упрощения используем все карты, в реальном приложении нужен более сложный запрос
        Page<Card> cardPage = cardRepository.findAll(pageable);
        
        cardPage.getContent().forEach(this::updateCardStatusIfExpired);
        
        List<CardResponse> content = cardPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        
        return new PageResponse<>(
                content,
                cardPage.getNumber(),
                cardPage.getSize(),
                cardPage.getTotalElements(),
                cardPage.getTotalPages(),
                cardPage.isLast()
        );
    }
    
    private void updateCardStatusIfExpired(Card card) {
        if (card.isExpired() && card.getStatus() != CardStatus.EXPIRED) {
            card.setStatus(CardStatus.EXPIRED);
            cardRepository.save(card);
        }
    }
    
    private CardResponse mapToResponse(Card card) {
        return new CardResponse(
                card.getId(),
                card.getMaskedCard(),
                card.getCardHolderName(),
                card.getBalance(),
                card.getExpiryDate(),
                card.getStatus(),
                card.getCardType(),
                card.isDefault()
        );
    }
    
    public Card getCardEntityById(Long id) {
        return cardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found with id: " + id));
    }
}
