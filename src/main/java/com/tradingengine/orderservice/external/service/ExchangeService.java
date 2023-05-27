package com.tradingengine.orderservice.external.service;

import com.tradingengine.orderservice.dto.OrderRequestDto;
import com.tradingengine.orderservice.dto.OrderStatusResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FeignClient(name = "Exchange1", url = "https://exchange.matraining.com/7d21b2cb-9942-4948-9699-1b9fa5a8ad1c")
public interface ExchangeService {

    @PostMapping("/order")
    UUID placeOrder(@RequestBody OrderRequestDto orderRequestDto);

    @GetMapping("/order/{orderId}")
    OrderStatusResponseDto checkStatus(@PathVariable("orderId") UUID orderId);

    @DeleteMapping("/order/{orderId}")
    Boolean cancelOrder(@PathVariable("orderId") UUID orderId);

    @PutMapping("/order/{orderId}")
    Boolean modifyOrder(@PathVariable("orderId") UUID orderId, @RequestBody OrderRequestDto orderRequestDto);
}

