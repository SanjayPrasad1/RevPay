package com.revpay.service;

import com.revpay.db.DBConnection;
import com.revpay.db.TransactionDao;
import com.revpay.db.UserDao;
import com.revpay.db.WalletDao;
import com.revpay.model.Transaction;
import com.revpay.model.User;
import com.revpay.model.Wallet;

import java.math.BigDecimal;
import java.sql.Connection;
import java.time.LocalDateTime;

public class MoneyTransferService {

    private final UserDao userDao = new UserDao();
    private final WalletDao walletDao = new WalletDao();
    private final TransactionDao transactionDao = new TransactionDao();

    public void transferMoney(long fromUserId,
                              String toIdentifier,
                              BigDecimal amount,
                              String note) throws Exception {

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Amount must be greater than zero.");
        }

        Connection con = null;

        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false);

            // 1) Resolve receiver
            User receiver;

            if (toIdentifier.matches("\\d+")) {
                receiver = userDao.findById(Long.parseLong(toIdentifier), con);
            } else {
                receiver = userDao.findByEmailOrPhone(toIdentifier, con);
            }

            if (receiver == null) {
                throw new RuntimeException("Receiver not found.");
            }

            if (receiver.getId() == fromUserId) {
                throw new RuntimeException("Cannot send money to yourself.");
            }

            // 2) Lock wallets
            Wallet senderWallet = walletDao.findByUserIdForUpdate(fromUserId, con);
            Wallet receiverWallet = walletDao.findByUserIdForUpdate(receiver.getId(), con);

            if (senderWallet == null || receiverWallet == null) {
                throw new RuntimeException("Wallet missing.");
            }

            // 3) Balance check
            if (senderWallet.getBalance().compareTo(amount) < 0) {
                throw new RuntimeException("Insufficient balance.");
            }

            // 4) Debit / Credit
            BigDecimal newSenderBalance = senderWallet.getBalance().subtract(amount);
            BigDecimal newReceiverBalance = receiverWallet.getBalance().add(amount);

            walletDao.updateBalance(senderWallet.getId(), newSenderBalance, con);
            walletDao.updateBalance(receiverWallet.getId(), newReceiverBalance, con);

            // 5) Insert transaction
            Transaction tx = new Transaction();
            tx.setSenderId(fromUserId);
            tx.setReceiverId(receiver.getId());
            tx.setAmount(amount);
            tx.setType("SEND");
            tx.setStatus("SUCCESS");
            tx.setNote(note);
            tx.setCreatedAt(LocalDateTime.now());

            transactionDao.insert(tx, con);

            con.commit();

        } catch (Exception e) {

            if (con != null) {
                try {
                    con.rollback();
                } catch (Exception ex) {
                    // ignore rollback failure
                }
            }

            throw e;  // DO NOT swallow this

        } finally {

            if (con != null) {
                try {
                    con.close();
                } catch (Exception e) {
                    // ignore close failure
                }
            }
        }
    }
}