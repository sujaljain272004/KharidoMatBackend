package com.SpringProject.kharidoMat.controller;

import com.SpringProject.kharidoMat.dto.DashboardStatsDTO;
import com.SpringProject.kharidoMat.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

	@Autowired
	private DashboardService dashboardService;

	@GetMapping("/stats/{userId}")
	public ResponseEntity<DashboardStatsDTO> getStats(@PathVariable Long userId) {
		DashboardStatsDTO stats = dashboardService.getDashboardStats(userId);
		return ResponseEntity.ok(stats);
	}
}
