package com.pmtool.backend.DTO.request;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IdleLogDtoRequest {
	private long idleLogId;
	private LocalDate idleDate;
	private Long idleStartTime;
	private Long idleEndTime;
	private Long idleDurationMillis;
}
