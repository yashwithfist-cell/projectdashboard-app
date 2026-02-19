package com.pmtool.backend.services;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;

import com.pmtool.backend.DTO.ChartDataDTO;
import com.pmtool.backend.DTO.DashboardStatsDTO;
import com.pmtool.backend.DTO.response.TimeLineResponseDto;

public interface DashboardService {
	public DashboardStatsDTO getStats();

	public List<ChartDataDTO> getHoursByProject();

	public List<ChartDataDTO> getHoursByEmployee();

	public Map<String, Map<String, Long>> getHoursByEmployeeAndProject();

	public List<TimeLineResponseDto> getMonthlyHoursByEmployeeAndProject(YearMonth month);
}
