package com.tradingengine.orderservice.entity;

import com.tradingengine.orderservice.enums.OrderSide;
import com.tradingengine.orderservice.enums.OrderStatus;
import com.tradingengine.orderservice.enums.OrderType;
import jakarta.persistence.*;
import lombok.*;

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
    @GeneratedValue
    private UUID id;
//    Added orderId from exchange
    private UUID orderId;
    private String product;
    private Double price;
    private Integer quantity;
    // made the clientid UUID
    private UUID clientId;
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
            referencedColumnName = "portfolioId"
    )
    private PortfolioEntity portfolio;


}
