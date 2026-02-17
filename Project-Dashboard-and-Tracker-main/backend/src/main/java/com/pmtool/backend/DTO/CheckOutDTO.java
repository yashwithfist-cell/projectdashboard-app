package com.pmtool.backend.DTO;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckOutDTO {

	private LocalDateTime startTime;
	private LocalDateTime endTime;
	private String comment;
	private LocalDate date;
}
