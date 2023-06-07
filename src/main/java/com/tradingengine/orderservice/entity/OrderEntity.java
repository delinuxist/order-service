package com.tradingengine.orderservice.entity;

import com.tradingengine.orderservice.enums.OrderSide;
import com.tradingengine.orderservice.enums.OrderStatus;
import com.tradingengine.orderservice.enums.OrderType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class OrderEntity {
    @Id
    @GeneratedValue
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
    private String exchangeUrl;

    @ManyToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "portfolio_id", referencedColumnName = "portfolioId")
    private PortfolioEntity portfolio;

    @OneToMany(mappedBy = "orderEntity")
    private List<OrderLeg> orderLegsOwnedByEntity;


}
