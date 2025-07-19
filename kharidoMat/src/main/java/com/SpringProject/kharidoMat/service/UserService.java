package com.SpringProject.kharidoMat.service;

import java.util.List;
import java.util.Set;

import com.SpringProject.kharidoMat.model.DashboardStats;
import com.SpringProject.kharidoMat.model.Item;
import com.SpringProject.kharidoMat.model.User;

public interface UserService {
	User registerUser(User user);

	List<User> getAllUsers();

	User getUserById(Long id);

	// wishList

	void addToWishlist(String email, Long itenId);

	void removeFromWishlist(String email, Long itemId);

	Set<Item> getWishlist(String email);

	DashboardStats getUserStats(Long userId, String role);

	boolean verifyEmail(String email, String otp);
	
	 User saveBasicUserInfo(User user);
	 
	 void completeRegistration(String email, String password, String studentId);
}
