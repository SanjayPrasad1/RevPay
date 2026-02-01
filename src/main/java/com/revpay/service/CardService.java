package com.revpay.service;

import com.revpay.dao.CardDao;
import com.revpay.model.Card;
import com.revpay.util.EncryptionUtil;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

public class CardService {

    private final CardDao cardDao = new CardDao();

    public void addCard(long userId,
                        String cardHolderName,
                        String cardNumber,
                        String cvv,
                        int expiryMonth,
                        int expiryYear,
                        boolean setDefault,
                        Connection con) throws Exception {


        // expiry validation
        YearMonth now = YearMonth.now();
        YearMonth cardExpiry = YearMonth.of(expiryYear, expiryMonth);

        if (cardExpiry.isBefore(now)) {
            throw new RuntimeException("Card is expired");
        }

        if (expiryMonth < 1 || expiryMonth > 12) {
            throw new RuntimeException("Invalid expiry month");
        }

        // 1. Encrypt sensitive info
        String encCard = EncryptionUtil.encrypt(cardNumber);
        String encCvv = EncryptionUtil.encrypt(cvv);

        // 2. Generate fingerprint for duplicate check
        String fingerprint = EncryptionUtil.hash(cardNumber + cvv + expiryMonth + expiryYear);

        if (cardDao.existsByFingerprint(fingerprint, con)) {
            throw new RuntimeException("This card is already added");
        }

        if (setDefault) {
            cardDao.unsetDefault(userId, con);
        }

        Card card = new Card();
        card.setUserId(userId);
        card.setCardHolderName(cardHolderName);
        card.setEncryptedCardNumber(encCard);
        card.setEncryptedCvv(encCvv);
        card.setExpiryMonth(expiryMonth);
        card.setExpiryYear(expiryYear);
        card.setCardFingerprint(fingerprint);
        card.setDefault(setDefault);
        card.setCreatedAt(LocalDateTime.now());

        cardDao.insert(card, con);
    }

    public List<Card> getUserCards(long userId, Connection con) throws Exception {
        return cardDao.findByUserId(userId, con);
    }

    public void setDefaultCard(long userId, long cardId, Connection con) throws Exception {
        cardDao.unsetDefault(userId, con);
        cardDao.setDefault(cardId, con); // new DAO method
    }

}
