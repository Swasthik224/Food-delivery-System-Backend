package com.fooddelivery.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "http://localhost:3000")
public class CartController {

    @GetMapping
    public ResponseEntity<Map<String, String>> getCart() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Cart is managed client-side. Use /api/orders to place an order.");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateCart(@RequestBody Map<String, Object> cartData) {
        Map<String, Object> response = new HashMap<>();
        response.put("valid", true);
        response.put("message", "Cart is valid");
        return ResponseEntity.ok(response);
    }
}