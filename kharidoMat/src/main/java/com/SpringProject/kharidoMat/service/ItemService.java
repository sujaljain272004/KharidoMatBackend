package com.SpringProject.kharidoMat.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.SpringProject.kharidoMat.model.Item;
import com.SpringProject.kharidoMat.model.User;
import com.SpringProject.kharidoMat.repository.ItemRepository;
import com.SpringProject.kharidoMat.repository.UserRepository;

@Service
public class ItemService {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    public Item createItem(Item item, String email) {
        User user = userRepository.findByEmail(email);
        item.setUser(user); // connect item to user
        return itemRepository.save(item);
    }
    
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }
    
    public List<Item> getItemsByCategory(String category) {
        return itemRepository.findByCategoryIgnoreCase(category);
    }
    
    public Item getItemById(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));
    }

    public Item saveItem(Item item) {
        return itemRepository.save(item);
    }


}
