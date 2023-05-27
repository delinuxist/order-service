package com.tradingengine.orderservice.entity;

import jakarta.persistence.*;
import lombok.*;

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
    private Long stockId;
    private String ticker;
    private Integer quantity;
    private Double price;

    @ManyToOne(optional = false)
    @JoinColumn(name = "portfolio_id", referencedColumnName = "portfolioId")
    private PortfolioEntity portfolio;
}
