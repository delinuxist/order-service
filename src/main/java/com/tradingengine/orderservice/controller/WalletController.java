package com.tradingengine.orderservice.controller;

import com.tradingengine.orderservice.entity.Wallet;
import com.tradingengine.orderservice.exception.wallet.WalletNotFoundException;
import com.tradingengine.orderservice.service.wallet.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @GetMapping("/create/{userId}")
    public Wallet createWallet(@PathVariable("userId") UUID userId) {
        walletService.createWallet(userId);
        return walletService.getWalletByUserId(userId).orElseThrow(RuntimeException::new);
    }

    @GetMapping("/{userId}")
    public Wallet getWalletByUserId(@PathVariable("userId") UUID userId) throws WalletNotFoundException {
        Optional<Wallet> wallet = walletService.getWalletByUserId(userId);
        return wallet.orElseThrow(() -> new WalletNotFoundException("No wallet owned by this user"));
    }

}
