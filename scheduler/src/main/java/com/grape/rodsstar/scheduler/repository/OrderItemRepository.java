package com.grape.rodsstar.scheduler.repository;

import com.grape.rodsstar.scheduler.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
