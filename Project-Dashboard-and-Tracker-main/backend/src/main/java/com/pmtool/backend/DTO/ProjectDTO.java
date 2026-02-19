package com.pmtool.backend.DTO;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class ProjectDTO {
	private Long id;
	private String name;
	private String clientName;
	private List<Long> disciplineIds; // Field for discipline IDs
	private Set<DisciplineDTO> disciplines;
	private String projectManagerId;
	private BigDecimal estimatedHours;

	// --- CONSTRUCTORS ---
	public ProjectDTO() {
	}

	// Constructor needed by ProjectDataService
	public ProjectDTO(Long id, String name) {
		this.id = id;
		this.name = name;
	}

	// Full constructor including clientName
	public ProjectDTO(Long id, String name, String clientName, BigDecimal estimatedHours) {
		this.id = id;
		this.name = name;
		this.clientName = clientName;
		this.estimatedHours = estimatedHours;
	}

	// Full constructor including disciplineIds
	public ProjectDTO(Long id, String name, String clientName, List<Long> disciplineIds) {
		this.id = id;
		this.name = name;
		this.clientName = clientName;
		this.disciplineIds = disciplineIds;
	}

	public ProjectDTO(Long id, String name, Set<DisciplineDTO> disciplines) {
		this.id = id;
		this.name = name;
		this.disciplines = disciplines;
	}

	// --- GETTERS AND SETTERS ---
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public List<Long> getDisciplineIds() {
		return disciplineIds;
	}

	public void setDisciplineIds(List<Long> disciplineIds) {
		this.disciplineIds = disciplineIds;
	}

}