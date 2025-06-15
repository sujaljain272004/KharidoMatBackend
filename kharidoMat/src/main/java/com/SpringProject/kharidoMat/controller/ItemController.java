package com.SpringProject.kharidoMat.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.SpringProject.kharidoMat.model.Item;
import com.SpringProject.kharidoMat.service.ItemService;
import com.SpringProject.kharidoMat.util.FileUploadUtil;
import com.SpringProject.kharidoMat.util.JwtUtil;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    @Autowired
    private ItemService itemService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/post")
    public ResponseEntity<?> postItem(@RequestBody Item item, @RequestHeader("Authorization") String authHeader) {
        // Extract token
        String token = authHeader.substring(7);
        String email = jwtUtil.extractEmail(token);

        Item savedItem = itemService.createItem(item, email);

        return ResponseEntity.ok(savedItem);
    }
    
    @GetMapping("/all")
    public ResponseEntity<?> getAllItems() {
        List<Item> items = itemService.getAllItems();
        return ResponseEntity.ok(items);
    }
    
    @GetMapping("/category/{category}")
    public ResponseEntity<?> getItemsByCategory(@PathVariable String category) {
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
            itemService.saveItem(item); // save updated item
            return ResponseEntity.ok("Image uploaded successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Image upload failed: " + e.getMessage());
        }
    }
    
    @GetMapping("/image/{fileName}")
    public ResponseEntity<?> getImage(@PathVariable String fileName) throws IOException {
        File file = new File("images/" + fileName);
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        byte[] image = java.nio.file.Files.readAllBytes(file.toPath());

        return ResponseEntity
                .ok()
                .header("Content-Type", "image/jpeg")
                .body(image);
    }

}
