package com.pmtool.backend.DTO;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import com.pmtool.backend.entity.Milestone;
import com.pmtool.backend.entity.ProjectAssignment;

public class MilestoneResponseDTO {

	private Long milestoneId;
	private String milestoneName;

	public void setMilestoneId(Long milestoneId) {
		this.milestoneId = milestoneId;
	}

	private String projectName;
	private LocalDate dueDate;
	private Double hoursConsumed;

	public void setMilestoneName(String milestoneName) {
		this.milestoneName = milestoneName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public void setDueDate(LocalDate dueDate) {
		this.dueDate = dueDate;
	}

	public void setHoursConsumed(Double hoursConsumed) {
		this.hoursConsumed = hoursConsumed;
	}

	public Long getMilestoneId() {
		return this.milestoneId;
	}

	public String getMilestoneName() {
		return this.milestoneName;
	}

	public String getProjectName() {
		return this.projectName;
	}

	public LocalDate getDueDate() {
		return this.dueDate;
	}

	public Double getHoursConsumed() {
		return this.hoursConsumed;
	}

	public MilestoneResponseDTO(Long milestoneId, String milestoneName, String projectName, LocalDate dueDate,
			Double hoursConsumed) {
		this.milestoneId = milestoneId;
		this.milestoneName = milestoneName;
		this.projectName = projectName;
		this.dueDate = dueDate;
		this.hoursConsumed = Double.valueOf((hoursConsumed != null) ? hoursConsumed.doubleValue() : 0.0D);
	}

	public MilestoneResponseDTO(Milestone milestone) {
		this.milestoneId = milestone.getId();
		this.milestoneName = milestone.getName();
		this.dueDate = milestone.getDueDate();

		if (milestone.getProject() != null) {
			this.projectName = milestone.getProject().getName();
		}

		this.hoursConsumed = Optional.ofNullable(milestone.getProjectAssignments())
				.orElse(Collections.emptySet())
		        .stream()
		        .mapToDouble(ProjectAssignment::getTotalWorkedSeconds)
		        .sum() / 3600.0;
	}

}
