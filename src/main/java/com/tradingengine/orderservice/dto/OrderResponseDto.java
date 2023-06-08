package com.tradingengine.orderservice.dto;

import com.tradingengine.orderservice.enums.OrderSide;
import com.tradingengine.orderservice.enums.OrderStatus;
import com.tradingengine.orderservice.enums.OrderType;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderResponseDto {
    private UUID id;
    private String product;
    private Double price;
    private Integer quantity;

    private UUID userId;
    private OrderSide orderSide;
    private OrderType type;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private OrderStatus status;

    private List<OrderLegResponseDto> orderLegResponseDtos;
 }
