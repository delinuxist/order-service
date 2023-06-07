package com.tradingengine.orderservice.exception.verification;

public class BuyOrderPriceNotReasonable extends Exception{
    public BuyOrderPriceNotReasonable() {
        super("Your Buy Order Price is too high Reduce it to save money");
    }
}
