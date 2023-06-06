package com.tradingengine.orderservice.scheduler;

import com.tradingengine.orderservice.dto.OrderStatusResponseDto;
import com.tradingengine.orderservice.entity.OrderEntity;
import com.tradingengine.orderservice.entity.StockEntity;
import com.tradingengine.orderservice.enums.OrderStatus;
import com.tradingengine.orderservice.enums.Side;
import com.tradingengine.orderservice.external.service.ExchangeService;
import com.tradingengine.orderservice.service.OrderService;
import com.tradingengine.orderservice.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.tradingengine.orderservice.utils.ModelBuilder.buildStockEntity;

@Component
@RequiredArgsConstructor
public class OrderScheduler {

    private final OrderService orderService;

    private final StockService stockService;

    private final ExchangeService exchangeService;

    //    @Scheduled(fixedRate = 30_000)
    private void updateFulfilledOrderAndCreateStock() {
        List<OrderEntity> orders = orderService.fetchPendingOrders();

        orders.forEach(order -> {
                    if (order.getStatus().equals(OrderStatus.OPEN)) {
                        OrderStatusResponseDto orderStatus = exchangeService.checkStatus(order.getId());


                        if (orderStatus.quantity().equals(orderStatus.cumulatitiveQuantity())) {
                            orderService.updateOrderStatus(order, OrderStatus.FULFILLED);
                            createStock(order);
                        }
                    }
                }
        );
    }

    private void createStock(OrderEntity order) {
        StockEntity stock = stockService.fetchStockByPortfolioAndTicker(order.getPortfolio(), order.getProduct());

        if (stock != null) {
            if (order.getSide().equals(Side.SELL)) {
                stock.setQuantity(stock.getQuantity() - order.getQuantity());
                stock.setPrice(stock.getPrice() - order.getPrice());
            } else {
                stock.setQuantity(stock.getQuantity() + order.getQuantity());
                stock.setPrice(stock.getPrice() + order.getPrice());
            }
        } else {
            stock = buildStockEntity(order);
        }
        stockService.saveStock(stock);
    }

}
