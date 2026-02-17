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

import com.pmtool.backend.DTO.request.IdleLogDtoRequest;
import com.pmtool.backend.DTO.response.ApiResponse;
import com.pmtool.backend.DTO.response.IdleLogDtoResponse;
import com.pmtool.backend.services.IdleLogService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/idleLog")
@RequiredArgsConstructor
@CrossOrigin
public class IdleLogController {

	private final IdleLogService service;

//	@PostMapping("/start")
//	@PreAuthorize("hasRole('EMPLOYEE')")
//	public ResponseEntity<ApiResponse> startIdle(@RequestBody IdleLogDto dto, HttpServletRequest request,
//			Authentication authentication) {
//		IdleLogDto idleLog = service.startIdleSession(dto, authentication);
//		return ResponseEntity
//				.ok(ApiResponse.success(idleLog, "Idle Log Start Added successfully", request.getRequestURI()));
//	}

	@PostMapping("/end")
	@PreAuthorize("hasRole('EMPLOYEE')")
	public ResponseEntity<ApiResponse> endIdle(@RequestBody IdleLogDtoRequest dto, HttpServletRequest request,
			Authentication authentication) {
		IdleLogDtoResponse idleLog = service.endIdleSession(dto, authentication);
		return ResponseEntity
				.ok(ApiResponse.success(idleLog, "Idle Log End Added successfully", request.getRequestURI()));
	}

	@GetMapping("/idleLogRecord/{idleDate}")
	@PreAuthorize("hasRole('EMPLOYEE')")
	public ResponseEntity<ApiResponse> getIdleLog(@PathVariable LocalDate idleDate, Authentication authentication,
			HttpServletRequest request) {
		Map<Long, List<IdleLogDtoResponse>> idleLog = service.getIdleLog(idleDate, authentication);
		return ResponseEntity
				.ok(ApiResponse.success(idleLog, "Idle Log Fetched successfully", request.getRequestURI()));
	}
}
