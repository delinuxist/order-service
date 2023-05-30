package com.tradingengine.orderservice.exception.verification;

public class InsufficientBalanceException  extends Exception{
    public InsufficientBalanceException() {
        super("Amount is wallet not Sufficient to complete your order!");
    }
}
