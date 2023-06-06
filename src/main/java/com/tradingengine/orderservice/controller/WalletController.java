package com.tradingengine.orderservice.controller;

import com.tradingengine.orderservice.dto.OrderRequestDto;
import com.tradingengine.orderservice.entity.OrderEntity;
import com.tradingengine.orderservice.entity.Wallet;
import com.tradingengine.orderservice.exception.portfolio.PortfolioNotFoundException;
import com.tradingengine.orderservice.exception.wallet.NoWalletFound;
import com.tradingengine.orderservice.service.OrderService;
import com.tradingengine.orderservice.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/wallet")
@RequiredArgsConstructor
public class WalletController {

        private final WalletService walletService;

        @PostMapping("/{userId}")
        @ResponseStatus(HttpStatus.CREATED)
        public void getWallet(@PathVariable("userId") UUID userId)  {
            walletService.createWallet(userId);

        }

        @GetMapping("/{userId}")
        public Wallet getUserWallet(@PathVariable("userId") UUID userId) throws NoWalletFound {
            Optional<Wallet> wallet = walletService.getWalletByUserId(userId);
            return wallet.orElseThrow(() -> new NoWalletFound("No wallet found"));
        }

}
