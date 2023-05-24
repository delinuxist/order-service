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
import java.util.UUID;

@Entity
@Table(name = "orders")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderEntity {
    @Id
    private UUID orderId;
    private String product;
    private Double price;
    private Integer quantity;
    private Long clientId;
    private OrderSide side;
    private OrderType type;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private OrderStatus status;

    @ManyToOne(
            cascade = CascadeType.ALL,
            optional = false
    )
    @JoinColumn(
            name = "portfolio_id",
            referencedColumnName = "portfolio_id"
    )
    private PortfolioEntity portfolio;


}
