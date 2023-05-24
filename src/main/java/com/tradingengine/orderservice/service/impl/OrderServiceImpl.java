package com.tradingengine.orderservice.service.impl;

import com.tradingengine.orderservice.dto.OrderRequestDto;
import com.tradingengine.orderservice.dto.OrderStatusResponseDto;
import com.tradingengine.orderservice.entity.OrderEntity;
import com.tradingengine.orderservice.entity.PortfolioEntity;
import com.tradingengine.orderservice.enums.OrderStatus;
import com.tradingengine.orderservice.exception.order.OrderNotFoundException;
import com.tradingengine.orderservice.exception.portfolio.PortfolioNotFoundException;
import com.tradingengine.orderservice.repository.OrderRepository;
import com.tradingengine.orderservice.repository.PortfolioRepository;
import com.tradingengine.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    @Value("${exchange.one}/order")
    private String exchange1;

    private final RestTemplate restTemplate;

    private final OrderRepository orderRepository;

    private final PortfolioRepository portfolioRepository;

    public OrderEntity placeOrder(Long portfolioId, OrderRequestDto orderRequestDto) throws PortfolioNotFoundException {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        //request body entity
        HttpEntity<OrderRequestDto> entity = new HttpEntity<>(orderRequestDto,headers);

         UUID orderId =  restTemplate.exchange(exchange1,HttpMethod.POST,entity,UUID.class).getBody();

         Optional<PortfolioEntity> portfolio = portfolioRepository.findById(portfolioId);

         if(portfolio.isEmpty()) {
             throw new PortfolioNotFoundException(portfolioId);
         }

        // create order object to be saved in db
        OrderEntity order = OrderEntity.builder()
                .orderId(orderId)
                .portfolio(portfolio.get())
                .price(orderRequestDto.price())
                .product(orderRequestDto.product())
                .side(orderRequestDto.side())
                .status(OrderStatus.PENDING)
                .quantity(orderRequestDto.quantity())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .type(orderRequestDto.type())
                .clientId(null)
                .build();
        return orderRepository.save(order);
    }


    public OrderStatusResponseDto checkOrderStatus(UUID orderID) throws OrderNotFoundException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<OrderStatusResponseDto> entity = new HttpEntity<>(headers);

        Optional<OrderEntity> order = orderRepository.findById(orderID);
        if(order.isEmpty()){
            throw new OrderNotFoundException(orderID);
        }
        return restTemplate.exchange(exchange1 + "/" + orderID,HttpMethod.GET, entity, OrderStatusResponseDto.class).getBody();
    }


    public Optional<OrderEntity> getOrder(UUID orderID) {
        return orderRepository.findById(orderID);
    }


    public List<OrderEntity> getAllOrders(){
        return orderRepository.findAll();
    }


    public String cancelOrder(UUID orderId) throws OrderNotFoundException {

        Optional<OrderEntity> order = orderRepository.findById(orderId);
        if(order.isEmpty()){
            throw new OrderNotFoundException(orderId);
        }

        Boolean result = restTemplate.exchange(exchange1 +"/"+ order.get().getOrderId(), HttpMethod.DELETE, null, Boolean.class).getBody();

        if (Boolean.TRUE.equals(result)){
            OrderEntity cancelledOrder = order.get();
            cancelledOrder.setStatus(OrderStatus.CANCELLED);
            cancelledOrder.setUpdatedAt(LocalDateTime.now());
            orderRepository.save(cancelledOrder);
            return "order with order id " + orderId + " has been cancelled!";
        }
        return "order could not be  cancelled";
    }


    public String modifyOrder(UUID orderId, OrderRequestDto orderRequestDto) throws OrderNotFoundException {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<OrderRequestDto> entity = new HttpEntity<>(orderRequestDto, headers);

        Optional<OrderEntity> order = orderRepository.findById(orderId);

        if(order.isEmpty()){
            throw new OrderNotFoundException(orderId);
        }
        else if ( !(order.get().getProduct().equals(orderRequestDto.product())) ) {
            return "Product must be same as original";
        } else if( !(order.get().getSide().equals(orderRequestDto.side())) ) {
            return "Order must be same as original";
        } else {
            Boolean result = restTemplate.exchange(exchange1 + "/" + order.get().getOrderId(), HttpMethod.PUT, entity, Boolean.class).getBody();
            if(result.equals(true)) {
                order.get().setPrice(orderRequestDto.price());
                order.get().setQuantity(orderRequestDto.quantity());
                return "Order updated successfully";

            }
        }
        return "Order cannot be updated";
    }
}
