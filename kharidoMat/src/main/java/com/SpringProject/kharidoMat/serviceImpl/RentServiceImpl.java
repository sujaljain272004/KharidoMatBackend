package com.SpringProject.kharidoMat.serviceImpl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.SpringProject.kharidoMat.model.Rent;
import com.SpringProject.kharidoMat.repository.RentRepository;
import com.SpringProject.kharidoMat.service.RentService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class RentServiceImpl implements RentService {

    private static final Logger log = LoggerFactory.getLogger(RentServiceImpl.class);

    @Autowired
    private RentRepository rentRepository;

    @Override
    public boolean markAsReturned(Long rentId) {
        log.info("Attempting to mark rent ID {} as returned", rentId);

        Optional<Rent> optional = rentRepository.findById(rentId);
        if (optional.isPresent()) {
            Rent rent = optional.get();
            rent.setReturned(true);
            rentRepository.save(rent);

            log.info("Successfully marked rent ID {} as returned", rentId);
            return true;
        }

        log.warn("Failed to mark rent ID {} as returned - not found", rentId);
        return false;
    }
}
