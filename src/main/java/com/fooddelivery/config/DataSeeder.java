package com.fooddelivery.config;

import com.fooddelivery.model.MenuItem;
import com.fooddelivery.model.Restaurant;
import com.fooddelivery.model.User;
import com.fooddelivery.repository.MenuItemRepository;
import com.fooddelivery.repository.RestaurantRepository;
import com.fooddelivery.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final RestaurantRepository restaurantRepository;
    private final MenuItemRepository menuItemRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // ✅ Manual constructor
    public DataSeeder(RestaurantRepository restaurantRepository,
                      MenuItemRepository menuItemRepository,
                      UserRepository userRepository,
                      PasswordEncoder passwordEncoder) {
        this.restaurantRepository = restaurantRepository;
        this.menuItemRepository = menuItemRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

        // ✅ Seed admin user
        if (!userRepository.existsByEmail("admin@foodrush.com")) {
            User user = new User();
            user.setName("Admin");
            user.setEmail("admin@foodrush.com");
            user.setPassword(passwordEncoder.encode("admin123"));
            user.setRole(User.Role.ADMIN);

            userRepository.save(user);
            System.out.println("Admin created");
        }

        // ✅ Seed restaurants
        if (restaurantRepository.count() == 0) {

            Restaurant r1 = new Restaurant();
            r1.setName("Biryani House");
            r1.setCuisine("Hyderabadi");
            r1.setAddress("Banjara Hills, Hyderabad");
            r1.setRating(4.5);
            r1.setDeliveryTime(30);
            r1.setDeliveryFee(29.0);
            r1.setOpen(true);
            r1 = restaurantRepository.save(r1);

            Restaurant r2 = new Restaurant();
            r2.setName("Pizza Paradise");
            r2.setCuisine("Italian");
            r2.setAddress("Jubilee Hills, Hyderabad");
            r2.setRating(4.2);
            r2.setDeliveryTime(40);
            r2.setDeliveryFee(39.0);
            r2.setOpen(true);
            r2 = restaurantRepository.save(r2);

            Restaurant r3 = new Restaurant();
            r3.setName("Burger Barn");
            r3.setCuisine("American");
            r3.setAddress("Madhapur, Hyderabad");
            r3.setRating(4.0);
            r3.setDeliveryTime(25);
            r3.setDeliveryFee(19.0);
            r3.setOpen(false);
            r3 = restaurantRepository.save(r3);

            // ✅ Menu items for r1
            menuItemRepository.saveAll(List.of(
                    createItem("Chicken Biryani", "Dum cooked", 249.0, "Biryani", true, r1),
                    createItem("Mutton Biryani", "Tender meat", 329.0, "Biryani", true, r1),
                    createItem("Veg Biryani", "Veg mix", 179.0, "Biryani", true, r1),
                    createItem("Raita", "Curd", 49.0, "Sides", true, r1)
            ));

            // ✅ Menu items for r2
            menuItemRepository.saveAll(List.of(
                    createItem("Margherita", "Cheese pizza", 299.0, "Pizza", true, r2),
                    createItem("Pepperoni", "Spicy", 399.0, "Pizza", true, r2),
                    createItem("Garlic Bread", "Bread", 99.0, "Sides", true, r2)
            ));

            // ✅ Menu items for r3
            menuItemRepository.saveAll(List.of(
                    createItem("Classic Burger", "Beef", 199.0, "Burger", true, r3),
                    createItem("Crispy Chicken", "Fried", 229.0, "Burger", true, r3),
                    createItem("Fries", "Crispy", 79.0, "Sides", true, r3)
            ));

            System.out.println("Data seeded");
        }
    }

    // ✅ Helper method (clean code)
    private MenuItem createItem(String name, String desc, double price,
                                String category, boolean available, Restaurant r) {
        MenuItem item = new MenuItem();
        item.setName(name);
        item.setDescription(desc);
        item.setPrice(price);
        item.setCategory(category);
        item.setAvailable(available);
        item.setRestaurant(r);
        return item;
    }
}