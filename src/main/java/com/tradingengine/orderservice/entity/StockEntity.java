package com.tradingengine.orderservice.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "stock")
@Data
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

    @ManyToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "portfolio_id", referencedColumnName = "portfolio_id")
    private PortfolioEntity portfolio;


}
