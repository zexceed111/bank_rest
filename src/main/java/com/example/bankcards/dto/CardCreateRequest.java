package com.example.bankcards.dto;

import com.example.bankcards.entity.CardType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardCreateRequest {
    @NotBlank(message = "Card holder name is required")
    private String cardHolderName;
    
    @NotNull(message = "Expiry date is required")
    private LocalDate expiryDate;
    
    private CardType cardType = CardType.DEBIT;
}
