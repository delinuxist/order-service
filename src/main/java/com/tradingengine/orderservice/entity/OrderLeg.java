package com.tradingengine.orderservice.entity;

import com.tradingengine.orderservice.enums.OrderSide;
import com.tradingengine.orderservice.enums.OrderStatus;
import com.tradingengine.orderservice.enums.OrderType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Entity
@Table(name = "orderleg")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderLeg {
    @Id
    @GeneratedValue
    UUID orderLegId;
    String Id;
    private String product;
    private Double price;
    private Integer quantity;
    private OrderSide orderSide;
    private OrderType type;
    private OrderStatus orderLegStatus;
    private String exchangeUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    OrderEntity orderEntity;


}
