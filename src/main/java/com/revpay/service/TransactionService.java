package com.revpay.service;

import com.revpay.db.DBConnection;
import com.revpay.db.TransactionDao;
import com.revpay.model.Transaction;

import java.sql.Connection;
import java.util.List;

public class TransactionService {
    private final TransactionDao transactionDao = new TransactionDao();

    public List<Transaction> getUserTransactions(long userId) throws Exception{
        try(Connection con = DBConnection.getConnection()) {
            return transactionDao.findByUserId(userId,con);
        }
    }
}
