package com.pmtool.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

//@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "milestones")
public class Milestone {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	private Long id;

	@Column(name = "milestone_name", nullable = false)
	private String name;

	@Column(name = "due_date")
	private LocalDate dueDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "project_id", nullable = false)
	private Project project;

	@OneToMany(mappedBy = "milestone", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<WorkLogEntry> workLogEntries;

	@OneToMany(mappedBy = "milestone", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<ProjectAssignment> projectAssignments = new HashSet<ProjectAssignment>();

//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "discipline_id",referencedColumnName = "id", nullable = false)
//	private Discipline discipline;
	@OneToMany(mappedBy = "milestone", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	private Set<Discipline> disciplines = new HashSet<Discipline>();

	public void addDiscipline(Discipline discipline) {
		disciplines.add(discipline);
		discipline.setMilestone(this);
	}

	public void removeDiscipline(Discipline discipline) {
		disciplines.remove(discipline);
		discipline.setMilestone(null);
	}

}