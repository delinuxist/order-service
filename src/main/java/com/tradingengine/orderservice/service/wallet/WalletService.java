package com.tradingengine.orderservice.service.wallet;

import com.tradingengine.orderservice.entity.Wallet;

import java.util.Optional;
import java.util.UUID;

public interface WalletService {
    Optional<Wallet> getWalletByUserId(UUID userId);

    Wallet createWallet(UUID userId);


}
