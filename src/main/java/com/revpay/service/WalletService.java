package com.revpay.service;

import com.revpay.app.RevPayApp;
import com.revpay.db.CardDao;
import com.revpay.db.TransactionDao;
import com.revpay.db.WalletDao;
import com.revpay.model.Card;
import com.revpay.model.Transaction;
import com.revpay.model.User;
import com.revpay.model.Wallet;

import java.math.BigDecimal;
import java.sql.Connection;
import java.time.LocalDateTime;


public class WalletService {

    private final WalletDao walletDao = new WalletDao();
    private final CardDao cardDao = new CardDao();
    private final TransactionDao transactionDao = new TransactionDao();
    private static final long SYSTEM_USER_ID = 0L;


    public void createWalletForUser(long userId, Connection con) throws Exception {
        walletDao.createForUser(userId, con);
    }

    public Wallet getWallet(long userId, Connection con) throws Exception {
        return walletDao.findByUserId(userId,con);
    }

    public void addMoneyFromCard(long userId,
                                 BigDecimal amount,
                                 Connection con) throws Exception {

        if (amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new RuntimeException("Amount must be greater than zero");

        Card defaultCard = cardDao.findDefaultCard(userId, con);
        if (defaultCard == null)
            throw new RuntimeException("No default card set");

        Wallet wallet = walletDao.findByUserIdForUpdate(userId, con);

        walletDao.updateBalance(
                wallet.getId(),
                wallet.getBalance().add(amount),
                con
        );

        Transaction tx = new Transaction();
        tx.setSenderId(null); // system
        tx.setReceiverId(userId);
        tx.setAmount(amount);
        tx.setType("TOP_UP");
        tx.setStatus("SUCCESS");
        tx.setNote("Added money via card");
        tx.setCreatedAt(LocalDateTime.now());

        transactionDao.insert(tx, con);
    }

    public void withdrawMoney(User user,
                              BigDecimal amount,
                              String note,
                              Connection con) throws Exception {

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Amount must be greater than zero");
        }

        Wallet wallet = walletDao.findByUserIdForUpdate(user.getId(), con);

        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        BigDecimal newBalance = wallet.getBalance().subtract(amount);
        walletDao.updateBalance(wallet.getId(), newBalance, con);

        Transaction tx = new Transaction();
        tx.setSenderId(user.getId());
        tx.setReceiverId(SYSTEM_USER_ID);       // SYSTEM
        tx.setAmount(amount);
        tx.setType("WITHDRAW");
        tx.setStatus("SUCCESS");
        tx.setNote(note);
        tx.setCreatedAt(LocalDateTime.now());

        transactionDao.insert(tx, con);
    }


}
