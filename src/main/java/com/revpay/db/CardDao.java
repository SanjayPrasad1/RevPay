package com.revpay.db;

import com.revpay.model.Card;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class CardDao {

    // Insert a new card
    public long insert(Card card, Connection con) throws Exception {
        String sql = """
        INSERT INTO cards
        (user_id, card_holder_name, encrypted_card_number, encrypted_cvv, expiry_month, expiry_year, card_fingerprint, is_default, created_at)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
    """;

        try (PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, card.getUserId());
            ps.setString(2, card.getCardHolderName());
            ps.setString(3, card.getEncryptedCardNumber());
            ps.setString(4, card.getEncryptedCvv());
            ps.setInt(5, card.getExpiryMonth());
            ps.setInt(6, card.getExpiryYear());
            ps.setString(7, card.getCardFingerprint());
            ps.setBoolean(8, card.isDefault());
            ps.setTimestamp(9, Timestamp.valueOf(card.getCreatedAt()));

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
                else throw new RuntimeException("Failed to insert card");
            }
        }
    }

    // Get all cards for a user
    public List<Card> findByUserId(long userId, Connection con) throws Exception {
        String sql = "SELECT * FROM cards WHERE user_id = ? ORDER BY created_at DESC";
        List<Card> list = new ArrayList<>();

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Card c = new Card();
                    c.setId(rs.getLong("id"));
                    c.setUserId(rs.getLong("user_id"));
                    c.setCardHolderName(rs.getString("card_holder_name"));
                    c.setEncryptedCardNumber(rs.getString("encrypted_card_number"));
                    c.setEncryptedCvv(rs.getString("encrypted_cvv"));
                    c.setExpiryMonth(rs.getInt("expiry_month"));
                    c.setExpiryYear(rs.getInt("expiry_year"));
                    c.setCardFingerprint(rs.getString("card_fingerprint"));
                    c.setDefault(rs.getBoolean("is_default"));
                    c.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    list.add(c);
                }
            }
        }

        return list;
    }

    // Check if a card fingerprint already exists (duplicate prevention)
    public boolean existsByFingerprint(String fingerprint, Connection con) throws Exception {
        String sql = "SELECT 1 FROM cards WHERE card_fingerprint = ? LIMIT 1";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, fingerprint);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    // Optionally, update default card
    public void unsetDefault(long userId, Connection con) throws Exception {
        String sql = "UPDATE cards SET is_default = false WHERE user_id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.executeUpdate();
        }
    }

    public void setDefault(long cardId, Connection con) throws Exception {
        String sql = "UPDATE cards SET is_default = true WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, cardId);
            ps.executeUpdate();
        }
    }

}
