package com.pmtool.backend.DTO.request;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TimelineSaveRequest {

	private String date;
	private List<TimeLineRequestDto> rows;
}
