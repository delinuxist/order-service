package com.tradingengine.orderservice.service.wallet.impl;

import com.tradingengine.orderservice.entity.Wallet;
import com.tradingengine.orderservice.repository.WalletRepository;
import com.tradingengine.orderservice.service.wallet.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class WalletServiceImpl implements WalletService {
    @Autowired
    private WalletRepository walletRepository;

    @Override
    public Optional<Wallet> getWalletByUserId(UUID userId) {
        return walletRepository.findByUserId(userId);
    }

    @Override
    public Wallet createWallet(UUID userId) {
        Wallet walletEntity = Wallet.builder()
                .amount(100.0)
                .userId(userId)
                .build();
        return walletRepository.save(walletEntity);
    }
}
