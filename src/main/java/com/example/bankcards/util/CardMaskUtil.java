package com.example.bankcards.util;

public class CardMaskUtil {
    
    private static final String MASK_PATTERN = "**** **** **** ";
    

    public static String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            return cardNumber;
        }

        if (cardNumber.startsWith("****")) {
            return cardNumber;
        }

        String lastFour = cardNumber.substring(cardNumber.length() - 4);
        return MASK_PATTERN + lastFour;
    }

    public static String getLastFourDigits(String maskedCard) {
        if (maskedCard == null || maskedCard.length() < 4) {
            return "";
        }
        return maskedCard.substring(maskedCard.length() - 4);
    }
}
