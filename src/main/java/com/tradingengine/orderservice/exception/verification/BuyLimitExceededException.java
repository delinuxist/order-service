package com.tradingengine.orderservice.exception.verification;

public class BuyLimitExceededException extends Exception{
    public BuyLimitExceededException() {
        super("Your requested quantity exceeded quantity available on Exchange");
    }
}
