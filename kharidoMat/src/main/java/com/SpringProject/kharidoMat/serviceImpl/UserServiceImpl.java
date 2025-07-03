package com.SpringProject.kharidoMat.serviceImpl;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.SpringProject.kharidoMat.model.Item;
import com.SpringProject.kharidoMat.model.User;
import com.SpringProject.kharidoMat.repository.ItemRepository;
import com.SpringProject.kharidoMat.repository.UserRepository;
import com.SpringProject.kharidoMat.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private ItemRepository itemRepository;

	@Override
	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	@Override
	public User getUserById(Long id) {
		return userRepository.findById(id).orElse(null);
	}

	@Override
	public User registerUser(User user) {
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		return userRepository.save(user);
	}

	@Override
	public void addToWishlist(String email, Long itemId) {
		// TODO Auto-generated method
		User user = userRepository.findByEmail(email);
		if (user == null) {
			throw new RuntimeException("User not found");
		}

		Optional<Item> optionalItem = itemRepository.findById(itemId);
		if (!optionalItem.isPresent()) {
			throw new RuntimeException("Item not found");
		}

		Item item = optionalItem.get();

		if (user.getWishlist() == null) {
			user.setWishlist(new HashSet<>());
		}

		boolean alreadyExist = false;
		for (Item i : user.getWishlist()) {
			if (i.getId().equals(item.getId())) {
				alreadyExist = true;
				break;
			}
		}

		if (!alreadyExist) {
			user.getWishlist().add(item);
			userRepository.save(user);
		}
	}

	@Override
	public void removeFromWishlist(String email, Long itemId) {
		// TODO Auto-generated method stub
		User user = userRepository.findByEmail(email);
		if (user == null) {
			throw new RuntimeException("User not found");
		}

		if (user.getWishlist() != null) {
			Item itemToRemove = null;
			for (Item i : user.getWishlist()) {
				if (i.getId().equals(itemId)) {
					itemToRemove = i;
					break;
				}
			}

			if (itemToRemove != null) {
				user.getWishlist().remove(itemToRemove);
				userRepository.save(user);
			}
		}
	}

	@Override
	public Set<Item> getWishlist(String email) {
		// TODO Auto-generated method stub
		User user = userRepository.findByEmail(email);

		if (user == null) {
			throw new RuntimeException("User not found");
		}
		return user.getWishlist();
	}

}
