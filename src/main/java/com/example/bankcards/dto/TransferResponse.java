package com.example.bankcards.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferResponse {
    private Long transferId;
    private Long fromCardId;
    private String fromCardMasked;
    private Long toCardId;
    private String toCardMasked;
    private BigDecimal amount;
    private LocalDateTime timestamp;
    private String status;
}
