package com.pmtool.backend.DTO;

import com.pmtool.backend.entity.WorkLogEntry;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class WorkLogResponseDTO {
	private Long id;
	private LocalDate date;
	private String task;
	private String description;
	private String projectName;
	private String milestoneName;
	private String disciplineName;
	private LocalTime startTime;
	private LocalTime endTime;
	private Double hoursWorked;
	private boolean currentProject;

	public WorkLogResponseDTO(WorkLogEntry entity) {
		this.id = entity.getId();
		this.date = entity.getDate();
		this.task = entity.getTask();
		this.description = entity.getDescription();
		this.startTime = entity.getStartTime().toLocalTime();
		this.endTime = (entity.getEndTime() != null) ? entity.getEndTime().toLocalTime() : LocalTime.now();
		this.hoursWorked = entity.getHoursWorked();
		this.projectName = (entity.getProject() != null) ? entity.getProject().getName() : "N/A";
		this.milestoneName = (entity.getMilestone() != null) ? entity.getMilestone().getName() : "N/A";
		this.disciplineName = (entity.getDiscipline() != null) ? entity.getDiscipline().getName() : "N/A";
		this.currentProject = entity.isCurrentProject();
	}

}