package com.pmtool.backend.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pmtool.backend.DTO.AttendanceLogDto;
import com.pmtool.backend.services.AttendanceLogService;

@RestController
@RequestMapping("/api/attendancelog")
@CrossOrigin
public class AttendanceLogController {

	@Autowired
	private AttendanceLogService attendanceLogService;

	@GetMapping("/search")
	@PreAuthorize("hasAnyRole('SYSTEM_ADMIN','HUMAN_RESOURCE')")
	public ResponseEntity<List<AttendanceLogDto>> search(@RequestParam String fromDate, @RequestParam String toDate) {
		LocalDate from = LocalDate.parse(fromDate);
		LocalDate to = LocalDate.parse(toDate);
		List<AttendanceLogDto> result = attendanceLogService.searchAttendance(from, to);
		return ResponseEntity.ok(result);
	}

	@GetMapping("/searchByEmployee")
	@PreAuthorize("hasRole('EMPLOYEE')")
	public ResponseEntity<List<AttendanceLogDto>> searchByEmployee(@RequestParam String fromDate,
			@RequestParam String toDate, Authentication authentication) {
		LocalDate from = LocalDate.parse(fromDate);
		LocalDate to = LocalDate.parse(toDate);
		String username = authentication.getName();
		List<AttendanceLogDto> result = attendanceLogService.searchAttendanceByEmployee(from, to, username);
		return ResponseEntity.ok(result);
	}

	@GetMapping("/empCheckIn")
	@PreAuthorize("hasRole('EMPLOYEE')")
	public ResponseEntity<AttendanceLogDto> getEmployeeCheckIn(Authentication authentication) {
		String username = authentication.getName();
		AttendanceLogDto attendanceLog = attendanceLogService.getEmployeeAttendance(username);
		return ResponseEntity.ok(attendanceLog);
	}

}
