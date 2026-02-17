package com.pmtool.backend.services;

import java.util.List;
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

	private final SecurityFilterChain filterChain;
	private final ProjectRepository projectRepository;
	private final EmployeeRepository employeeRepository;
	private final DepartmentRepository departmentRepository;
	private final MilestoneRepository milestoneRepository;
	private final WorkLogEntryRepository workLogEntryRepository;
	private final TimLineRepository timLineRepository;

	@Override
	public DashboardStatsDTO getStats() {
		return new DashboardStatsDTO(projectRepository.count(), employeeRepository.count(),
				departmentRepository.count(), milestoneRepository.count());
	}

	@Override
	public List<ChartDataDTO> getHoursByProject() {
		List<TimeLineResponseDto> timeLineSummaryList = timLineRepository.findHoursByProject();
		List<ChartDataDTO> chartDataList = timeLineSummaryList.stream().map(timeline -> ChartDataDTO.builder()
				.name(timeline.getName()).value(TimeUtil.formatDurationToDouble(timeline.getDurationMillis())).build())
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
}
