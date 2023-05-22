package com.tradingengine.orderservice.service;

import com.tradingengine.orderservice.entity.OrderEntity;
import com.tradingengine.orderservice.enumeration.OrderStatus;
import com.tradingengine.orderservice.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    private OrderEntity createNewOrder(OrderEntity order){
        return orderRepository.save(order);
    }

    private void deleteOrder(OrderEntity order){
         orderRepository.delete(order);
    }

    private void deleteOrderById(UUID orderID){
        orderRepository.deleteById(orderID);
    }

    private void cancelOrder(UUID orderID){
        Optional<OrderEntity> optionalOrder = orderRepository.findById(orderID);
        if (optionalOrder.isPresent()){
            OrderEntity existingOrder = optionalOrder.get();
            existingOrder.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(existingOrder);
        }
    }

    /* todo: cancel all pending orders
    private void cancelAllPendingOrders()

     */

    private List<OrderEntity> getAllOrders(){
        return orderRepository.findAll();
    }
}
