package com.pmtool.backend.DTO;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.pmtool.backend.entity.Milestone;
import com.pmtool.backend.entity.ProjectAssignment;

import lombok.Data;

@Data
public class MilestoneResponseDTO {

	private Long milestoneId;
	private String milestoneName;
	private String projectName;
	private LocalDate dueDate;
	private Double hoursConsumed;
	private Set<DisciplineDTO> disciplineSet;

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

		this.hoursConsumed = Optional.ofNullable(milestone.getProjectAssignments()).orElse(Collections.emptySet())
				.stream().mapToDouble(ProjectAssignment::getTotalWorkedSeconds).sum() / 3600.0;
//		this.disciplineDTO = DisciplineDTO.builder().id(milestone.getDiscipline().getId())
//				.name(milestone.getDiscipline().getName()).build();
		milestone.getDisciplines().stream().map(discipline -> disciplineSet
				.add(DisciplineDTO.builder().id(discipline.getId()).name(discipline.getName()).build()));
	}

}
