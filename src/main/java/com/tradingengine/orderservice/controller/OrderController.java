package com.tradingengine.orderservice.controller;

import com.tradingengine.orderservice.dto.OrderRequestDto;
import com.tradingengine.orderservice.entity.OrderEntity;
import com.tradingengine.orderservice.exception.portfolio.PortfolioNotFoundException;
import com.tradingengine.orderservice.service.OrderService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.http.HttpHeaders;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/{portfolioId}")
    public OrderEntity createOrder(@PathVariable("portfolioId") Long portfolioId, @Validated @RequestBody OrderRequestDto orderRequestDto) throws PortfolioNotFoundException {
        return orderService.placeOrder(portfolioId,orderRequestDto);
    }

    @GetMapping
    public List<OrderEntity> getAllOrders() {
        return orderService.getAllOrders();
    }

    //todo: work on request to cancel an order, giving error 500
    @DeleteMapping("/cancelOrder")
    public String cancelOrder(@RequestBody String order_id) {
        UUID orderID = UUID.fromString(order_id);
         return orderService.cancelOrder(orderID);
    }

//    @PostMapping("/createOrder/${portfolio_id}")
//    public OrderEntity createNewOrder(Long portfolio_id, @PathVariable OrderRequestDto orderRequestDto){
//        return orderService.placeNewOrder(portfolio_id, orderRequestDto);
//    }



}
