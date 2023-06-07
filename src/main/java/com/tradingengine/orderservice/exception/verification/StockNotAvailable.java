package com.tradingengine.orderservice.exception.verification;

public class StockNotAvailable extends Exception{
    public StockNotAvailable( ) {
        super("You don't own such stock");
    }
}
