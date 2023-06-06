package com.tradingengine.orderservice.controller;


import com.tradingengine.orderservice.entity.OrderEntity;
import com.tradingengine.orderservice.exception.order.OrderNotFoundException;
import com.tradingengine.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

//@RestController
//@RequestMapping("/api/trade")
//@RequiredArgsConstructor
//public class TradeController {
//    private final OrderService orderService;
//    @GetMapping("/open/{orderId}")
//    public OrderEntity getOrderById(@PathVariable("orderId") UUID orderId) throws OrderNotFoundException {
//        return orderService.getOrderById(orderId);
//    }
//}
