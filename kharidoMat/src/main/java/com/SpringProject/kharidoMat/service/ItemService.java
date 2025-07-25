package com.SpringProject.kharidoMat.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.SpringProject.kharidoMat.dto.ItemDTO;
import com.SpringProject.kharidoMat.dto.ItemDetailResponseDTO;
import com.SpringProject.kharidoMat.dto.UserDTO;
import com.SpringProject.kharidoMat.model.Item;
import com.SpringProject.kharidoMat.model.User;
import com.SpringProject.kharidoMat.repository.ItemRepository;
import com.SpringProject.kharidoMat.repository.ReviewRepository;
import com.SpringProject.kharidoMat.repository.UserRepository;

@Service
public class ItemService {

    private static final Logger logger = LoggerFactory.getLogger(ItemService.class);

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ReviewRepository reviewRepository;

    public Item createItem(Item item, String email) {
        logger.info("Creating item for user: {}", email);
        User user = userRepository.findByEmail(email);
        item.setUser(user);
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
        return itemRepository.findByCategoryIgnoreCase(category);
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
    
    public ItemDetailResponseDTO getItemDetails(Long itemId) {
        // 1. Fetch the core item entity
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        // 2. Fetch the calculated review data
        Double averageRating = reviewRepository.findAverageRatingByItemId(itemId);
        Long totalReviews = reviewRepository.countReviewsByItemId(itemId);

        // 3. Create and populate the DTO
        ItemDetailResponseDTO dto = new ItemDetailResponseDTO(item);
        dto.setId(item.getId());
        dto.setTitle(item.getTitle());
        dto.setPricePerDay(item.getPricePerDay());
        dto.setImageName(item.getImageName());
        
        // Populate owner details if they exist
        if (item.getUser() != null) {
            UserDTO ownerDto = new UserDTO();
            ownerDto.setId(item.getUser().getId());
            ownerDto.setFullName(item.getUser().getFullName());
            ownerDto.setEmail(item.getUser().getEmail());
            // Map other owner fields as needed
            
        }

        // 4. Set the new review fields
        // Handle case where there are no reviews (averageRating could be null)
        dto.setAverageRating(averageRating == null ? 0.0 : averageRating);
        dto.setTotalReviews(totalReviews);

        return dto;
    }

}
