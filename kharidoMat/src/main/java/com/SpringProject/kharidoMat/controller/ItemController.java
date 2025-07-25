package com.SpringProject.kharidoMat.controller;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.SpringProject.kharidoMat.dto.ItemDetailResponseDTO;
import com.SpringProject.kharidoMat.model.Item;
import com.SpringProject.kharidoMat.service.ItemService;
import com.SpringProject.kharidoMat.util.FileUploadUtil;
import com.SpringProject.kharidoMat.util.JwtUtil;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    private static final Logger logger = LoggerFactory.getLogger(ItemController.class);

    @Autowired
    private ItemService itemService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/post")
    public ResponseEntity<?> postItem(@RequestBody Item item, @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        String email = jwtUtil.extractEmail(token);

        logger.info("Posting new item by user '{}': {}", email, item.getTitle());
        Item savedItem = itemService.createItem(item, email);

        return ResponseEntity.ok(savedItem);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllItems() {
        logger.info("Fetching all items");
        List<Item> items = itemService.getAllItems();
        return ResponseEntity.ok(items);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<?> getItemsByCategory(@PathVariable String category) {
        logger.info("Fetching items for category '{}'", category);
        List<Item> items = itemService.getItemsByCategory(category);
        return ResponseEntity.ok(items);
    }

    @PostMapping("/upload-image/{itemId}")
    public ResponseEntity<?> uploadItemImage(@PathVariable Long itemId, @RequestParam("file") MultipartFile file) {
        logger.info("Uploading image for item ID {}", itemId);
        try {
            Item item = itemService.getItemById(itemId);
            if (item == null) {
                logger.error("Item not found with ID: {}", itemId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Item not found.");
            }

            String fileName = file.getOriginalFilename();
            logger.info("Original filename: {}", fileName);

            FileUploadUtil.saveFile("images", fileName, file);

            item.setImageName(fileName);
            logger.info("Image name set to item: {}", fileName);

            itemService.saveItem(item);
            logger.info("Item updated in DB with image name.");

            Map<String, String> response = new HashMap<>();
            response.put("message", "Image uploaded successfully");
            response.put("imageName", fileName);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Image upload failed for item ID {}: {}", itemId, e.getMessage());
            return ResponseEntity.status(500).body("Image upload failed: " + e.getMessage());
        }
    }



    @GetMapping("/image/{fileName}")
    public ResponseEntity<?> getImage(@PathVariable String fileName) throws IOException {
        File file = new File("images/" + fileName);
        if (!file.exists()) {
            logger.warn("Image '{}' not found", fileName);
            return ResponseEntity.notFound().build();
        }

        byte[] image = java.nio.file.Files.readAllBytes(file.toPath());
        logger.info("Returning image '{}'", fileName);

        return ResponseEntity
                .ok()
                .header("Content-Type", "image/jpeg")
                .body(image);
    }

    @GetMapping("/search")
    public List<Item> searchItems(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Boolean available
    ) {
        logger.info("Searching items with filters - title: {}, category: {}, price: {}-{}, available: {}",
                title, category, minPrice, maxPrice, available);
        return itemService.searchItems(title, category, minPrice, maxPrice, available);
    }
    
    @GetMapping("/my")
    public ResponseEntity<?> getMyItems(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        String email = jwtUtil.extractEmail(token);

        logger.info("Fetching items posted by user '{}'", email);
        List<Item> items = itemService.getItemsByUserEmail(email);
        return ResponseEntity.ok(items);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getItemById(@PathVariable Long id) {
        logger.info("Fetching item with ID {}", id);
        try {
            Item item = itemService.getItemById(id);
            
            // --- MODIFIED PART ---
            // Instead of returning the raw item, create and return the DTO
            ItemDetailResponseDTO responseDTO = new ItemDetailResponseDTO(item);
            return ResponseEntity.ok(responseDTO);
            
        } catch (Exception e) {
            logger.error("Failed to fetch item with ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(404).body("Item not found");
        }
    }


}
