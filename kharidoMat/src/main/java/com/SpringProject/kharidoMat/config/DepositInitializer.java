package com.SpringProject.kharidoMat.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.SpringProject.kharidoMat.model.Category;
import com.SpringProject.kharidoMat.repository.CategoryRepository;

@Component
public class DepositInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;

    public DepositInitializer(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void run(String... args) {
    	Map<String, Double> deposits = new HashMap<>();
        deposits.put("Electronics", 1500.0);
        deposits.put("Books", 100.0);
        deposits.put("Furniture", 1000.0);
        deposits.put("Hostel Essentials", 300.0);
        deposits.put("Clothing & Costumes", 200.0);
        deposits.put("Sports Equipment", 500.0);
        deposits.put("Bicycles", 1000.0);
        deposits.put("Event Decor", 300.0);
        deposits.put("Musical Instruments", 800.0);
        deposits.put("Lab Equipment", 1000.0);
        deposits.put("Mobile Accessories", 300.0);
        deposits.put("Kitchenware", 200.0);
        deposits.put("Stationery", 100.0);
        deposits.put("Others", 500.0);


        deposits.forEach((name, deposit) -> {
            Category category = categoryRepository.findByNameIgnoreCase(name)
                    .orElse(new Category(name, deposit));
            category.setBaseDeposit(deposit);
            categoryRepository.save(category);
        });
    }
}
