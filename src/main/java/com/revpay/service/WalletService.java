package com.revpay.service;

import com.revpay.db.WalletDao;
import com.revpay.model.Wallet;

import java.sql.Connection;

public class WalletService {

    private final WalletDao walletDao = new WalletDao();

    public void createWalletForUser(long userId, Connection con) throws Exception {
        walletDao.createForUser(userId, con);
    }

    public Wallet getWallet(long userId, Connection con) throws Exception {
        return walletDao.findByUserId(userId,con);
    }
}
