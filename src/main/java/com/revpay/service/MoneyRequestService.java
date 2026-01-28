package com.revpay.service;

import com.revpay.db.DBConnection;
import com.revpay.db.MoneyRequestDao;
import com.revpay.db.UserDao;
import com.revpay.model.MoneyRequest;
import com.revpay.model.User;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.List;

public class MoneyRequestService {

    private final MoneyRequestDao moneyRequestDao = new MoneyRequestDao();
    private final MoneyTransferService moneyTransferService = new MoneyTransferService();
    private final UserDao userDao = new UserDao();
    public void requestMoney(long requesterId,
                             String toIdentifier,
                             BigDecimal amount) throws Exception {

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Amount must be greater than zero");
        }

        try (Connection con = DBConnection.getConnection()) {
            User requestee;
            if (toIdentifier.matches("\\d+")){
                requestee = userDao.findById(Long.parseLong(toIdentifier), con);
            }else {
                requestee = userDao.findByEmailOrPhone(toIdentifier, con);
            }
            if (requestee==null){
                throw new RuntimeException("User not found");
            }
            if (requesterId == requestee.getId()){
                throw new RuntimeException("Cannot request money from yourself");
            }
            moneyRequestDao.createRequest(requesterId, requestee.getId(), amount, con);
        }
    }
public void acceptRequest(long requestId, long requesteeId) throws Exception {

    Connection con = DBConnection.getConnection();
    con.setAutoCommit(false);

    try {
        MoneyRequest req =
                moneyRequestDao.findPendingById(requestId, requesteeId, con);

        if (req == null)
            throw new RuntimeException("Invalid or already processed request");

        // 1. Create transaction and GET ID
        long txId = moneyTransferService.transferMoneyInternal(
                requesteeId,
                req.getRequesterId(),
                req.getAmount(),
                "Money request accepted",
                con
        );

        // 2. Link request to transaction
        moneyRequestDao.markAccepted(requestId, txId, con);

        con.commit();

    } catch (Exception e) {
        con.rollback();
        throw e;
    } finally {
        con.close();
    }
}

    public List<MoneyRequest> viewPendingRequests(long requesteeId) throws Exception {
        try (Connection con = DBConnection.getConnection()) {
            return moneyRequestDao.findPendingForUser(requesteeId, con);
        }
    }



}
