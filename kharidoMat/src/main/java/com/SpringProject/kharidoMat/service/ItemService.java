package com.SpringProject.kharidoMat.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.SpringProject.kharidoMat.model.Item;
import com.SpringProject.kharidoMat.model.User;
import com.SpringProject.kharidoMat.repository.ItemRepository;
import com.SpringProject.kharidoMat.repository.UserRepository;

@Service
public class ItemService {

    private static final Logger logger = LoggerFactory.getLogger(ItemService.class);

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

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
}
