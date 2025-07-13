package com.SpringProject.kharidoMat.controller;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
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

import com.SpringProject.kharidoMat.model.DashboardStats;
import com.SpringProject.kharidoMat.model.LoginRequest;
import com.SpringProject.kharidoMat.model.ResetPasswordRequest;
import com.SpringProject.kharidoMat.model.User;
import com.SpringProject.kharidoMat.model.VerificationRequest;
import com.SpringProject.kharidoMat.repository.UserRepository;
import com.SpringProject.kharidoMat.service.OTPService;
import com.SpringProject.kharidoMat.service.UserService;
import com.SpringProject.kharidoMat.util.JwtUtil;

@RestController
@RequestMapping("/api/users")
public class UserController {
	
	@Autowired
	private OTPService otpService;
	
	@Autowired
	private UserService userService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private JwtUtil jwtUtil;
	
	private static final Logger log = LoggerFactory.getLogger(UserController.class);

	@PostMapping("/register")
	public ResponseEntity<String> register(@RequestBody User user) {
		userService.registerUser(user);
		return ResponseEntity.ok("Registered. OTP sent to e‑mail.");
	}

	/* Verifying OTP */
	@PostMapping("/verify")
	public ResponseEntity<String> verify(@RequestBody VerificationRequest req) {
		boolean ok = userService.verifyEmail(req.getEmail(), req.getOtp());
		return ok ? ResponseEntity.ok("Email verified successfully!")
				: ResponseEntity.status(400).body("Invalid / expired OTP");
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

		if (!user.isVerified()) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Please verify your email before logging in.");
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

	@GetMapping("/dashboard")
	public ResponseEntity<DashboardStats> getDashboardStats(Authentication auth) {
		User user = userRepository.findByEmail(auth.getName());
		DashboardStats stats = userService.getUserStats(user.getId(), user.getRole().name());
		return ResponseEntity.ok(stats);
	}
	
	//forget password 
	@PostMapping("/forgot-password")
	public ResponseEntity<String> forgotPassword(@RequestBody Map<String, String> request) {
	    String email = request.get("email");
	    User user = userRepository.findByEmail(email);
	    
	    if (user == null) {
	        log.warn("Forgot password: User not found for {}", email);
	        return ResponseEntity.status(404).body("User not found");
	    }

	    otpService.generateAndSendOTP(email);
	    log.info("Forgot password: OTP sent to {}", email);
	    return ResponseEntity.ok("OTP sent to your email");
	}

	@PostMapping("/reset-password")
	public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
	    log.info("Reset password attempt for {}", request.getEmail());

	    boolean valid = otpService.verifyOTP(request.getEmail(), request.getOtp());
	    if (!valid) {
	        log.warn("Reset password failed: Invalid/Expired OTP for {}", request.getEmail());
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired OTP");
	    }

	    User user = userRepository.findByEmail(request.getEmail());
	    if (user == null) {
	        log.warn("Reset password failed: User not found for {}", request.getEmail());
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
	    }

	    user.setPassword(passwordEncoder.encode(request.getNewPassword()));
	    userRepository.save(user);

	    log.info("Password reset successful for {}", request.getEmail());
	    return ResponseEntity.ok("Password reset successful");
	}


}
