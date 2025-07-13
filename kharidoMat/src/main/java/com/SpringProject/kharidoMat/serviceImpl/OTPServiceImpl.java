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
public class OTPServiceImpl implements OTPService{
	
	 /*  email  ->  [otp , expiryEpochSeconds]  */
	
	private Map<String, String[]> otpCache = new ConcurrentHashMap<String, String[]>();
	private Random random = new Random();
	
	@Autowired
	private EmailService emailService;
	
	private static final Logger log = LoggerFactory.getLogger(OTPServiceImpl.class);
	
	
	@Override
	public void generateAndSendOTP(String email) {
		String otp = String.format("%06d", random.nextInt(1_000_000));
		long expires = Instant.now().plusSeconds(300).getEpochSecond();
		otpCache.put(email, new String[]{otp, String.valueOf(expires)});
		
		log.info("Generated OTP {} for {} (expires in 5 min)", otp, email);
		
		String body =
	            "Your CampusRent login OTP is: " + otp +
	            "\nThis code is valid for 5 minutes.";
	        emailService.sendEmail(email, "CampusRent Login OTP", body);
	}

	@Override
	public boolean verifyOTP(String email, String OTP){
		if(!otpCache.containsKey(email)) {
			log.warn("OTP verify failed: no OTP cached for {}", email);
			return false;
		}
		String[] data = otpCache.get(email);
		boolean valid = data[0].equals(OTP) && Instant.now().getEpochSecond() < Long.parseLong(data[1]);
		log.info("Verifying OTP for {}  |  expected={}  provided={}  expired?={}",
                email, data[0], OTP, Instant.now().getEpochSecond() < Long.parseLong(data[1]));
		if (valid) otpCache.remove(email);          // one time use
        return valid;
	}

}
