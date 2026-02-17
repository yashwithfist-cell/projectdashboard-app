package com.pmtool.backend.DTO.request;

import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TimeLineRequestDto {

	private Long timeLineId;
	private String tagName;
	private LocalTime duration;
	private String label;
	private String comment;
	private LocalTime startTime;
	private LocalTime endTime;
}
