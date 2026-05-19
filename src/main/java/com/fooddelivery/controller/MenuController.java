package com.fooddelivery.controller;

import com.fooddelivery.model.MenuItem;
import com.fooddelivery.model.Restaurant;
import com.fooddelivery.repository.MenuItemRepository;
import com.fooddelivery.repository.RestaurantRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {
    "http://localhost:3000",
    "https://food-delivery-system-inky.vercel.app"
})
public class MenuController {

    private final MenuItemRepository menuItemRepository;
    private final RestaurantRepository restaurantRepository;

    // ✅ ONLY ONE constructor
    public MenuController(MenuItemRepository menuItemRepository,
                          RestaurantRepository restaurantRepository) {
        this.menuItemRepository = menuItemRepository;
        this.restaurantRepository = restaurantRepository;
    }

    // ➕ Add item (FIXED)
    @PostMapping("/menu")
    public MenuItem addItem(@RequestBody MenuItem item) {

        if (item.getRestaurant() == null || item.getRestaurant().getId() == null) {
            throw new RuntimeException("Restaurant ID is required");
        }

        // 🔥 Fetch real restaurant
        Long restaurantId = item.getRestaurant().getId();

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));

        // 🔥 Attach properly
        item.setRestaurant(restaurant);

        item.setAvailable(true);

        return menuItemRepository.save(item);
    }

    // 🔄 Toggle availability
    @PutMapping("/menu/{id}/availability")
    public MenuItem toggleAvailability(@PathVariable Long id) {
        MenuItem item = menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        item.setAvailable(!item.isAvailable());
        return menuItemRepository.save(item);
    }

    // 📋 Get menu for user page
    @GetMapping("/restaurants/{restaurantId}/menu")
    public List<MenuItem> getMenuByRestaurant(@PathVariable Long restaurantId) {
        return menuItemRepository.findByRestaurantId(restaurantId);
    }
}
