package com.pmtool.backend.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import com.pmtool.backend.DTO.AttendanceLogDto;

public interface AttendanceLogService {
	public List<AttendanceLogDto> searchAttendance(LocalDate fromDate, LocalDate toDate);
	public List<AttendanceLogDto> searchAttendanceByEmployee(LocalDate fromDate, LocalDate toDate, String username);
	public AttendanceLogDto getEmployeeAttendance(String username);
}
