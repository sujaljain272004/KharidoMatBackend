package com.SpringProject.kharidoMat.serviceImpl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.SpringProject.kharidoMat.model.Rent;
import com.SpringProject.kharidoMat.repository.RentRepository;
import com.SpringProject.kharidoMat.service.RentService;

public class RentServiceImpl implements RentService{
	
	
	@Autowired
	private RentRepository rentRepository;
	
	@Override
	public boolean markAsReturned(Long rentId) {
		Optional<Rent> optional = rentRepository.findById(rentId);
		if(optional.isPresent()) {
			Rent rent = optional.get();
			rent.setReturned(true);
			rentRepository.save(rent);
			return true;
		}
		
		return false;
	}

}
