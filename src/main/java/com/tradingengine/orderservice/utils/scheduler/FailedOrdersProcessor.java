package com.tradingengine.orderservice.utils.scheduler;

import com.tradingengine.orderservice.dto.OrderRequestToExchange;
import com.tradingengine.orderservice.entity.OrderLeg;
import com.tradingengine.orderservice.enums.OrderStatus;
import com.tradingengine.orderservice.service.OrderService;
import com.tradingengine.orderservice.utils.ModelBuilder;
import com.tradingengine.orderservice.utils.WebClientService;
import com.tradingengine.orderservice.utils.strategy.OrderProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class FailedOrdersProcessor {
    private final OrderService orderService;
    private final OrderProcessor orderProcessor;
    private final WebClientService webClientService;
    private final ModelBuilder builder;

    @Scheduled(cron = "*/20 * * * * *")
    public void processFailedOrderLegs() {
        List<OrderLeg> orderLegList = orderService.fetchAllOrderLegs()
                .stream().filter(orderLeg -> orderLeg.getOrderLegStatus().equals(OrderStatus.FAILED))
                .toList();

        for (OrderLeg order : orderLegList) {
            OrderRequestToExchange orderRequest = builder.rebuildOrderRequest(order.getProduct(), order.getQuantity(), order.getPrice(), order.getOrderSide(), order.getType());
            String response = webClientService.placeOrderOnExchangeAndGetID(orderRequest, order.getExchangeUrl());

            OrderStatus orderStatus = response.equals("") ? OrderStatus.FAILED : OrderStatus.OPEN;
            order.setOrderLegStatus(orderStatus);
            orderService.saveOrderLeg(order);
        }
    }
}
