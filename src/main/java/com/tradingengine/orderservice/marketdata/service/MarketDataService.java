package com.tradingengine.orderservice.marketdata.service;

import com.tradingengine.orderservice.dto.OrderRequestDto;
import com.tradingengine.orderservice.enums.OrderSide;
import com.tradingengine.orderservice.marketdata.models.Order;
import com.tradingengine.orderservice.marketdata.models.ProductInfo;

import java.util.List;

public interface MarketDataService {
    List<ProductInfo> getProductByTicker(String ticker);
    List<Order> getOrdersByTickerAndSideAndType(OrderRequestDto order);
}
