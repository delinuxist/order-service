package com.tradingengine.orderservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Setter
@Getter
@ToString
@Table(name = "portfolios")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PortfolioEntity {
    @Id
    @GeneratedValue
    private UUID portfolioId;
    private String name;
    private UUID userId;

    @OneToMany(mappedBy = "portfolio", fetch = FetchType.EAGER)
    List<StockEntity> stocksOwned;
}
