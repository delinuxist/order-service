package com.tradingengine.orderservice.entity;

import com.tradingengine.orderservice.enums.OrderStatus;
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
    private UUID order_id;
    private String product;
    private Double price;
    private Integer quantity;
    private Long client_id;
    private String side;
    private String type;
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
