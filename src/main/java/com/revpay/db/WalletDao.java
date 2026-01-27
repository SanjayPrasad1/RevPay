package com.revpay.db;

import com.revpay.model.Wallet;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class WalletDao {

    public void createForUser(long userId, Connection con) throws Exception {
        String sql = """
        INSERT INTO wallets (user_id, balance, created_at)
        VALUES (?, ?, ?)
    """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setBigDecimal(2, BigDecimal.ZERO);
            ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));

            ps.executeUpdate();
        }
    }


    public Wallet findByUserId(long userId, Connection con) throws Exception {

        String sql = """
            SELECT id, user_id, balance, created_at
            FROM wallets
            WHERE user_id = ?
        """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, userId);

            ResultSet rs = ps.executeQuery();

            if (!rs.next()) return null;

            Wallet wallet = new Wallet();
            wallet.setId(rs.getLong("id"));
            wallet.setUserId(rs.getLong("user_id"));
            wallet.setBalance(rs.getBigDecimal("balance"));
            wallet.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

            return wallet;
        }
    }
    public Wallet findByUserIdForUpdate(long userId, Connection con) throws Exception {

        String sql = """
        SELECT id, user_id, balance, created_at
        FROM wallets
        WHERE user_id = ?
        FOR UPDATE
    """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                Wallet wallet = new Wallet();
                wallet.setId(rs.getLong("id"));
                wallet.setUserId(rs.getLong("user_id"));
                wallet.setBalance(rs.getBigDecimal("balance"));
                wallet.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

                return wallet;
            }
        }
    }
    public void updateBalance(long walletId, BigDecimal newBalance, Connection con) throws Exception {

        String sql = "UPDATE wallets SET balance = ? WHERE id = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setBigDecimal(1, newBalance);
            ps.setLong(2, walletId);
            ps.executeUpdate();
        }
    }

}
