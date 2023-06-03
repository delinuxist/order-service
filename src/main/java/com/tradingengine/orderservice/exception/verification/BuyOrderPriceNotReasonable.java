package com.tradingengine.orderservice.exception.verification;

public class BuyOrderPriceNotReasonable extends Exception{
    public BuyOrderPriceNotReasonable() {
        super("Price is not reasonable");
    }
}
