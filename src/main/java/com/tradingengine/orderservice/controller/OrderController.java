package com.tradingengine.orderservice.controller;

import com.tradingengine.orderservice.entity.OrderEntity;
import com.tradingengine.orderservice.repository.OrderRepository;
import com.tradingengine.orderservice.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    OrderService orderService;

    @GetMapping("/test")
    public String test() {
        return "Order Service";
    }

    @PostMapping("")
    public OrderEntity createOrder(OrderEntity orderEntity){
        return
    }

    public UUID getOrderById(UUID orderiD)
}
