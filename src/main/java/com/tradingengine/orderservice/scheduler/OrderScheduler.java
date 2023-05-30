package com.tradingengine.orderservice.scheduler;

import com.tradingengine.orderservice.dto.OrderStatusResponseDto;
import com.tradingengine.orderservice.entity.OrderEntity;
import com.tradingengine.orderservice.entity.StockEntity;
import com.tradingengine.orderservice.enums.OrderSide;
import com.tradingengine.orderservice.enums.OrderStatus;
import com.tradingengine.orderservice.external.service.ExchangeService;
import com.tradingengine.orderservice.service.OrderService;

import com.tradingengine.orderservice.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderScheduler {

    private final OrderService orderService;

    private final StockService stockService;

    private final ExchangeService exchangeService;

    @Scheduled(fixedRate = 30_000)
    private void updateFulfilledOrderAndCreateStock() {
        List<OrderEntity> orders = orderService.fetchPendingOrders();

        orders.forEach(order -> {
                    if (order.getStatus() != OrderStatus.CANCELLED) {
                        OrderStatusResponseDto orderStatus = exchangeService.checkStatus(order.getOrderId());
                        if (orderStatus.quantity().equals(orderStatus.cumulatitiveQuantity())) {
                            order.setStatus(OrderStatus.FULFILLED);
                            order.setUpdatedAt(LocalDateTime.now());
                            orderService.updateOrderStatus(order);
                            createStock(order);
                        }
                    }
                }
         );
    }

    private void createStock(OrderEntity order) {
        StockEntity stock = stockService.fetchStockByPortfolioAndTicker(order.getPortfolio(),order.getProduct());

        if(stock != null) {
            if (order.getSide().equals(OrderSide.SELL)){
                stock.setQuantity(stock.getQuantity() - order.getQuantity());
                stock.setPrice(stock.getPrice() - order.getPrice());
            } else {
                stock.setQuantity(stock.getQuantity() + order.getQuantity());
                stock.setPrice(stock.getPrice() + order.getPrice());
            }
        } else {
            stock = StockEntity.builder()
                    .portfolio(order.getPortfolio())
                    .price(order.getPrice())
                    .ticker(order.getProduct())
                    .quantity(order.getQuantity())
                    .build();
        }
        stockService.saveStock(stock);
    }

}
