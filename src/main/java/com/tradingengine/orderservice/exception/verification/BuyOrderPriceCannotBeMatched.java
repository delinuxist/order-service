package com.tradingengine.orderservice.exception.verification;

public class BuyOrderPriceCannotBeMatched extends Exception{
    public BuyOrderPriceCannotBeMatched() {
        super("Can't match your Buy Order Price on Exchange , Your price doesn't meet price on Exchange");
    }
}
