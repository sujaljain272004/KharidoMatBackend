package com.SpringProject.kharidoMat.controller;

import com.SpringProject.kharidoMat.dto.ItemDetailResponseDTO;
import com.SpringProject.kharidoMat.model.Item;
import com.SpringProject.kharidoMat.service.ItemService;
import com.SpringProject.kharidoMat.util.FileUploadUtil;
import com.SpringProject.kharidoMat.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        Item savedItem = itemService.createItem(item, email);
        return ResponseEntity.ok(savedItem);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Item>> getAllItems() {
        List<Item> items = itemService.getAllItems();
        return ResponseEntity.ok(items);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<Item>> getItemsByCategory(@PathVariable String category) {
        List<Item> items = itemService.getItemsByCategory(category);
        return ResponseEntity.ok(items);
    }

    @PostMapping("/upload-image/{itemId}")
    public ResponseEntity<?> uploadItemImage(@PathVariable Long itemId, @RequestParam("file") MultipartFile file) {
        try {
            Item item = itemService.getItemById(itemId);
            String fileName = file.getOriginalFilename();
            FileUploadUtil.saveFile("images", fileName, file);
            item.setImageName(fileName);
            itemService.saveItem(item);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Image uploaded successfully");
            response.put("imageName", fileName);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Image upload failed: " + e.getMessage());
        }
    }

    @GetMapping("/image/{fileName}")
    public ResponseEntity<?> getImage(@PathVariable String fileName) throws IOException {
        File file = new File("images/" + fileName);
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }
        byte[] image = java.nio.file.Files.readAllBytes(file.toPath());
        return ResponseEntity.ok().header("Content-Type", "image/jpeg").body(image);
    }

    @GetMapping("/search")
    public List<Item> searchItems(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Boolean available) {
        return itemService.searchItems(title, category, minPrice, maxPrice, available);
    }

    @GetMapping("/my")
    public ResponseEntity<List<Item>> getMyItems(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        String email = jwtUtil.extractEmail(token);
        List<Item> items = itemService.getItemsByUserEmail(email);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getItemById(@PathVariable Long id) {
        try {
            Item item = itemService.getItemById(id);
            ItemDetailResponseDTO responseDTO = new ItemDetailResponseDTO(item);
            return ResponseEntity.ok(responseDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteItem(@PathVariable Long id, @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        String email = jwtUtil.extractEmail(token);
        try {
            itemService.deleteItem(id, email);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Item deleted successfully.");
            return ResponseEntity.ok(response);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}