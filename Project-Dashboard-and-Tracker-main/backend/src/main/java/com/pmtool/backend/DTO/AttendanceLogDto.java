package com.pmtool.backend.DTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceLogDto {

	private Long id;
	private String empId;
	private String empDeviceCode;
	private LocalDateTime timestamp;
	private String direction;
	private LocalDate date;
	private LocalDateTime inTime;
	private LocalDateTime outTime;
	private String totalHours;
	private String totalHoursWorked;
	private String status;
	private String username;

	private LocalDateTime firstCheckIn;
	private LocalDateTime lastCheckIn;
	private LocalDateTime lastCheckOut;
	private long totalWorkedMillis;
	private boolean currentlyCheckedIn;
	private List<AttendanceSegmentDto> segments;

	private List<LocalDateTime> inList = new ArrayList<LocalDateTime>();
	private List<LocalDateTime> outList = new ArrayList<LocalDateTime>();

	public AttendanceLogDto(String empDeviceCode, LocalDateTime timestamp, String direction) {
		this.empDeviceCode = empDeviceCode;
		this.timestamp = timestamp;
		this.direction = direction;
	}

//	private Employee employee;

}
