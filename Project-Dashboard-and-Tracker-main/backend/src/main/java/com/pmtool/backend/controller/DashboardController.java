package com.pmtool.backend.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pmtool.backend.DTO.ChartDataDTO;
import com.pmtool.backend.DTO.DashboardStatsDTO;
import com.pmtool.backend.services.DashboardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {
	 private final DashboardService dashboardService;

	    @GetMapping("/stats")
	    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','HUMAN_RESOURCE','SUPER_ADMIN')")
	    public DashboardStatsDTO getStats() {
	        return dashboardService.getStats();
	    }

	    @GetMapping("/hours-by-project")
	    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','HUMAN_RESOURCE','SUPER_ADMIN')")
	    public List<ChartDataDTO> getHoursByProject() {
	        return dashboardService.getHoursByProject();
	    }

	    @GetMapping("/hours-by-employee")
	    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','HUMAN_RESOURCE','SUPER_ADMIN')")
	    public List<ChartDataDTO> getHoursByEmployee() {
	        return dashboardService.getHoursByEmployee();
	    }
}
