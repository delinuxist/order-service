package com.tradingengine.orderservice.repository;

import com.tradingengine.orderservice.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, UUID> {

    @Query(
            nativeQuery = true,
            value = "select * from orders where status=0"
    )
    List<OrderEntity> findPendingOrders();

    @Query(
            nativeQuery = true,
            value = "select * from orders where status=1"
    )
    List<OrderEntity> findFilledOrders();

    @Query(
            nativeQuery = true,
            value = "select * from orders where status=2"
    )
    List<OrderEntity> findCancelledOrders();

    @Query(
            nativeQuery = true,
            value = "select * from orders where status=3"
    )
    List<OrderEntity> findPartiallyFilledOrders();


    @Query(nativeQuery = true,
            value = "select * from orders where status=4"
    )
    List<OrderEntity> findFailedOrders();
}
