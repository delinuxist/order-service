package com.tradingengine.orderservice.utils;

import com.tradingengine.orderservice.dto.OrderRequestToExchange;
import com.tradingengine.orderservice.entity.OrderEntity;
import com.tradingengine.orderservice.entity.OrderLeg;
import com.tradingengine.orderservice.entity.PortfolioEntity;
import com.tradingengine.orderservice.entity.StockEntity;
import com.tradingengine.orderservice.enums.OrderSide;
import com.tradingengine.orderservice.enums.OrderStatus;
import com.tradingengine.orderservice.enums.OrderType;
import com.tradingengine.orderservice.exception.portfolio.PortfolioNotFoundException;
import com.tradingengine.orderservice.repository.OrderRepository;
import com.tradingengine.orderservice.repository.PortfolioRepository;
import com.tradingengine.orderservice.service.PortfolioService;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Builder
public class ModelBuilder {
    static PortfolioRepository portfolioRepository;
    static PortfolioService portfolioService;
    static OrderRepository orderRepository;

    public static OrderEntity buildOrderEntity(OrderRequestToExchange orderRequestToExchange, UUID portfolioId, UUID userId) throws PortfolioNotFoundException {

        Optional<PortfolioEntity> portfolio = portfolioRepository.findById(portfolioId);

        if (portfolio.isEmpty()) {
            throw new PortfolioNotFoundException(portfolioId);
        }

        return OrderEntity.builder()
                .product(orderRequestToExchange.getProduct())
                .price(orderRequestToExchange.getPrice())
                .quantity(orderRequestToExchange.getQuantity())
                .userId(userId)
                .orderSide(orderRequestToExchange.getOrderSide())
                .type(orderRequestToExchange.getType())
                .status(OrderStatus.PENDING)
                .portfolio(portfolioService.getPortfolioById(portfolioId))
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static OrderLeg buildOrderLeg(UUID response, String exchangeUrl, OrderEntity orderEntity, Integer quantity) {
        return OrderLeg.builder()
                .Id(response)
                .product(orderEntity.getProduct())
                .price(orderEntity.getPrice())
                .quantity(quantity)
                .orderSide(orderEntity.getOrderSide())
                .type(orderEntity.getType())
                .orderLegStatus(OrderStatus.OPEN)
                .orderEntity(orderEntity)
                .exchangeUrl(exchangeUrl)
                .build();
    }

    public static StockEntity buildStockEntity(OrderEntity order) {
        return StockEntity.builder()
                .portfolio(order.getPortfolio())
                .price(order.getPrice())
                .ticker(order.getProduct())
                .quantity(order.getQuantity())
                .build();
    }

    public static OrderRequestToExchange rebuildOrderRequest(String product, Integer quantity, Double price, OrderSide orderSide, OrderType type) {
        return OrderRequestToExchange.builder()
                .product(product)
                .quantity(quantity)
                .price(price)
                .orderSide(orderSide)
                .type(type)
                .build();
    }


}
