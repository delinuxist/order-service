package com.tradingengine.orderservice.controller;

import com.tradingengine.orderservice.entity.Wallet;
import com.tradingengine.orderservice.utils.wallet.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @GetMapping("/{userId}")
    public Wallet createOrder(@PathVariable("userId") UUID userId) {
        walletService.createWallet(userId);
        return walletService.getWalletByUserId(userId).orElseThrow(RuntimeException::new);
    }

}
