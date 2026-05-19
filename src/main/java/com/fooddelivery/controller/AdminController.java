package com.fooddelivery.controller;

import com.fooddelivery.model.Order;
import com.fooddelivery.model.Restaurant;
import com.fooddelivery.service.OrderService;
import com.fooddelivery.service.RestaurantService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:3000","https://food-delivery-system-inky.vercel.app")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final OrderService orderService;
    private final RestaurantService restaurantService;

    // ✅ Constructor injection (recommended)
    public AdminController(OrderService orderService, RestaurantService restaurantService) {
        this.orderService = orderService;
        this.restaurantService = restaurantService;
    }

    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        List<Order> orders = orderService.getAllOrders();
        List<Restaurant> restaurants = restaurantService.getAllRestaurants();

        long delivered = orders.stream()
                .filter(o -> o.getStatus() == Order.OrderStatus.DELIVERED)
                .count();

        long active = orders.stream()
                .filter(o -> o.getStatus() != Order.OrderStatus.DELIVERED &&
                             o.getStatus() != Order.OrderStatus.CANCELLED)
                .count();

        double revenue = orders.stream()
                .filter(o -> o.getStatus() == Order.OrderStatus.DELIVERED)
                .mapToDouble(Order::getTotalAmount)
                .sum();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalOrders", orders.size());
        stats.put("delivered", delivered);
        stats.put("active", active);
        stats.put("revenue", revenue);
        stats.put("restaurants", restaurants.size());

        return ResponseEntity.ok(stats);
    }

    @PostMapping("/restaurants")
    public ResponseEntity<Restaurant> addRestaurant(@RequestBody Restaurant restaurant) {
        return ResponseEntity.ok(restaurantService.save(restaurant));
    }
}
