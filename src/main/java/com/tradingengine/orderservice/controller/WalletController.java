package com.tradingengine.orderservice.controller;

import com.tradingengine.orderservice.dto.OrderRequestDto;
import com.tradingengine.orderservice.entity.OrderEntity;
import com.tradingengine.orderservice.entity.Wallet;
import com.tradingengine.orderservice.exception.portfolio.PortfolioNotFoundException;
import com.tradingengine.orderservice.service.OrderService;
import com.tradingengine.orderservice.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
public class WalletController {

        private final WalletService walletService;

        @GetMapping("/{userId}")
        public Wallet createOrder(@PathVariable("userId") UUID userId)  {
            walletService.createWallet(userId);
            return walletService.getWalletByUserId(userId).orElseThrow(RuntimeException::new);
        }

    }
