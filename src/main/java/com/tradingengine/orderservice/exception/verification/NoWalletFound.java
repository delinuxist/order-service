package com.tradingengine.orderservice.exception.verification;

import java.util.UUID;

public class NoWalletFound extends Exception{
    public NoWalletFound(UUID userId) {
        super("No wallet found for user with ID " + userId);
    }
}
