package com.pmtool.backend.DTO.response;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TimeLineResponseDto {
	private Long timeLineId;
	private String tagName;
	private LocalTime duration;
	private String label;
	private String comment;
	private LocalTime startTime;
	private LocalTime endTime;
	private LocalDate date;
	private Long durationMillis;
	private String name;
	private String employeeId;

	public TimeLineResponseDto(String name, Long durationMillis) {
		super();
		this.name = name;
		this.durationMillis = durationMillis;
	}

	public TimeLineResponseDto(String employeeId, String name, Long durationMillis) {
		super();
		this.employeeId = employeeId;
		this.name = name;
		this.durationMillis = durationMillis;
	}

}
