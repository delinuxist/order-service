package com.tradingengine.orderservice.addedfunctionality.wallet.impl;

import com.tradingengine.orderservice.addedfunctionality.wallet.WalletService;
import com.tradingengine.orderservice.entity.Wallet;
import com.tradingengine.orderservice.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class WalletServiceImpl implements WalletService {
    @Autowired
    private  WalletRepository walletRepository;

    @Override
    public Optional<Wallet> getWalletByUserId(UUID userId) {
        return walletRepository.findByClientId(userId);
    }

    @Override
    public void createWallet(UUID userId) {
        Wallet walletEntity = Wallet.builder()
                .amount(100.0)
                .clientId(userId)
                .build();
        walletRepository.save(walletEntity);
    }
}
