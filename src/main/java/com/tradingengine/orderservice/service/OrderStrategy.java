package com.tradingengine.orderservice.service;

import com.tradingengine.orderservice.dto.OrderRequestDto;

import java.io.IOException;

public interface OrderStrategy {
    boolean canOrderSplit(OrderRequestDto order) throws IOException;

}
