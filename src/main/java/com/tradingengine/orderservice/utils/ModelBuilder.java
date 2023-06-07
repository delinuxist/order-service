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
import com.tradingengine.orderservice.service.PortfolioService;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@RequiredArgsConstructor
@Service
public class ModelBuilder {
    //    static PortfolioRepository portfolioRepository;
    private final PortfolioService portfolioService;
    private final OrderRepository orderRepository;

    public OrderEntity buildOrderEntity(OrderRequestToExchange orderRequestToExchange, UUID portfolioId, UUID userId) throws PortfolioNotFoundException {

        PortfolioEntity portfolio = portfolioService.fetchPortfolioByPortfolioId(portfolioId);

        return OrderEntity.builder()
                .product(orderRequestToExchange.getProduct())
                .price(orderRequestToExchange.getPrice())
                .quantity(orderRequestToExchange.getQuantity())
                .userId(userId)
                .orderSide(orderRequestToExchange.getSide())
                .type(orderRequestToExchange.getType())
                .status(OrderStatus.PENDING)
                .portfolio(portfolioService.getPortfolioById(portfolioId))
                .createdAt(LocalDateTime.now())
                .build();
    }

    public OrderLeg buildOrderLeg(UUID response, String exchangeUrl, OrderEntity orderEntity, Integer quantity) {
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

    public StockEntity buildStockEntity(OrderEntity order) {
        return StockEntity.builder()
                .portfolio(order.getPortfolio())
                .price(order.getPrice())
                .ticker(order.getProduct())
                .quantity(order.getQuantity())
                .build();
    }

    public OrderRequestToExchange rebuildOrderRequest(String product, Integer quantity, Double price, OrderSide orderSide, OrderType type) {
        return OrderRequestToExchange.builder()
                .product(product)
                .quantity(quantity)
                .price(price)
                .side(orderSide)
                .type(type)
                .build();
    }


}
