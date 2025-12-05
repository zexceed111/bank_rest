package com.example.bankcards.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    private long id;

    @Size(min = 12, max = 12)
    @Column(name = "card_number", unique = true, nullable = false)
    private String cardNumber;

    @Size(min = 12, max = 12)
    @NotNull
    @Column(name = "masked_card")
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

    @Column(name = "ccv", nullable = false)
    private String ccv;

    @Column(name = "pin_code", nullable = false)
    private String pinCode;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "update_at")
    private LocalDateTime updateAt;

    @OneToMany(fetch = FetchType.LAZY)
    @Column(name = "user_id", nullable = false)
    private User user;

    @Column(name = "is_default")
    private boolean isDefault;

    @Column(name = "card_type")
    @Enumerated(EnumType.STRING)
    private CardType cardType = CardType.DEBIT;

    @PrePersist
    protected void onCreate(){
        createdAt = LocalDateTime.now();
        updateAt = LocalDateTime.now();
        genarateMaskNumber();
    }

    @PrePersist
    protected void onUpdate(){
        updateAt = LocalDateTime.now();
        if(maskedCard == null){
            genarateMaskNumber();
        }
    }

    private void genarateMaskNumber(){
        if (cardNumber != null && cardNumber.length() >= 4){
            this.cardNumber = "**** **** ****" + cardNumber.substring(cardNumber.length() - 4);
        }
    }

    public boolean isExpired(){
        return LocalDate.now().isAfter(expiryDate);
    }

    public boolean isUseble(){
        return status == CardStatus.ACTIVE && !isExpired();
    }

}
