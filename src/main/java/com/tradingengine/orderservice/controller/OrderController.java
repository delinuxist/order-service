package com.tradingengine.orderservice.controller;

import com.tradingengine.orderservice.dto.OrderRequestDto;
import com.tradingengine.orderservice.dto.OrderStatusResponseDto;
import com.tradingengine.orderservice.entity.OrderEntity;
import com.tradingengine.orderservice.exception.order.OrderNotFoundException;
import com.tradingengine.orderservice.exception.portfolio.PortfolioNotFoundException;
import com.tradingengine.orderservice.service.OrderService;
import com.tradingengine.orderservice.service.impl.OrderServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/{portfolioId}")
    public OrderEntity createOrder(@PathVariable("portfolioId") Long portfolioId,
                                   @Validated @RequestBody OrderRequestDto orderRequestDto) throws PortfolioNotFoundException {
        return orderService.placeOrder(portfolioId,orderRequestDto);
    }

    @GetMapping("/getOrder/{orderId}")
    public OrderEntity getOrderById(@PathVariable("orderId") UUID orderId) throws OrderNotFoundException {
        return orderService.getOrderById(orderId);
    }

    @GetMapping("/checkStatus/{orderId}")
    public OrderStatusResponseDto checkOrderStatus(@PathVariable("orderId") UUID orderId) throws OrderNotFoundException {
        return orderService.checkOrderStatus(orderId);
    }

    @GetMapping("/allOrders")
    public List<OrderEntity> getAllOrders() {
        return orderService.getAllOrders();
    }

    @DeleteMapping("/{orderId}")
    public Boolean cancelOrder(@PathVariable("orderId") UUID orderId) throws OrderNotFoundException {
         return orderService.cancelOrder(orderId);
    }

    @PutMapping("/{orderId}")
    public Boolean modifyExistingOrder(
            @PathVariable("orderId") UUID orderId,
            @RequestBody OrderRequestDto orderRequestDto
    ) throws OrderNotFoundException {
        return orderService.modifyOrder(orderId, orderRequestDto);
    }

}
