package com.SpringProject.kharidoMat.serviceImpl;

import com.SpringProject.kharidoMat.dto.DashboardStatsDTO;
import com.SpringProject.kharidoMat.model.User;
import com.SpringProject.kharidoMat.repository.BookingRepository;
import com.SpringProject.kharidoMat.repository.ItemRepository;
import com.SpringProject.kharidoMat.repository.UserRepository;
import com.SpringProject.kharidoMat.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DashboardServiceImpl implements DashboardService {

@Autowired
private BookingRepository bookingRepository;

 @Autowired
 private ItemRepository itemRepository;

@Autowired
private UserRepository userRepository;

@Override
public DashboardStatsDTO getDashboardStats(Long userId) {
	// TODO Auto-generated method stub
int totalBookings = bookingRepository.countByUserId(userId);
double totalSpent = bookingRepository.sumAmountSpentByUserId(userId);
int totalListings = itemRepository.countByOwnerId(userId);
double totalEarned = bookingRepository.sumAmountEarnedByOwnerId(userId);

User user = userRepository.findById(userId).orElse(null);
int wishlistCount = (user != null && user.getWishlist() != null) ? user.getWishlist().size() : 0;

return new DashboardStatsDTO(totalBookings, totalSpent, totalListings, totalEarned, wishlistCount);
}

}
