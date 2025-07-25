package com.SpringProject.kharidoMat.serviceImpl;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.SpringProject.kharidoMat.enums.Role;
import com.SpringProject.kharidoMat.model.Item;
import com.SpringProject.kharidoMat.model.User;
import com.SpringProject.kharidoMat.repository.BookingRepository;
import com.SpringProject.kharidoMat.repository.ItemRepository;
import com.SpringProject.kharidoMat.repository.UserRepository;
import com.SpringProject.kharidoMat.service.OTPService;
import com.SpringProject.kharidoMat.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private ItemRepository itemRepository;

	@Autowired
	private BookingRepository bookingRepository;

	@Autowired
	private OTPService otpService;

	@Override
	public List<User> getAllUsers() {
		logger.info("Fetching all users");
		return userRepository.findAll();
	}

	@Override
	public User getUserById(Long id) {
		logger.info("Fetching user by ID: {}", id);
		return userRepository.findById(id).orElse(null);
	}

	@Override
	public void saveBasicUserInfo(User user) {
	    logger.info("Starting registration for: {}", user.getEmail());

	    if (userRepository.findByEmail(user.getEmail()) != null) {
	        throw new IllegalArgumentException("User already exists with email: " + user.getEmail());
	    }

	    // Save basic user info first
	    user.setVerified(false); // explicitly set
	    userRepository.save(user); // 💾 this was missing

	    // Then generate and send OTP
	    otpService.generateAndSendOTP(user.getEmail());

	    logger.info("User saved and OTP sent to email: {}", user.getEmail());
	}


	
	public void completeRegistration(String email, String password, String studentId) {
		User user = userRepository.findByEmail(email);
		if (user == null) {
			throw new IllegalArgumentException("User not found for email: " + email);
		}

		user.setPassword(passwordEncoder.encode(password));
		user.setStudentId(studentId);
		user.setVerified(true);
		user.setRole(Role.STUDENT);

		userRepository.save(user); // ✅ Update existing user instead of creating new
	}


	@Override
	public User registerUser(User user) {
		logger.info("Registering new user: {}", user.getEmail());

		if (user.getPassword() == null || user.getPassword().isBlank()) {
			throw new IllegalArgumentException("Password cannot be null or blank");
		}

		user.setPassword(passwordEncoder.encode(user.getPassword()));

		if (user.getRole() == null) {
			user.setRole(Role.STUDENT);
		}

		user.setVerified(false);

		User savedUser = userRepository.save(user);
		logger.info("User saved: {}", savedUser.getEmail());

		otpService.generateAndSendOTP(savedUser.getEmail());
		logger.info("OTP sent to email: {}", savedUser.getEmail());

		return savedUser;
	}

	@Override
	public void addToWishlist(String email, Long itemId) {
		logger.info("Adding item {} to wishlist of user {}", itemId, email);
		User user = userRepository.findByEmail(email);
		if (user == null) {
			logger.error("User not found: {}", email);
			throw new RuntimeException("User not found");
		}

		Optional<Item> optionalItem = itemRepository.findById(itemId);
		if (!optionalItem.isPresent()) {
			logger.error("Item not found: {}", itemId);
			throw new RuntimeException("Item not found");
		}

		Item item = optionalItem.get();
		if (user.getWishlist() == null) {
			user.setWishlist(new HashSet<>());
		}

		boolean alreadyExist = user.getWishlist().stream().anyMatch(i -> i.getId().equals(item.getId()));

		if (!alreadyExist) {
			user.getWishlist().add(item);
			userRepository.save(user);
			logger.info("Item {} added to wishlist for user {}", itemId, email);
		} else {
			logger.info("Item {} already exists in wishlist for user {}", itemId, email);
		}
	}

	@Override
	public void removeFromWishlist(String email, Long itemId) {
		logger.info("Removing item {} from wishlist of user {}", itemId, email);
		User user = userRepository.findByEmail(email);
		if (user == null) {
			logger.error("User not found: {}", email);
			throw new RuntimeException("User not found");
		}

		if (user.getWishlist() != null) {
			Item itemToRemove = user.getWishlist().stream().filter(i -> i.getId().equals(itemId)).findFirst()
					.orElse(null);

			if (itemToRemove != null) {
				user.getWishlist().remove(itemToRemove);
				userRepository.save(user);
				logger.info("Item {} removed from wishlist of user {}", itemId, email);
			} else {
				logger.warn("Item {} not found in wishlist of user {}", itemId, email);
			}
		}
	}

	@Override
	public Set<Item> getWishlist(String email) {
		logger.info("Fetching wishlist for user {}", email);
		User user = userRepository.findByEmail(email);
		if (user == null) {
			logger.error("User not found: {}", email);
			throw new RuntimeException("User not found");
		}
		return user.getWishlist();
	}


	@Override
	public boolean verifyEmail(String email, String otp) {
	    logger.info("Verifying OTP for email: {}", email);

	    if (!otpService.verifyOTP(email, otp)) {
	        logger.warn("OTP verification failed for email: {}", email);
	        return false;
	    }

	    User user = userRepository.findByEmail(email);
	    if (user == null) {
	        logger.error("No user found with email: {}", email);
	        return false; // or throw an exception if that's your design
	    }

	    user.setVerified(true);
	    userRepository.save(user);
	    logger.info("Email verified successfully for user: {}", email);
	    return true;
	}

}
