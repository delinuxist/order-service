package com.tradingengine.orderservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "stocks")
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID stockId;
    private String ticker;
    private Integer quantity;
    private Double price;
    private UUID userId;


    @ManyToOne(optional = false)
    @JoinColumn(name = "portfolio_id", referencedColumnName = "portfolioId")
    private PortfolioEntity portfolio;
}
