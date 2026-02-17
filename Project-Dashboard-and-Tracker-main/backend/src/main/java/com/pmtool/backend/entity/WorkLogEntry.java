package com.pmtool.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "worklog_entries")
public class WorkLogEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id") // Can be null for tasks like "Break"
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discipline_id") // Can be null
    private Discipline discipline;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "milestone_id") // Can be null
    private Milestone milestone;

    @Column(name = "entry_date", nullable = false)
    private LocalDate date;

    @Column(name = "task", nullable = false)
    private String task;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "hours_worked", nullable = false)
    private Double hoursWorked;
    
    @Column(name = "current_project")
	private boolean currentProject=false;

    // Inside your WorkLogEntry.java class

    @PrePersist // Runs before a new entity is saved
    @PreUpdate  // Runs before an existing entity is updated
    public void calculateHoursWorked() {
        if (startTime != null && endTime != null) {
            // Calculate duration and set it in hours (as a double)
            java.time.Duration duration = java.time.Duration.between(startTime, endTime);
            this.hoursWorked = duration.toMillis() / (60.0 * 60.0 * 1000.0);
        } else {
            this.hoursWorked = 0.0;
        }
    }

}