package com.SpringProject.kharidoMat.controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.SpringProject.kharidoMat.dto.EditProfileRequest;
import com.SpringProject.kharidoMat.dto.UserDTO;
import com.SpringProject.kharidoMat.model.*;
import com.SpringProject.kharidoMat.repository.UserRepository;
import com.SpringProject.kharidoMat.service.OTPService;
import com.SpringProject.kharidoMat.service.UserService;
import com.SpringProject.kharidoMat.util.JwtUtil;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

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

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        log.info("Starting registration for: {}", user.getEmail());
        userService.saveBasicUserInfo(user);
        return ResponseEntity.ok("OTP sent to email.");
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verify(@RequestBody VerificationRequest req) {
        boolean verified = userService.verifyEmail(req.getEmail(), req.getOtp());

        if (verified) {
            return ResponseEntity.ok("OTP verified. Proceed to set password.");
        } else {
            return ResponseEntity.status(400).body("Invalid or expired OTP");
        }

    }
    
    @PostMapping("/complete-registration")
    public ResponseEntity<String> complete(@RequestBody CompleteRegistrationRequest req) {
        userService.completeRegistration(req.getEmail(), req.getPassword(), req.getStudentId());
        return ResponseEntity.ok("Registration completed!");
    }




    @GetMapping("/")
    public List<User> getAllUsers() {
        log.info("Fetching all users");
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        log.info("Fetching user with ID: {}", id.toString());
        return userService.getUserById(id);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        log.info("Login attempt for {}", request.getEmail());
        User user = userRepository.findByEmail(request.getEmail());
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Login failed: Invalid credentials for {}", request.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Credentials");
        }

        if (!user.isVerified()) {
            log.warn("Login failed: Unverified account for {}", request.getEmail());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Please verify your email before logging in.");
        }

        String token = jwtUtil.generateToken(user.getEmail());
        log.info("Login successful for {}", request.getEmail());
        return ResponseEntity.ok(token);
    }

    @GetMapping("/test")
    public ResponseEntity<String> testProtectedApi() {
        log.info("Protected test endpoint accessed");
        return ResponseEntity.ok("You are authenticated! 🎉");
    }

    @PostMapping("/wishlist/add/{email}/{itemId}")
    public ResponseEntity<String> addToWishlist(@PathVariable String email, @PathVariable Long itemId) {
        log.info("Adding item {} to wishlist of {}", itemId.toString(), email);
        userService.addToWishlist(email, itemId);
        return ResponseEntity.ok("Item added to wishlist.");
    }

    @PostMapping("/wishlist/remove/{email}/{itemId}")
    public ResponseEntity<String> removeFromWishlist(@PathVariable String email, @PathVariable Long itemId) {
        log.info("Removing item {} from wishlist of {}", itemId.toString(), email);
        userService.removeFromWishlist(email, itemId);
        return ResponseEntity.ok("Item removed from wishlist.");
    }

    @GetMapping("/wishlist/{email}")
    public ResponseEntity<Set<?>> getWishlist(@PathVariable String email) {
        log.info("Fetching wishlist for {}", email);
        return ResponseEntity.ok(userService.getWishlist(email));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardStats> getDashboardStats(Authentication auth) {
        String email = auth.getName();
        log.info("Fetching dashboard stats for {}", email);
        User user = userRepository.findByEmail(email);
        DashboardStats stats = userService.getUserStats(user.getId(), user.getRole().name());
        return ResponseEntity.ok(stats);
    }

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
    
    @PutMapping("/users/edit-profile")
    public ResponseEntity<?> editProfile(@RequestBody EditProfileRequest request, Principal principal) {
        User user = userRepository.findByEmail(principal.getName());
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setPrn(request.getPrn());
        user.setAcademicYear(request.getAcademicYear());

        userRepository.save(user);

        return ResponseEntity.ok("Profile updated successfully");
    }
    
    @GetMapping("/profile")
    public ResponseEntity<UserDTO> getUserProfile(Authentication authentication) {
        // Spring Security's Authentication object holds the user's email (principal)
        String email = authentication.getName();
        
        // Find the user in the database
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
        
        // Convert the User entity to a safe DTO and return it
        UserDTO userDTO = new UserDTO(user);
        
        return ResponseEntity.ok(userDTO);
    }
}
