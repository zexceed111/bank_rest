package com.example.bankcards.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "cards")
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "card_number", unique = true, nullable = false, length = 500)
    private String cardNumber;

    @NotNull
    @Column(name = "masked_card", length = 19, nullable = false)
    private String maskedCard;

    @NotNull
    @Column(name = "card_holder_name")
    private String cardHolderName;

    @Column(name = "balance", nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CardStatus status = CardStatus.ACTIVE;

    @Column(name = "ccv", nullable = false, length = 500)
    private String ccv;

    @Column(name = "pin_code", nullable = false, length = 500)
    private String pinCode;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "update_at")
    private LocalDateTime updateAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "is_default")
    private boolean isDefault;

    @Column(name = "card_type")
    @Enumerated(EnumType.STRING)
    private CardType cardType = CardType.DEBIT;

    @PrePersist
    protected void onCreate(){
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        updateAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate(){
        updateAt = LocalDateTime.now();
    }

    public boolean isExpired(){
        return LocalDate.now().isAfter(expiryDate);
    }

    public boolean isUseble(){
        return status == CardStatus.ACTIVE && !isExpired();
    }

}
