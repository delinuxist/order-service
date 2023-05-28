package com.tradingengine.orderservice.marketdata.service.impl;

import com.tradingengine.orderservice.dto.OrderRequestDto;
import com.tradingengine.orderservice.enums.OrderSide;
import com.tradingengine.orderservice.marketdata.models.Order;
import com.tradingengine.orderservice.marketdata.models.ProductInfo;
import com.tradingengine.orderservice.marketdata.service.MarketDataService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MarketDataImpl implements MarketDataService {
    @Override
    public List<ProductInfo> getProductByTicker(String ticker) {
        return null;
    }

    @Override
    public List<Order> getOrdersByTickerAndSideAndType(OrderRequestDto order) {
        return null;
    }
}
