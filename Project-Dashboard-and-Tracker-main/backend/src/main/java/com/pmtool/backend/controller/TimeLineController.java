package com.pmtool.backend.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pmtool.backend.DTO.TimelineRowDTO;
import com.pmtool.backend.DTO.response.ApiResponse;
import com.pmtool.backend.DTO.response.TimeLineResponseDto;
import com.pmtool.backend.services.TimeLineService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@CrossOrigin
@RestController
@RequestMapping("/api/timeline")
@RequiredArgsConstructor
public class TimeLineController {

	private final TimeLineService timelineService;

	@PostMapping("/saveTimeLineRow")
	@PreAuthorize("hasRole('EMPLOYEE')")
	public ResponseEntity<ApiResponse> saveTimeLineRow(@RequestBody TimelineRowDTO timelineDto,
			Authentication authentication, HttpServletRequest request) {
		TimelineRowDTO timelineRowDTO = timelineService.saveTimeLineRow(timelineDto, authentication);
		return ResponseEntity.ok(
				ApiResponse.success(timelineRowDTO, "Timeline comment updated successfully", request.getRequestURI()));
	}
	
	@PostMapping("/saveAllTimeLineRows")
	@PreAuthorize("hasRole('EMPLOYEE')")
	public ResponseEntity<ApiResponse> saveAllTimeLineRows(@RequestBody List<TimelineRowDTO> timelineDto,
			Authentication authentication, HttpServletRequest request) {
		List<TimelineRowDTO> timelineRowDTOList = timelineService.saveAllTimeLineRows(timelineDto, authentication);
		return ResponseEntity.ok(
				ApiResponse.success(timelineRowDTOList, "Timeline comment updated successfully", request.getRequestURI()));
	}

	@GetMapping("/comments/today")
	@PreAuthorize("hasRole('EMPLOYEE')")
	public ResponseEntity<ApiResponse> getTimeLineRows(Authentication authentication, HttpServletRequest request) {
		List<TimelineRowDTO> timelineRowDTOList = timelineService.getTimeLineRows(authentication.getName());
		return ResponseEntity
				.ok(ApiResponse.success(timelineRowDTOList, "Timeline Fetched successfully", request.getRequestURI()));
	}

	@GetMapping("/all/{employeeId}/{date}")
	@PreAuthorize("hasAnyRole('SYSTEM_ADMIN','HUMAN_RESOURCE')")
	public ResponseEntity<ApiResponse> getAllTimeLineRows(@PathVariable String employeeId,@PathVariable LocalDate date,HttpServletRequest request) {
		List<TimelineRowDTO> timelineMap = timelineService.getAllTimeLineRows(employeeId,date);
		return ResponseEntity.ok(ApiResponse.success(timelineMap, " All Timelines Fetched successfully",
				request.getRequestURI()));
	}

}
