package com.pmtool.backend.DTO;

import java.time.LocalDate;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TimelineRowDTO {
	private String rowId;
	private String type;
	private String start;
	private String end;
	private int startMin;
	private int endMin;
	private String duration;
	private String label;
	private String username;
	private String comment;
	private LocalDate date;
	private String colour;
	private String projectName;
	private String milestoneName;
	private String disciplineName;
	private boolean lastRow;
}
