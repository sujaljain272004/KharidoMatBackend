// In a new package, e.g., com.yourproject.tasks
package com.SpringProject.kharidoMat.tasks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.SpringProject.kharidoMat.model.User;
import com.SpringProject.kharidoMat.repository.UserRepository;
import com.SpringProject.kharidoMat.service.EmailService;
import com.SpringProject.kharidoMat.service.ReportService;

import java.time.YearMonth;
import java.util.List;

@Component
public class ScheduledTasks {

    @Autowired
    private UserRepository userRepository; // To get all users

    @Autowired
    private ReportService reportService;

    @Autowired
    private EmailService emailService;
    
    // This runs at 1 AM on the last day of every month
    @Scheduled(cron = "0 0 1 L * ?") 
    public void sendMonthlyReports() {
        YearMonth lastMonth = YearMonth.now().minusMonths(1);
        
        // Find all users who own at least one item (you can optimize this query)
        List<User> owners = userRepository.findAll(); // Or a custom query to find only owners

        for (User owner : owners) {
            try {
                byte[] report = reportService.generateMonthlyReportForOwner(owner, lastMonth);
                if (report.length > 0) { // Only send if there's data
                    emailService.sendMonthlyReportEmail(owner, lastMonth, report);
                }
            } catch (Exception e) {
                // Log error for this specific user
            }
        }
    }
}