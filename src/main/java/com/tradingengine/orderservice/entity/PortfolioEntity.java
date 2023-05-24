package com.tradingengine.orderservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Locale;


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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "portfolio_id")
    private Long portfolioId;
    private String name;
    private Long clientId;


}
