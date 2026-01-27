package com.revpay.db;

import com.revpay.model.Transaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;

public class TransactionDao {

    public void insert(Transaction tx, Connection con) throws Exception {

        String sql = """
            INSERT INTO transactions
            (sender_id, receiver_id, amount, type, status, note, created_at)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, tx.getSenderId());
            ps.setLong(2, tx.getReceiverId());
            ps.setBigDecimal(3, tx.getAmount());
            ps.setString(4, tx.getType());
            ps.setString(5, tx.getStatus());
            ps.setString(6, tx.getNote());
            ps.setTimestamp(7, Timestamp.valueOf(tx.getCreatedAt()));

            ps.executeUpdate();
        }
    }
}
