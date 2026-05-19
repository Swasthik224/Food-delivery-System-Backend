package com.fooddelivery.controller;

import com.fooddelivery.model.Restaurant;
import com.fooddelivery.repository.RestaurantRepository;
import com.fooddelivery.service.RestaurantService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

import java.util.List;

@RestController
@RequestMapping("/api/restaurants")
@CrossOrigin(origins = "http://localhost:5173","https://food-delivery-system-inky.vercel.app")
public class RestaurantController {

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private RestaurantRepository restaurantRepository;

    // ✅ GET ALL
    @GetMapping
    public ResponseEntity<List<Restaurant>> getAll() {

        return ResponseEntity.ok(
                restaurantService.getAllRestaurants()
        );
    }

    // ✅ GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<Restaurant> getById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                restaurantService.getById(id)
        );
    }

    // ✅ GET OWNER RESTAURANT
    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<Restaurant> getOwnerRestaurant(
            @PathVariable Long ownerId
    ) {

        Restaurant restaurant =
                restaurantRepository
                        .findByOwnerId(ownerId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Restaurant not found"
                                ));

        return ResponseEntity.ok(
                restaurant
        );
    }

    // ✅ CREATE
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Restaurant> create(
            @RequestBody Restaurant restaurant
    ) {

        return ResponseEntity.ok(
                restaurantService.save(restaurant)
        );
    }

    // ✅ UPDATE
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Restaurant> update(
            @PathVariable Long id,
            @RequestBody Restaurant restaurant
    ) {

        restaurant.setId(id);

        return ResponseEntity.ok(
                restaurantService.save(restaurant)
        );
    }

    // ✅ DELETE
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(
            @PathVariable Long id
    ) {

        restaurantService.delete(id);

        return ResponseEntity.noContent()
                .build();
    }
}
