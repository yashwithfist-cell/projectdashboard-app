package com.pmtool.backend.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pmtool.backend.DTO.AttendanceLogDto;
import com.pmtool.backend.constants.BiometricDeviceConstants;
import com.pmtool.backend.exception.BiometricFetchFailureException;
import com.pmtool.backend.mapper.AttendanceLogMapper;
import com.pmtool.backend.repository.AttendanceRepository;

@Service
public class AttendanceLogServiceImpl implements AttendanceLogService {

	@Autowired
	private AttendanceRepository attendanceRepository;

	@Autowired
	private AttendanceLogMapper attendanceLogMapper;

	@Autowired
	SoapClientService soapClientService;

	public List<AttendanceLogDto> searchAttendance(LocalDate fromDate, LocalDate toDate) {
		return attendanceRepository.getAttendanceBetweenDates(fromDate, toDate).stream()
				.map(log -> attendanceLogMapper.convertToAttendanceLogDto(log)).toList();
	}

	@Override
	public List<AttendanceLogDto> searchAttendanceByEmployee(LocalDate fromDate, LocalDate toDate, String username) {
		return attendanceRepository.getAttendanceByEmpBetweenDates(fromDate, toDate, username).stream()
				.map(log -> attendanceLogMapper.convertToAttendanceLogDto(log)).toList();
	}

	@Override
	public AttendanceLogDto getEmployeeAttendance(String username) {
		AttendanceLogDto attendanceLog = null;
		try {
			attendanceLog = soapClientService.fetchEmployeeData(username);
		} catch (Exception e) {
			throw new BiometricFetchFailureException("Failed to fetch first chech in for username : " + username);
		}
		return attendanceLog;
	}

}
