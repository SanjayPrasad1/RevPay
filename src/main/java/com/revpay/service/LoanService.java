package com.revpay.service;

import com.revpay.db.DBConnection;
import com.revpay.dao.LoanDao;
import com.revpay.dao.UserDao;
import com.revpay.model.Loan;
import com.revpay.model.User;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.List;

public class LoanService {

    private final LoanDao loanDao = new LoanDao();
    private final UserDao userDao = new UserDao();
    private final WalletService walletService = new WalletService();
    private final TransactionService transactionService = new TransactionService();

    public void applyLoan(
            long businessUserId,
            BigDecimal amount,
            int tenureMonths
    ) throws Exception {

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);

            User user = userDao.findById(businessUserId, con);

            if (user == null || !"BUSINESS".equalsIgnoreCase(user.getUserType())) {
                throw new Exception("Only BUSINESS users can apply for loans");
            }

            BigDecimal interestRate = BigDecimal.valueOf(10); // fixed 10%
            BigDecimal totalPayable =
                    amount.add(amount.multiply(interestRate).divide(BigDecimal.valueOf(100)));

            BigDecimal emi =
                    totalPayable.divide(BigDecimal.valueOf(tenureMonths), 2, BigDecimal.ROUND_HALF_UP);

            Loan loan = new Loan();
            loan.setBusinessUserId(businessUserId);
            loan.setPrincipalAmount(amount);
            loan.setInterestRate(interestRate);
            loan.setTenureMonths(tenureMonths);
            loan.setMonthlyEmi(emi);
            loan.setOutstandingAmount(totalPayable);
            loan.setStatus("ACTIVE");

            loanDao.createLoan(loan, con);

            walletService.creditWalletInternal(
                    businessUserId,
                    amount,
                    "Business Loan Credit",
                    con
            );

            con.commit();
        }
    }

    public List<Loan> getActiveLoans(long businessUserId) throws Exception {
        try (Connection con = DBConnection.getConnection()) {
            return loanDao.findActiveLoans(businessUserId, con);
        }
    }

    public void payEmi(
            long loanId,
            long businessUserId,
            BigDecimal amount
    ) throws Exception {

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);

            walletService.debitWalletInternal(
                    businessUserId,
                    amount,
                    "Loan EMI Payment",
                    con
            );

            loanDao.reduceOutstanding(loanId, amount, con);
            loanDao.closeIfPaid(loanId, con);

            con.commit();
        }
    }
}
