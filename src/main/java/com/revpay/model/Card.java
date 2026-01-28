package com.revpay.model;

import java.time.LocalDateTime;

public class Card {
    private long id;
    private long userId;
    private String cardHolderName;
    private String encryptedCardNumber;
    private String encryptedCvv;
    private int expiryMonth;
    private int expiryYear;
    private String cardFingerprint;
    private boolean isDefault;
    private LocalDateTime createdAt;

    // Getters & Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }

    public String getCardHolderName() { return cardHolderName; }
    public void setCardHolderName(String cardHolderName) { this.cardHolderName = cardHolderName; }

    public String getEncryptedCardNumber() { return encryptedCardNumber; }
    public void setEncryptedCardNumber(String encryptedCardNumber) { this.encryptedCardNumber = encryptedCardNumber; }

    public String getEncryptedCvv() { return encryptedCvv; }
    public void setEncryptedCvv(String encryptedCvv) { this.encryptedCvv = encryptedCvv; }

    public int getExpiryMonth() { return expiryMonth; }
    public void setExpiryMonth(int expiryMonth) { this.expiryMonth = expiryMonth; }

    public int getExpiryYear() { return expiryYear; }
    public void setExpiryYear(int expiryYear) { this.expiryYear = expiryYear; }

    public String getCardFingerprint() { return cardFingerprint; }
    public void setCardFingerprint(String cardFingerprint) { this.cardFingerprint = cardFingerprint; }

    public boolean isDefault() { return isDefault; }
    public void setDefault(boolean isDefault) { this.isDefault = isDefault; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
