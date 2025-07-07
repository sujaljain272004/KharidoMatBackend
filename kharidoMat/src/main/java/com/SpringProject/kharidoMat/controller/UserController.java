package com.SpringProject.kharidoMat.controller;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.SpringProject.kharidoMat.model.Booking;
import com.SpringProject.kharidoMat.model.LoginRequest;
import com.SpringProject.kharidoMat.model.User;
import com.SpringProject.kharidoMat.repository.UserRepository;
import com.SpringProject.kharidoMat.service.BookingService;
import com.SpringProject.kharidoMat.service.UserService;
import com.SpringProject.kharidoMat.util.JwtUtil;

@RestController
@RequestMapping("/api/users")
public class UserController {
	@Autowired
	private UserService userService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private JwtUtil jwtUtil;

	@PostMapping("/register")
	public User registerUser(@RequestBody User user) {
		return userService.registerUser(user);
	}

	@GetMapping("/")
	public List<User> getAllUsers() {
		return userService.getAllUsers();
	}

	@GetMapping("/{id}")
	public User getUser(@PathVariable Long id) {
		return userService.getUserById(id);
	}

	@PostMapping("/login")
	public ResponseEntity<String> login(@RequestBody LoginRequest request) {
		User user = userRepository.findByEmail(request.getEmail());
		if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Credentials");
		}

		String token = jwtUtil.generateToken(user.getEmail());

		return ResponseEntity.ok(token);
	}

	@GetMapping("/test")
	public ResponseEntity<String> testProtectedApi() {
		return ResponseEntity.ok("You are authenticated! 🎉");
	}

	// WishList

	@PostMapping("/wishlist/add/{email}/{itemId}")
	public ResponseEntity<String> addToWishlist(@PathVariable String email, @PathVariable Long itemId) {
		userService.addToWishlist(email, itemId);
		return ResponseEntity.ok("Item added to wishlist.");
	}

	@PostMapping("/wishlist/remove/{email}/{itemId}")
	public ResponseEntity<String> removeFromWishlist(@PathVariable String email, @PathVariable Long itemId) {
		userService.removeFromWishlist(email, itemId);
		return ResponseEntity.ok("Item removed from wishlist.");
	}

	@GetMapping("/wishlist/{email}")
	public ResponseEntity<Set<?>> getWishlist(@PathVariable String email) {
		return ResponseEntity.ok(userService.getWishlist(email));
	}

}
