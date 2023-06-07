package com.tradingengine.orderservice.marketdata.service;

import com.tradingengine.orderservice.marketdata.models.Product;
import com.tradingengine.orderservice.marketdata.models.ProductInfo;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

public interface MarketDataService {
    Stream<ProductInfo> getProductByTicker(String ticker) throws IOException;

    Stream<Product> findOrdersBySide(String product, String side) throws IOException;

    Stream<Product> findOrdersBySideAndOrderType(String product, String side, String orderType) throws IOException;

    List<Product> findOrders(String product) throws IOException;

//    Map<String, List<ProductInfo>>
}
