package com.revpay.dao;

import com.revpay.model.Loan;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoanDao {

    public long createLoan(Loan loan, Connection con) throws Exception {

        String sql = """
            INSERT INTO business_loans
            (business_user_id, principal_amount, interest_rate, tenure_months,
             monthly_emi, outstanding_amount, status)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            RETURNING id
        """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, loan.getBusinessUserId());
            ps.setBigDecimal(2, loan.getPrincipalAmount());
            ps.setBigDecimal(3, loan.getInterestRate());
            ps.setInt(4, loan.getTenureMonths());
            ps.setBigDecimal(5, loan.getMonthlyEmi());
            ps.setBigDecimal(6, loan.getOutstandingAmount());
            ps.setString(7, loan.getStatus());

            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getLong(1);
        }
    }

    public List<Loan> findActiveLoans(long businessUserId, Connection con)
            throws Exception {

        String sql = """
            SELECT * FROM business_loans
            WHERE business_user_id = ?
              AND status = 'ACTIVE'
        """;

        List<Loan> list = new ArrayList<>();

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, businessUserId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Loan l = new Loan();
                l.setId(rs.getLong("id"));
                l.setBusinessUserId(rs.getLong("business_user_id"));
                l.setPrincipalAmount(rs.getBigDecimal("principal_amount"));
                l.setInterestRate(rs.getBigDecimal("interest_rate"));
                l.setTenureMonths(rs.getInt("tenure_months"));
                l.setMonthlyEmi(rs.getBigDecimal("monthly_emi"));
                l.setOutstandingAmount(rs.getBigDecimal("outstanding_amount"));
                l.setStatus(rs.getString("status"));
                l.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                list.add(l);
            }
        }
        return list;
    }

    public void reduceOutstanding(long loanId, BigDecimal amount, Connection con)
            throws Exception {

        String sql = """
            UPDATE business_loans
            SET outstanding_amount = outstanding_amount - ?
            WHERE id = ?
        """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setBigDecimal(1, amount);
            ps.setLong(2, loanId);
            ps.executeUpdate();
        }
    }

    public void closeIfPaid(long loanId, Connection con) throws Exception {

        String sql = """
            UPDATE business_loans
            SET status = 'CLOSED'
            WHERE id = ?
              AND outstanding_amount <= 0
        """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, loanId);
            ps.executeUpdate();
        }
    }
}
