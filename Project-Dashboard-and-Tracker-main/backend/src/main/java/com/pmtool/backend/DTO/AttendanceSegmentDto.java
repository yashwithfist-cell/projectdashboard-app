package com.pmtool.backend.DTO;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceSegmentDto {

	private LocalDateTime in;
	private LocalDateTime out;
	private String status;
}
