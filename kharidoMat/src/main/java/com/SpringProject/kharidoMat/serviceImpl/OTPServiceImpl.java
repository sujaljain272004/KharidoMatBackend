package com.SpringProject.kharidoMat.serviceImpl;

import java.time.Instant;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.SpringProject.kharidoMat.service.EmailService;
import com.SpringProject.kharidoMat.service.OTPService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class OTPServiceImpl implements OTPService {

    /*  email  ->  [otp , expiryEpochSeconds]  */
    private Map<String, String[]> otpCache = new ConcurrentHashMap<>();
    private Random random = new Random();

    @Autowired
    private EmailService emailService;

    private static final Logger log = LoggerFactory.getLogger(OTPServiceImpl.class);

    @Override
    public void generateAndSendOTP(String email) {
        String otp = String.format("%06d", random.nextInt(1_000_000));
        long expires = Instant.now().plusSeconds(300).getEpochSecond();
        otpCache.put(email, new String[]{otp, String.valueOf(expires)});

        log.info("Generated OTP {} for {} (expires in 5 min)", otp, email);

        String body =
                "Your CampusRent login OTP is: " + otp +
                "\nThis code is valid for 5 minutes.";
        emailService.sendEmail(email, "CampusRent Login OTP", body);
    }

    @Override
    public boolean verifyOTP(String email, String userOtp) {
        if (!email.contains("@")) {
            log.error("verifyOTP called with invalid email: '{}'. Likely email and OTP are swapped.", email);
            return false;
        }

        log.info("Verifying OTP - Input email: {}, OTP: {}", email, userOtp);

        if (!otpCache.containsKey(email)) {
            log.warn("OTP verify failed: no OTP cached for {}", email);
            return false;
        }

        String[] data = otpCache.get(email);
        String cachedOtp = data[0];
        long expiryTime = Long.parseLong(data[1]);
        long currentTime = Instant.now().getEpochSecond();

        boolean isValid = cachedOtp.equals(userOtp) && currentTime < expiryTime;

        if (isValid) {
            otpCache.remove(email);
            log.info("OTP verified successfully for {}", email);
        } else {
            log.warn("OTP verification failed for {}. Incorrect or expired OTP.", email);
        }

        return isValid;
    }

}
