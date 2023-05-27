package com.tradingengine.orderservice.exception.portfolio;

public class PortfolioDeletionFailedException extends Exception{
    public PortfolioDeletionFailedException() {
        super("Portfolio contains stocks");
    }
}
