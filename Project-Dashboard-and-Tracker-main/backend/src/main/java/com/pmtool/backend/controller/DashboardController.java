package com.pmtool.backend.controller;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pmtool.backend.DTO.ChartDataDTO;
import com.pmtool.backend.DTO.DashboardStatsDTO;
import com.pmtool.backend.DTO.response.ApiResponse;
import com.pmtool.backend.DTO.response.TimeLineResponseDto;
import com.pmtool.backend.services.DashboardService;

import jakarta.servlet.http.HttpServletRequest;
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

	@GetMapping("/hours-by-employee-projects")
	@PreAuthorize("hasAnyRole('SYSTEM_ADMIN','HUMAN_RESOURCE','SUPER_ADMIN')")
	public ResponseEntity<ApiResponse> getHoursByEmployeeAndProject(HttpServletRequest request) {
		Map<String, Map<String, Long>> employeeProjectsMap = dashboardService.getHoursByEmployeeAndProject();
		return ResponseEntity.ok(ApiResponse.success(employeeProjectsMap,
				"Projects by employee Map Fetched Successfully", request.getRequestURI()));
	}

	@GetMapping("/monthly-hours-by-employee-projects")
	@PreAuthorize("hasAnyRole('SYSTEM_ADMIN','HUMAN_RESOURCE','SUPER_ADMIN')")
	public ResponseEntity<ApiResponse> getMonthlyHoursByEmployeeAndProject(
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth month, HttpServletRequest request) {
		List<TimeLineResponseDto> monthlyHoursList = dashboardService.getMonthlyHoursByEmployeeAndProject(month);
		return ResponseEntity.ok(ApiResponse.success(monthlyHoursList, "Monthly Work Hors Fetched Successfully",
				request.getRequestURI()));
	}
}
