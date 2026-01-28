package com.revpay.db;

import com.revpay.model.Transaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

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
    public List<Transaction> findByUserId(long userId, Connection con) throws Exception {

        String sql = """
        SELECT id, sender_id, receiver_id, amount, status, note, created_at
        FROM transactions
        WHERE sender_id = ? OR receiver_id = ?
        ORDER BY created_at DESC
    """;

        List<Transaction> list = new ArrayList<>();

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Transaction t = new Transaction();
                    t.setId(rs.getLong("id"));
                    t.setSenderId(rs.getLong("sender_id"));
                    t.setReceiverId(rs.getLong("receiver_id"));
                    t.setAmount(rs.getBigDecimal("amount"));
                    t.setStatus(rs.getString("status"));
                    t.setNote(rs.getString("note"));
                    t.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

                    list.add(t);
                }
            }
        }

        return list;
    }

}
