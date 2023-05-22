package com.tradingengine.orderservice.entity;

import com.tradingengine.orderservice.enumeration.OrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
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
    private String ticker;
    private Double price;
    private Integer quantity;
    private Integer clientId;
    private String side;
    private String type;
    private Date createdAt;
    private Date updatedAt;
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
