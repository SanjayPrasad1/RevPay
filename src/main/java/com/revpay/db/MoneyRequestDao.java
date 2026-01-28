package com.revpay.db;

import com.revpay.model.MoneyRequest;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class MoneyRequestDao {

    public void createRequest(long requesterId,
                              long requesteeId,
                              BigDecimal amount,
                              Connection con) throws Exception {

        String sql = """
            INSERT INTO money_requests (requester_id, requestee_id, amount, status)
            VALUES (?, ?, ?, 'PENDING')
        """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, requesterId);
            ps.setLong(2, requesteeId);
            ps.setBigDecimal(3, amount);
            ps.executeUpdate();
        }
    }
    public MoneyRequest findPendingById(long requestId, long requesteeId, Connection con) throws Exception {
        String sql = """
        SELECT * FROM money_requests
        WHERE id = ? AND requestee_id = ? AND status = 'PENDING'
        FOR UPDATE
    """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, requestId);
            ps.setLong(2, requesteeId);

            ResultSet rs = ps.executeQuery();
            if (!rs.next()) return null;

            MoneyRequest mr = new MoneyRequest();
            mr.setId(rs.getLong("id"));
            mr.setRequesterId(rs.getLong("requester_id"));
            mr.setRequesteeId(rs.getLong("requestee_id"));
            mr.setAmount(rs.getBigDecimal("amount"));
            mr.setStatus(rs.getString("status"));
            return mr;
        }
    }

    public void updateStatus(long requestId,
                             String status,
                             Connection con) throws Exception {

        String sql = """
        UPDATE money_requests
        SET status = ?
        WHERE id = ?
    """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setLong(2, requestId);
            ps.executeUpdate();
        }
    }

    public List<MoneyRequest> findPendingForUser(long requesteeId, Connection con) throws Exception {

        String sql = """
        SELECT mr.id,
               mr.requester_id,
               mr.requestee_id,
               mr.amount,
               mr.status,
               mr.created_at,
               u.full_name
        FROM money_requests mr
        JOIN users u ON mr.requester_id = u.id
        WHERE mr.requestee_id = ?
          AND mr.status = 'PENDING'
        ORDER BY mr.created_at
    """;

        List<MoneyRequest> list = new ArrayList<>();

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, requesteeId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                MoneyRequest mr = new MoneyRequest();
                mr.setId(rs.getLong("id"));
                mr.setRequesterId(rs.getLong("requester_id"));
                mr.setRequesteeId(rs.getLong("requestee_id"));
                mr.setAmount(rs.getBigDecimal("amount"));
                mr.setStatus(rs.getString("status"));
                mr.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                mr.setRequesterName(rs.getString("full_name")); // add field in model

                list.add(mr);
            }
        }
        return list;
    }

    public void markAccepted(long requestId, long transactionId, Connection con)
            throws Exception {

        String sql = """
        UPDATE money_requests
        SET status = 'ACCEPTED',
            transaction_id = ?
        WHERE id = ?
    """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, transactionId);
            ps.setLong(2, requestId);
            ps.executeUpdate();
        }
    }

}
