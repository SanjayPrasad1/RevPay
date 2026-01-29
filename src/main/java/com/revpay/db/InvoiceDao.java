package com.revpay.db;

import com.revpay.model.Invoice;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InvoiceDao {

    public long create(Invoice invoice, Connection con) throws Exception {
        String sql = """
            INSERT INTO invoices (business_user_id, customer_user_id, total_amount, status, due_date)
            VALUES (?, ?, ?, ?, ?)
            RETURNING id
        """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, invoice.getBusinessUserId());
            ps.setLong(2, invoice.getCustomerUserId());
            ps.setBigDecimal(3, invoice.getTotalAmount());
            ps.setString(4, invoice.getStatus());
            ps.setDate(5, Date.valueOf(invoice.getDueDate()));

            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getLong(1);
        }
    }

    public Invoice findById(long invoiceId, Connection con) throws Exception {

        String sql = "SELECT * FROM invoices WHERE id = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, invoiceId);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) return null;

            Invoice i = new Invoice();
            i.setId(rs.getLong("id"));
            i.setBusinessUserId(rs.getLong("business_user_id"));
            i.setCustomerUserId(rs.getLong("customer_user_id"));
            i.setTotalAmount(rs.getBigDecimal("total_amount"));
            i.setStatus(rs.getString("status"));
            i.setDueDate(rs.getDate("due_date").toLocalDate());
            i.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

            return i;
        }
    }

    public void markPaid(long invoiceId, Connection con) throws Exception {
        String sql = "UPDATE invoices SET status = 'PAID' WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, invoiceId);
            ps.executeUpdate();
        }
    }

    public List<Invoice> findUnpaidForCustomer(long customerId, Connection con) throws Exception {

        String sql = "SELECT * FROM invoices WHERE customer_user_id = ? AND status = 'UNPAID'";
        List<Invoice> list = new ArrayList<>();

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, customerId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Invoice i = new Invoice();
                i.setId(rs.getLong("id"));
                i.setBusinessUserId(rs.getLong("business_user_id"));
                i.setTotalAmount(rs.getBigDecimal("total_amount"));
                i.setDueDate(rs.getDate("due_date").toLocalDate());
                i.setStatus(rs.getString("status"));
                list.add(i);
            }
        }
        return list;
    }

    public List<Invoice> findByBusinessUserId(long businessUserId, Connection con)
            throws SQLException {

        String sql = """
        SELECT * FROM invoices
        WHERE business_user_id = ?
        ORDER BY created_at DESC
    """;

        List<Invoice> list = new ArrayList<>();

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, businessUserId);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    public List<Invoice> findUnpaidByCustomerId(long customerUserId, Connection con)
            throws SQLException {

        String sql = """
        SELECT * FROM invoices
        WHERE customer_user_id = ?
          AND status = 'UNPAID'
        ORDER BY due_date
    """;

        List<Invoice> list = new ArrayList<>();

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, customerUserId);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }
    private Invoice map(ResultSet rs) throws SQLException {
        Invoice i = new Invoice();
        i.setId(rs.getLong("id"));
        i.setBusinessUserId(rs.getLong("business_user_id"));
        i.setCustomerUserId(rs.getLong("customer_user_id"));
        i.setTotalAmount(rs.getBigDecimal("total_amount"));
        i.setStatus(rs.getString("status"));
        i.setDueDate(rs.getDate("due_date").toLocalDate());
        i.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return i;
    }



}

