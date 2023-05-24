package com.tradingengine.orderservice.controller;

import com.tradingengine.orderservice.dto.OrderRequestDto;
import com.tradingengine.orderservice.dto.OrderStatusResponseDto;
import com.tradingengine.orderservice.entity.OrderEntity;
import com.tradingengine.orderservice.exception.order.OrderNotFoundException;
import com.tradingengine.orderservice.exception.portfolio.PortfolioNotFoundException;
import com.tradingengine.orderservice.service.impl.OrderServiceImpl;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    private final OrderServiceImpl orderServiceImpl;

    public OrderController(OrderServiceImpl orderServiceImpl) {
        this.orderServiceImpl = orderServiceImpl;
    }

    @PostMapping("/{portfolioId}")
    public OrderEntity createOrder(@PathVariable("portfolioId") Long portfolioId, @Validated @RequestBody OrderRequestDto orderRequestDto) throws PortfolioNotFoundException {
        return orderServiceImpl.placeOrder(portfolioId,orderRequestDto);
    }

    @GetMapping("/getOrder/{orderID}")
    public Optional<OrderEntity> getOrder(@PathVariable("orderID") UUID orderID){
        return orderServiceImpl.getOrder(orderID);
    }

    @GetMapping("/checkStatus/{orderID}")
    public OrderStatusResponseDto checkOrderStatus(@PathVariable("orderID") UUID orderID) throws OrderNotFoundException {
        return orderServiceImpl.checkOrderStatus(orderID);
    }

    @GetMapping("/allOrders")
    public List<OrderEntity> getAllOrders() {
        return orderServiceImpl.getAllOrders();
    }

    @DeleteMapping("/{orderID}")
    public String cancelOrder(@PathVariable("orderID") UUID order_id) throws OrderNotFoundException {
         return orderServiceImpl.cancelOrder(order_id);
    }

    @PutMapping("/{orderID}")
    public String modifyExistingOrder(@PathVariable("orderID") UUID order_id, @RequestBody OrderRequestDto orderRequestDto) throws OrderNotFoundException {
        return orderServiceImpl.modifyOrder(order_id, orderRequestDto);
    }

}
