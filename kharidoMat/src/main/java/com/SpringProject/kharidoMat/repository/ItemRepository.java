package com.SpringProject.kharidoMat.repository;




import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.SpringProject.kharidoMat.model.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {

	List<Item> findByCategoryIgnoreCase(String category);

}