package com.pmtool.backend.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Service;

import com.pmtool.backend.DTO.ChartDataDTO;
import com.pmtool.backend.DTO.DashboardStatsDTO;
import com.pmtool.backend.DTO.HoursDTO;
import com.pmtool.backend.DTO.response.TimeLineResponseDto;
import com.pmtool.backend.entity.TimeLineSummary;
import com.pmtool.backend.repository.DepartmentRepository;
import com.pmtool.backend.repository.EmployeeRepository;
import com.pmtool.backend.repository.MilestoneRepository;
import com.pmtool.backend.repository.ProjectRepository;
import com.pmtool.backend.repository.TimLineRepository;
import com.pmtool.backend.repository.WorkLogEntryRepository;
import com.pmtool.backend.util.TimeUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

	private final ProjectRepository projectRepository;
	private final EmployeeRepository employeeRepository;
	private final DepartmentRepository departmentRepository;
	private final MilestoneRepository milestoneRepository;
	private final TimLineRepository timLineRepository;

	@Override
	public DashboardStatsDTO getStats() {
		return new DashboardStatsDTO(projectRepository.count(), employeeRepository.count(),
				departmentRepository.count(), milestoneRepository.count());
	}

	@Override
	public List<ChartDataDTO> getHoursByProject() {
		List<TimeLineResponseDto> timeLineSummaryList = timLineRepository.findHoursByProject();
		List<ChartDataDTO> chartDataList = timeLineSummaryList.stream()
				.map(timeline -> ChartDataDTO.builder().name(timeline.getName())
						.value(TimeUtil.formatDurationToDouble(timeline.getDurationMillis()))
						.estimatedHours(
								timeline.getEstimatedHours() == null ? BigDecimal.ZERO : timeline.getEstimatedHours())
						.build())
				.toList();
		return chartDataList;
	}

	@Override
	public List<ChartDataDTO> getHoursByEmployee() {
//		return workLogEntryRepository.findHoursPerEmployee();
		List<TimeLineResponseDto> timeLineSummaryList = timLineRepository.findHoursByEmployee();
		List<ChartDataDTO> chartDataList = timeLineSummaryList.stream()
				.map(timeline -> ChartDataDTO.builder().name(timeline.getName())
						.value(TimeUtil.formatDurationToDouble(timeline.getDurationMillis()))
						.employeeId(timeline.getEmployeeId()).build())
				.toList();
		return chartDataList;

	}

	@Override
	public Map<String, Map<String, Long>> getHoursByEmployeeAndProject() {
		List<TimeLineResponseDto> timeLineList = timLineRepository.findHoursByEmployeeAndProject();
		Map<String, Map<String, Long>> employeeProjectsMap = timeLineList.stream()
				.collect(Collectors.groupingBy(row -> row.getEmployeeId() + " - " + row.getEmpName(),
						LinkedHashMap::new, Collectors.toMap(TimeLineResponseDto::getProjectName,
								TimeLineResponseDto::getDurationMillis, Long::sum, LinkedHashMap::new)));
		return employeeProjectsMap;
	}

	@Override
	public List<TimeLineResponseDto> getMonthlyHoursByEmployeeAndProject(YearMonth month) {
		LocalDate start = month.atDay(1);
		LocalDate end = month.atEndOfMonth();
		List<TimeLineResponseDto> monthlyProjectHoursList = timLineRepository.findMonthlyHoursByProject(start, end);
		return monthlyProjectHoursList;
	}
}
