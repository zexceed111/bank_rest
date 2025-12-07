package com.example.bankcards.dto;

import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.CardType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardResponse {
    private Long id;
    private String maskedCard;
    private String cardHolderName;
    private BigDecimal balance;
    private LocalDate expiryDate;
    private CardStatus status;
    private CardType cardType;
    private boolean isDefault;
}
