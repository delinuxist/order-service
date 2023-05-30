package com.tradingengine.orderservice.exception.verification;

public class SellOrderPriceCannotBeMatched extends Exception {
    public SellOrderPriceCannotBeMatched() {
        super("The selling price of your stock is too high might likely not be bought");
    }
}
