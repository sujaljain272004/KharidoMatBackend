package com.SpringProject.kharidoMat.service;

import com.SpringProject.kharidoMat.dto.DashboardStatsDTO;

public interface DashboardService {

    DashboardStatsDTO getDashboardStats(Long userId);
}
