package com.tradingengine.orderservice.service.impl;

import com.tradingengine.orderservice.entity.Wallet;
import com.tradingengine.orderservice.repository.WalletRepository;
import com.tradingengine.orderservice.service.WalletService;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class WalletServiceImpl implements WalletService {
    private  WalletRepository walletRepository;
    @Override
    public Optional<Wallet> getWalletByUserId(UUID userId) {
        return walletRepository.findByClientId(userId);
    }
}
