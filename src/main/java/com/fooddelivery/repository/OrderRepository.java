package com.fooddelivery.repository;

import com.fooddelivery.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserEmailOrderByPlacedAtDesc(String email);
    List<Order> findAllByOrderByPlacedAtDesc();
    List<Order> findByStatus(Order.OrderStatus status);
}
