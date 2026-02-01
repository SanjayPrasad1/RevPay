package com.revpay.service;

import com.revpay.dao.TransactionDao;
import com.revpay.dao.UserDao;
import com.revpay.dao.WalletDao;
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
                              String note,
                              Connection con) throws Exception {

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Amount must be greater than zero.");
        }

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
    }

    public long transferMoneyInternal(long fromUserId,
                                      long toUserId,
                                      BigDecimal amount,
                                      String note,
                                      Connection con) throws Exception {

        Wallet sender = walletDao.findByUserIdForUpdate(fromUserId, con);
        Wallet receiver = walletDao.findByUserIdForUpdate(toUserId, con);

        if (sender.getBalance().compareTo(amount) < 0)
            throw new RuntimeException("Insufficient balance");

        walletDao.updateBalance(
                sender.getId(),
                sender.getBalance().subtract(amount),
                con
        );

        walletDao.updateBalance(
                receiver.getId(),
                receiver.getBalance().add(amount),
                con
        );

        Transaction tx = new Transaction();
        tx.setSenderId(fromUserId);
        tx.setReceiverId(toUserId);
        tx.setAmount(amount);
        tx.setStatus("SUCCESS");
        tx.setType("REQUEST");
        tx.setNote(note);
        tx.setCreatedAt(LocalDateTime.now());

        return transactionDao.insert(tx, con);
    }

}

