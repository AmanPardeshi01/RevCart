package com.revcart.repository;

import com.revcart.entity.Order;
import com.revcart.entity.User;
import com.revcart.enums.OrderStatus;
import java.time.Instant;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByUser(User user, Pageable pageable);
    List<Order> findByDeliveryAgentAndStatus(User deliveryAgent, OrderStatus status);

    // Count orders assigned to a delivery agent (PACKED or OUT_FOR_DELIVERY)
    @Query("SELECT COUNT(o) FROM Order o WHERE o.deliveryAgent = :agent AND o.status IN (com.revcart.enums.OrderStatus.PACKED, com.revcart.enums.OrderStatus.OUT_FOR_DELIVERY)")
    long countAssignedOrders(@Param("agent") User agent);

    // Count orders in transit (OUT_FOR_DELIVERY)
    @Query("SELECT COUNT(o) FROM Order o WHERE o.deliveryAgent = :agent AND o.status = com.revcart.enums.OrderStatus.OUT_FOR_DELIVERY")
    long countInTransitOrders(@Param("agent") User agent);

    // Count orders delivered today
    @Query("SELECT COUNT(o) FROM Order o WHERE o.deliveryAgent = :agent AND o.status = com.revcart.enums.OrderStatus.DELIVERED AND o.deliveredAt >= :startOfDay AND o.deliveredAt < :endOfDay")
    long countDeliveredToday(@Param("agent") User agent, @Param("startOfDay") Instant startOfDay, @Param("endOfDay") Instant endOfDay);

    // Count pending orders (PLACED or PACKED without delivery agent)
    @Query("SELECT COUNT(o) FROM Order o WHERE o.deliveryAgent IS NULL AND o.status IN (com.revcart.enums.OrderStatus.PLACED, com.revcart.enums.OrderStatus.PACKED)")
    long countPendingOrders();

    // Get assigned orders for a delivery agent
    @Query("SELECT o FROM Order o WHERE o.deliveryAgent = :agent AND o.status IN (com.revcart.enums.OrderStatus.PACKED, com.revcart.enums.OrderStatus.OUT_FOR_DELIVERY) ORDER BY o.createdAt DESC")
    List<Order> findAssignedOrders(@Param("agent") User agent);

    // Get in transit orders for a delivery agent
    @Query("SELECT o FROM Order o WHERE o.deliveryAgent = :agent AND o.status = com.revcart.enums.OrderStatus.OUT_FOR_DELIVERY ORDER BY o.createdAt DESC")
    List<Order> findInTransitOrders(@Param("agent") User agent);

    // Get pending orders (no delivery agent assigned)
    @Query("SELECT o FROM Order o WHERE o.deliveryAgent IS NULL AND o.status IN (com.revcart.enums.OrderStatus.PLACED, com.revcart.enums.OrderStatus.PACKED) ORDER BY o.createdAt DESC")
    List<Order> findPendingOrders();
}

