package com.pmtool.backend.DTO.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IdleLogDtoResponse {
	private long idleLogId;
	private LocalDate idleDate;
	private LocalDateTime idleStartTime;
	private LocalDateTime idleEndTime;
	private Long idleDurationMillis;
	private String comment;
}
