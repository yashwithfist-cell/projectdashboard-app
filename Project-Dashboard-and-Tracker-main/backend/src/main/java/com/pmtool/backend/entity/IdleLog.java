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
@Table(name = "idle_log")
public class IdleLog {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idle_log_id")
	private long idleLogId;
	@Column(name = "idle_date", nullable = false)
	private LocalDate idleDate;
	@Column(name = "idle_start_time", nullable = false)
	private LocalDateTime idleStartTime;
	@Column(name = "idle_end_time", nullable = false)
	private LocalDateTime idleEndTime;
	@Column(name = "idle_duration_millis", nullable = false)
	private Long idleDurationMillis;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "employee_id", nullable = false)
	private Employee employee;
}
