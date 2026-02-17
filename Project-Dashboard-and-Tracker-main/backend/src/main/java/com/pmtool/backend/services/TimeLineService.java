package com.pmtool.backend.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;

import com.pmtool.backend.DTO.TimelineRowDTO;
import com.pmtool.backend.DTO.response.TimeLineResponseDto;
import com.pmtool.backend.entity.Employee;

public interface TimeLineService {

	public Map<String, List<TimelineRowDTO>> buildTimelineForDay();

	public void saveTimelineForDay(Map<String, List<TimelineRowDTO>> timelineMap);

	public TimelineRowDTO saveTimeLineRow(TimelineRowDTO timeLineDto, Authentication authentication);

	public List<TimelineRowDTO> getTimeLineRows(String name);

	public List<TimelineRowDTO> getAllTimeLineRows(String employeeId, LocalDate date);

	public List<TimelineRowDTO> saveAllTimeLineRows(List<TimelineRowDTO> timelineDtoList, Authentication authentication);
}
