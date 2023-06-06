package com.tradingengine.orderservice.exception.wallet;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NoWalletFound extends Exception{
    public NoWalletFound(String message) {
        super(message);
    }
}
