package com.tradingengine.orderservice.utils.scheduler;

import com.tradingengine.orderservice.dto.OrderRequestToExchange;
import com.tradingengine.orderservice.entity.OrderLeg;
import com.tradingengine.orderservice.enums.OrderStatus;
import com.tradingengine.orderservice.service.OrderService;
import com.tradingengine.orderservice.utils.strategy.OrderProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;
import java.util.UUID;

import static com.tradingengine.orderservice.utils.ModelBuilder.rebuildOrderRequest;

@RequiredArgsConstructor
public class FailedOrdersProcessor {
    private final OrderService orderService;
    private final OrderProcessor orderProcessor;

    @Scheduled(cron = "*/20 * * * *")
    public void processFailedOrderLegs() {
        List<OrderLeg> orderLegList = orderService.fetchAllOrderLegs()
                .stream().filter(orderLeg -> orderLeg.getOrderLegStatus().equals(OrderStatus.FAILED))
                .toList();

        for (OrderLeg order : orderLegList) {
            OrderRequestToExchange orderRequest = rebuildOrderRequest(order.getProduct(), order.getQuantity(), order.getPrice(), order.getOrderSide(), order.getType());
            String response = orderService.executeOrder(orderRequest, order.getExchangeUrl());

            OrderStatus orderStatus = UUID.fromString(response).equals(UUID.fromString("")) ? OrderStatus.FAILED : OrderStatus.OPEN;
            orderProcessor.setOrderLegStatusFromResponse(order, orderStatus);

            orderService.saveOrderLeg(order);
        }
    }
}
