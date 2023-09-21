package com.nextuple.walletbankingapp.service;

import com.nextuple.walletbankingapp.entity.Wallet;
import com.nextuple.walletbankingapp.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class WalletService {


    @Autowired
    private WalletRepository walletRepository;
    @Autowired
    private UserService userService;


    public Optional<Wallet> getWalletById(String id) {
        return walletRepository.findById(id);
    }

    public Wallet createWallet(Wallet wallet) {
        return walletRepository.save(wallet);
    }

    public Wallet updateWallet(String id, Wallet updatedWallet) {
        if (walletRepository.existsById(id)) {
            updatedWallet.setId(id);
            return walletRepository.save(updatedWallet);
        }
        return null;
    }


}

