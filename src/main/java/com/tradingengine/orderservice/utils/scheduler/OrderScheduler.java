package com.tradingengine.orderservice.utils.scheduler;

import com.tradingengine.orderservice.dto.OrderStatusResponseDto;
import com.tradingengine.orderservice.entity.OrderEntity;
import com.tradingengine.orderservice.entity.OrderLeg;
import com.tradingengine.orderservice.entity.StockEntity;
import com.tradingengine.orderservice.enums.OrderSide;
import com.tradingengine.orderservice.enums.OrderStatus;
import com.tradingengine.orderservice.service.OrderService;
import com.tradingengine.orderservice.service.StockService;
import com.tradingengine.orderservice.utils.ModelBuilder;
import com.tradingengine.orderservice.utils.WebClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderScheduler {

    private final OrderService orderService;
    private final StockService stockService;
    private final WebClientService webClientService;
    private final ModelBuilder builder;


//    @Scheduled(cron = "*/20 * * * * *")
//    private void orderStatusUpdater() {
//        List<OrderEntity> orderEntities = orderService.fetchAllOrders();
//
//        for (OrderEntity orderEntity : orderEntities) {
//            List<OrderLeg> orderLegsOwnedByOrder = orderEntity.getOrderLegsOwnedByEntity();
//
//            //if all order legs are fulfilled, order itself is fulfilled, else, etc
//            boolean allOrderLegsFulfilled = orderLegsOwnedByOrder
//                    .stream()
//                    .allMatch(orderLeg -> orderLeg.getOrderLegStatus().equals(OrderStatus.FILLED));
//
//            boolean allOrderLegsFailed = orderLegsOwnedByOrder
//                    .stream()
//                    .allMatch(orderLeg -> orderLeg.getOrderLegStatus().equals(OrderStatus.FAILED));
//
//            boolean someOrderLegsSucceeded = orderLegsOwnedByOrder
//                    .stream()
//                    .anyMatch(orderLeg -> orderLeg.getOrderLegStatus().equals(OrderStatus.FILLED));
//
//            if (allOrderLegsFulfilled) {
//                orderEntity.setStatus(OrderStatus.FILLED);
//            } else if (allOrderLegsFailed) {
//                orderEntity.setStatus(OrderStatus.PENDING);
//            } else if (someOrderLegsSucceeded) {
//                orderEntity.setStatus(OrderStatus.PARTIAL_FILL);
//            } else {
//                orderEntity.setStatus(OrderStatus.OPEN);
//            }
//            orderService.saveOrderEntity(orderEntity);
//        }
//    }

    @Scheduled(cron = "*/20 * * * * *")
    private void orderLegsStatusUpdater() {
        List<OrderLeg> orderLegs = orderService.fetchAllOpenOrderLegs();

        orderLegs.forEach(orderLeg -> {
                    if (orderLeg.getOrderLegStatus().equals(OrderStatus.OPEN)) {
                        OrderStatusResponseDto orderStatus = webClientService.checkOrderStatus(orderLeg.getId(), orderLeg.getExchangeUrl());

                        if (orderStatus.getQuantity().equals(orderStatus.getCumulatitiveQuantity())) {
                            orderService.updateOrderLegStatus(orderLeg, OrderStatus.FILLED);

                            createStock(orderLeg.getOrderEntity());
                        }
                    }
                }
        );
    }

    private void createStock(OrderEntity order) {
        StockEntity stock = stockService.fetchStockByPortfolioAndTicker(order.getPortfolio(), order.getProduct());

        if (stock != null) {
            if (order.getOrderSide().equals(OrderSide.SELL)) {
                stock.setQuantity(stock.getQuantity() - order.getQuantity());
                stock.setPrice(stock.getPrice() - order.getPrice());
            } else {
                stock.setQuantity(stock.getQuantity() + order.getQuantity());
                stock.setPrice(stock.getPrice() + order.getPrice());
            }
        } else {
            stock = builder.buildStockEntity(order);
        }
        stockService.saveStock(stock);
    }


}
