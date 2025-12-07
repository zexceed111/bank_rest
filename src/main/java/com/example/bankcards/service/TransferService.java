package com.example.bankcards.service;

import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.dto.TransferResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.exception.BadRequestException;
import com.example.bankcards.exception.ResourceNotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@Transactional
public class TransferService {
    
    @Autowired
    private CardRepository cardRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public TransferResponse transferBetweenOwnCards(TransferRequest request, String username) {
        // Получаем пользователя
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        
        // Получаем карты
        Card fromCard = cardRepository.findByIdAndUser(request.getFromCardId(), user)
                .orElseThrow(() -> new ResourceNotFoundException("From card not found or does not belong to you"));
        
        Card toCard = cardRepository.findByIdAndUser(request.getToCardId(), user)
                .orElseThrow(() -> new ResourceNotFoundException("To card not found or does not belong to you"));
        
        // Проверяем, что карты разные
        if (fromCard.getId().equals(toCard.getId())) {
            throw new BadRequestException("Cannot transfer to the same card");
        }
        
        // Проверяем статус карт
        if (fromCard.getStatus() != CardStatus.ACTIVE || fromCard.isExpired()) {
            throw new BadRequestException("From card is not active or expired");
        }
        
        if (toCard.getStatus() != CardStatus.ACTIVE || toCard.isExpired()) {
            throw new BadRequestException("To card is not active or expired");
        }
        
        // Проверяем баланс
        if (fromCard.getBalance().compareTo(request.getAmount()) < 0) {
            throw new BadRequestException("Insufficient balance");
        }
        
        // Проверяем, что сумма положительная
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Transfer amount must be greater than zero");
        }
        
        // Выполняем перевод
        fromCard.setBalance(fromCard.getBalance().subtract(request.getAmount()));
        toCard.setBalance(toCard.getBalance().add(request.getAmount()));
        
        cardRepository.save(fromCard);
        cardRepository.save(toCard);
        
        // В реальном приложении здесь должна быть запись о транзакции в отдельной таблице
        // Для демо используем простой ответ
        
        return new TransferResponse(
                System.currentTimeMillis(), // В реальном приложении это должен быть ID транзакции
                fromCard.getId(),
                fromCard.getMaskedCard(),
                toCard.getId(),
                toCard.getMaskedCard(),
                request.getAmount(),
                LocalDateTime.now(),
                "SUCCESS"
        );
    }
}
