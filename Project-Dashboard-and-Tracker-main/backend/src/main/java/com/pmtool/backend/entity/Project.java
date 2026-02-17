package com.pmtool.backend.entity;

import com.pmtool.backend.util.DurationToPostgresIntervalConverter;
import jakarta.persistence.*;
import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "projects")
public class Project {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "project_id")
	private Long id;

	@Column(name = "project_name", nullable = false, unique = true)
	private String name;

	@Column(name = "client_name")
	private String clientName;

	@Convert(converter = DurationToPostgresIntervalConverter.class)
	@Column(name = "estimated_hours")
	private Duration estimatedHours;

//    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private Set<Discipline> disciplines;

	@OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<Discipline> disciplines = new HashSet<>();

	@OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<Milestone> milestones;

	@OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<WorkLogEntry> workLogEntries;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "emp_id", referencedColumnName = "employee_id")
	private Employee employee;

	@OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<ProjectAssignment> assignments = new HashSet<>();

	@OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<TimeLineSummary> timelineSummary = new HashSet<>();

	@Column(name = "file_path")
	String filePath;

	public Project() {
	}

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

	public Duration getEstimatedHours() {
		return estimatedHours;
	}

	public void setEstimatedHours(Duration estimatedHours) {
		this.estimatedHours = estimatedHours;
	}

	public Set<Discipline> getDisciplines() {
		return disciplines;
	}

	public void setDisciplines(Set<Discipline> disciplines) {
		this.disciplines = disciplines;
	}

	public Set<Milestone> getMilestones() {
		return milestones;
	}

	public void setMilestones(Set<Milestone> milestones) {
		this.milestones = milestones;
	}

	public Set<WorkLogEntry> getWorkLogEntries() {
		return workLogEntries;
	}

	public void setWorkLogEntries(Set<WorkLogEntry> workLogEntries) {
		this.workLogEntries = workLogEntries;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public Set<ProjectAssignment> getAssignments() {
		return assignments;
	}

	public void setAssignments(Set<ProjectAssignment> assignments) {
		this.assignments = assignments;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public Set<TimeLineSummary> getTimelineSummary() {
		return timelineSummary;
	}

	public void setTimelineSummary(Set<TimeLineSummary> timelineSummary) {
		this.timelineSummary = timelineSummary;
	}

}