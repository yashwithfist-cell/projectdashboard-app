package com.pmtool.backend.DTO;

import java.math.BigDecimal;
import java.util.Set;

import org.springframework.core.io.Resource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class ProjectResponseDTO {
	private Long id;
	private String name;
	private String clientName;
	private Double hoursConsumed;
	private String employeeId;
	private Set<ProjectAssignmentDto> assignSet;
	private String filePath;
	private Resource resource;
	private BigDecimal estimatedHours;

	public ProjectResponseDTO(Long id, String name, String clientName, Double hoursConsumed, String employeeId,
			Set<ProjectAssignmentDto> assignSet,BigDecimal estimatedHours) {
		this.id = id;
		this.name = name;
		this.clientName = clientName;

		this.hoursConsumed = Double.valueOf((hoursConsumed != null) ? hoursConsumed.doubleValue() : 0.0D);
		this.employeeId = employeeId;
		this.assignSet = assignSet;
		this.estimatedHours=estimatedHours != null ? estimatedHours : BigDecimal.ZERO;
	}

	public ProjectResponseDTO(Long id, String name, String clientName, Double hoursConsumed, String employeeId,BigDecimal estimatedHours) {
		this.id = id;
		this.name = name;
		this.clientName = clientName;

		this.hoursConsumed = Double.valueOf((hoursConsumed != null) ? hoursConsumed.doubleValue() : 0.0D);
		this.employeeId = employeeId;
		this.estimatedHours=estimatedHours != null ? estimatedHours : BigDecimal.ZERO;
	}

}
