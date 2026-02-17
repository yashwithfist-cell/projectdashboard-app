package com.pmtool.backend.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "time_line_summary_test")
public class TimeLineSummary {

	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
//	@Column(name = "time_line_id")
//	private Long timeLineId;
	@Column(name = "rowId")
	private String rowId;
	@Column(name = "Date")
	private LocalDate date;
	@Column(name = "start_time")
	private LocalDateTime startTime;
	@Column(name = "end_time")
	private LocalDateTime endTime;
	@Column(name = "type")
	private String type;
	@Column(name = "label")
	private String label;
	@Column(name = "duration_millis")
	private Long durationMillis;
	@Column(name = "comment")
	private String comment;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "employee_id")
	private Employee employee;
//	@Column(name = "rowId", unique = true)
//	private String rowId;
	@Column(name = "currentProject")
	private boolean currentProject = false;
	@Column(name = "colour")
	private String colour;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "project_id")
	private Project project;

}
