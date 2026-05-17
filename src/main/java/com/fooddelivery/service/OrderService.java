package com.fooddelivery.service;

import com.fooddelivery.dto.OrderRequest;
import com.fooddelivery.model.*;
import com.fooddelivery.repository.MenuItemRepository;
import com.fooddelivery.repository.OrderRepository;
import com.fooddelivery.repository.RestaurantRepository;
import com.fooddelivery.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final MenuItemRepository menuItemRepository;
    private final RestaurantRepository restaurantRepository;

    // ✅ constructor instead of Lombok
    public OrderService(OrderRepository orderRepository,
                        UserRepository userRepository,
                        MenuItemRepository menuItemRepository,
                        RestaurantRepository restaurantRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.menuItemRepository = menuItemRepository;
        this.restaurantRepository = restaurantRepository;
    }

    public Order placeOrder(OrderRequest req, String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<OrderItem> orderItems = req.getItems().stream().map(itemReq -> {

            MenuItem menuItem = menuItemRepository.findById(itemReq.getMenuItemId())
                    .orElseThrow(() -> new RuntimeException("Menu item not found"));

            OrderItem item = new OrderItem();
            item.setMenuItem(menuItem);
            item.setQuantity(itemReq.getQuantity());
            item.setPrice(menuItem.getPrice() * itemReq.getQuantity());

            return item;

        }).collect(Collectors.toList());

        Restaurant restaurant = orderItems.get(0).getMenuItem().getRestaurant();

        double total = orderItems.stream()
                .mapToDouble(OrderItem::getPrice)
                .sum();

        Order order = new Order();
        order.setUser(user);
        order.setRestaurant(restaurant);
        order.setItems(orderItems);
        order.setTotalAmount(total);
        order.setStatus(Order.OrderStatus.PLACED);
        order.setPlacedAt(LocalDateTime.now());
        order.setDeliveryAddress(req.getDeliveryAddress());
        order.setPaymentId(req.getPaymentId());

        return orderRepository.save(order);
    }

    public List<Order> getUserOrders(String email) {
        return orderRepository.findByUserEmailOrderByPlacedAtDesc(email);
    }

    public Order trackOrder(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    public Order updateStatus(Long id, Order.OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus(status);
        return orderRepository.save(order);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAllByOrderByPlacedAtDesc();
    }
}