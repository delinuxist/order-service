package com.tradingengine.orderservice.repository;

import com.tradingengine.orderservice.entity.OrderLeg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrderLegRepository extends JpaRepository<OrderLeg, UUID> {
}
