package com.tradingengine.orderservice.events;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.tradingengine.orderservice.dto.OrderRequestToExchange;
import com.tradingengine.orderservice.dto.RedisOrderInformation;
import com.tradingengine.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
@RequiredArgsConstructor
public class RedisMessageSubscriber {

    @Autowired
    OrderService  orderService;

    @Autowired
    Gson gson;



    public void onMessage(String  message) throws IOException {
        log.info("Message consumed  {}", gson.fromJson(message, RedisOrderInformation.class));
        RedisOrderInformation order = gson.fromJson(message, RedisOrderInformation.class);
        OrderRequestToExchange orderRequest = OrderRequestToExchange.builder()
                .price(order.getPrice())
                .product(order.getProduct())
                .type(order.getType())
                .side(order.getSide())
                .quantity(order.getQuantity())
                .build();
        orderService.processAndPlaceOrder(order.getPortfolioId(), orderRequest);


    }
}
