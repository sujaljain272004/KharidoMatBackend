// =============== 3. ItemService.java (Corrected) ===============
// This file now correctly injects the BookingRepository.

package com.SpringProject.kharidoMat.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.SpringProject.kharidoMat.dto.ItemDetailResponseDTO;
import com.SpringProject.kharidoMat.dto.ItemWithBookingsDTO;
import com.SpringProject.kharidoMat.model.Booking;
import com.SpringProject.kharidoMat.model.Category;
import com.SpringProject.kharidoMat.model.Item;
import com.SpringProject.kharidoMat.model.Review;
import com.SpringProject.kharidoMat.model.User;
import com.SpringProject.kharidoMat.repository.BookingRepository; // Import BookingRepository
import com.SpringProject.kharidoMat.repository.CategoryRepository;
import com.SpringProject.kharidoMat.repository.ItemRepository;
import com.SpringProject.kharidoMat.repository.ReviewRepository;
import com.SpringProject.kharidoMat.repository.UserRepository;

@Service
public class ItemService {

    private static final Logger logger = LoggerFactory.getLogger(ItemService.class);

    @Autowired private ItemRepository itemRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ReviewRepository reviewRepository;
    @Autowired private BookingRepository bookingRepository; 
    @Autowired private CategoryRepository categoryRepository;


    public Item createItem(Item item, String email) {
        logger.info("Creating item for user: {}", email);
        User user = userRepository.findByEmail(email);
        item.setUser(user);

        Set<Category> resolvedCategories = new HashSet<>();
        if (item.getCategories() != null) {
            for (Category inputCategory : item.getCategories()) {
                String name = inputCategory.getName();
                Category existing = null;

                Optional<Category> optional = categoryRepository.findByNameIgnoreCase(name);
                if (optional.isPresent()) {
                    existing = optional.get();
                } else {
                    existing = new Category(name);
                    categoryRepository.save(existing);
                }
                resolvedCategories.add(existing);
            }
        }

        item.setCategories(resolvedCategories);
        Item savedItem = itemRepository.save(item);
        logger.info("Item created: {}", savedItem.getId());
        return savedItem;
    }


    public List<Item> getAllItems() {
        logger.info("Fetching all items");
        return itemRepository.findAll();
    }

    public List<Item> getItemsByCategory(String category) {
        logger.info("Fetching items by category: {}", category);
        return itemRepository.findByCategoryNameIgnoreCase(category);
    }

    public Item getItemById(Long id) {
        logger.info("Fetching item by ID: {}", id);
        return itemRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Item not found with ID: {}", id);
                    return new RuntimeException("Item not found");
                });
    }

    public Item saveItem(Item item) {
        logger.info("Saving item with ID: {}", item.getId());
        return itemRepository.save(item);
    }

    public List<Item> searchItems(String title, String category, Double minPrice, Double maxPrice, Boolean available) {
        logger.info("Searching items with filters - Title: {}, Category: {}, MinPrice: {}, MaxPrice: {}, Available: {}",
                title, category, minPrice, maxPrice, available);
        return itemRepository.searchItems(title, category, minPrice, maxPrice, available);
    }
    
    public List<Item> getItemsByUserEmail(String email) {
        logger.info("Getting items for user email: {}", email);
        User user = userRepository.findByEmail(email);
        return itemRepository.findByUser(user);
    }
    
    @Transactional
    public void deleteItem(Long itemId, String email) {
        logger.info("Attempting to delete item {} by user {}", itemId, email);
        
        Item item = getItemById(itemId);
        if (!item.getUser().getEmail().equals(email)) {
            throw new AccessDeniedException("You are not authorized to delete this item.");
        }
        
        // Delete associated Reviews
        List<Review> reviews = reviewRepository.findByItemId(itemId);
        if (!reviews.isEmpty()) {
            reviewRepository.deleteAllInBatch(reviews);
            logger.info("Deleted {} review(s) for item {}", reviews.size(), itemId);
        }

        // Delete associated Bookings
        List<Booking> bookings = bookingRepository.findAllByItemId(itemId);
        if (!bookings.isEmpty()) {
            bookingRepository.deleteAllInBatch(bookings);
            logger.info("Deleted {} booking(s) for item {}", bookings.size(), itemId);
        }
        
        List<User> usersWithItemInWishlist = userRepository.findByWishlist_Id(itemId);
        for (User user : usersWithItemInWishlist) {
            user.getWishlist().removeIf(itemInWishlist -> itemInWishlist.getId().equals(itemId));
        }
        
        itemRepository.delete(item);
        logger.info("Successfully deleted item with ID: {}", itemId);
    }
    
    public ItemDetailResponseDTO getItemDetails(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        Double averageRating = reviewRepository.findAverageRatingByItemId(itemId);
        Long totalReviews = reviewRepository.countReviewsByItemId(itemId);

        ItemDetailResponseDTO dto = new ItemDetailResponseDTO(item);
        dto.setAverageRating(averageRating == null ? 0.0 : averageRating);
        dto.setTotalReviews(totalReviews);

        return dto;
    }
    
    public List<ItemWithBookingsDTO> getItemsWithBookingsByUserEmail(String email) {
        User user = userRepository.findByEmail(email);
        List<Item> items = itemRepository.findByUser(user);
        List<ItemWithBookingsDTO> result = new java.util.ArrayList<>();

        for (Item item : items) {
            List<Booking> bookings = bookingRepository.findAllByItemId(item.getId());
            ItemWithBookingsDTO dto = new ItemWithBookingsDTO(item, bookings);
            result.add(dto);
        }

        return result;
    }
}