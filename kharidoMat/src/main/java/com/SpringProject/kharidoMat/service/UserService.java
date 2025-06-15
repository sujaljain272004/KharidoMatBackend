package com.SpringProject.kharidoMat.service;

import java.util.List;

import com.SpringProject.kharidoMat.model.User;

public interface UserService {
	  User registerUser(User user);
	    List<User> getAllUsers();
	    User getUserById(Long id);
}
