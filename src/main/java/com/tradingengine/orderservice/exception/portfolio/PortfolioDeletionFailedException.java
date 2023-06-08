package com.tradingengine.orderservice.exception.portfolio;

public class PortfolioDeletionFailedException extends RuntimeException{
    public PortfolioDeletionFailedException() {
        super("Portfolio contains stocks");
    }
}
