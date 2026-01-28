package com.revpay.service;

import com.revpay.db.DBConnection;
import com.revpay.db.TransactionDao;
import com.revpay.model.Transaction;
import com.revpay.model.TransactionView;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class TransactionService {
    private final TransactionDao transactionDao = new TransactionDao();

//    public List<Transaction> getUserTransactions(long userId) throws Exception{
//        try(Connection con = DBConnection.getConnection()) {
//            return transactionDao.findByUserId(userId,con);
//        }
//    }
public List<TransactionView> getUserTransactionViews(long userId) throws Exception {
    try (Connection con = DBConnection.getConnection()) {
        return getUserTransactionViews(userId, con);
    }
}

    private List<TransactionView> getUserTransactionViews(long userId, Connection con) throws Exception {

        List<Transaction> txList = transactionDao.findByUserId(userId, con);
        List<TransactionView> result = new ArrayList<>();

        for (Transaction tx : txList) {

            TransactionView view = new TransactionView();
            view.setAmount(tx.getAmount());
            view.setCreatedAt(tx.getCreatedAt());
            view.setNote(tx.getNote());

            switch (tx.getType()) {

                case "TOP_UP":
                    view.setDisplayType("TOP UP");
                    break;

                case "WITHDRAW":
                    view.setDisplayType("WITHDRAW");
                    break;

                case "SEND":
                    view.setDisplayType(
                            tx.getSenderId() != null && tx.getSenderId().equals(userId)
                                    ? "SENT"
                                    : "RECEIVED"
                    );
                    break;

                case "REQUEST":
                    view.setDisplayType("REQUEST");
                    break;

                default:
                    view.setDisplayType("UNKNOWN");
            }

            result.add(view);
        }

        return result;
    }

}
