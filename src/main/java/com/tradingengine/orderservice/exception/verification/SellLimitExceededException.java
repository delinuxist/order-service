package com.tradingengine.orderservice.exception.verification;

public class SellLimitExceededException extends Exception{
    public SellLimitExceededException() {
        super("Your requested quantity exceeded quantity available on Exchange");
    }
}
