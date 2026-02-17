package com.pmtool.backend.services;

import com.pmtool.backend.DTO.WorkLogEntryDTO; // Corrected package name if needed
import com.pmtool.backend.DTO.WorkLogResponseDTO; // Corrected package name if needed
import com.pmtool.backend.entity.*;
import com.pmtool.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WorkLogService {

	@Autowired
	private WorkLogEntryRepository workLogEntryRepository;
	@Autowired
	private EmployeeRepository employeeRepository;
	@Autowired
	private ProjectRepository projectRepository;
	@Autowired
	private MilestoneRepository milestoneRepository;
	@Autowired
	private DisciplineRepository disciplineRepository;

	/**
	 * Saves a new worklog entry and returns it as a DTO. This prevents JSON
	 * serialization errors caused by circular references in entities.
	 * 
	 * @param dto      The WorkLogEntryDTO from the frontend request.
	 * @param username The username of the authenticated employee.
	 * @return A WorkLogResponseDTO representing the saved entry.
	 */
	@Transactional
	public WorkLogResponseDTO saveNewEntry(WorkLogEntryDTO dto, String username) {
		Employee employee = employeeRepository.findByUsername(username)
				.orElseThrow(() -> new RuntimeException("Authenticated employee not found in database"));

		WorkLogEntry newEntry = new WorkLogEntry();
		newEntry.setEmployee(employee);
		newEntry.setDate(dto.getDate());
		newEntry.setTask(dto.getTask());
		newEntry.setDescription(dto.getDescription());
		newEntry.setStartTime(LocalDateTime.of(dto.getDate(), dto.getStartTime()));
		newEntry.setEndTime((dto.getEndTime() != null) ? LocalDateTime.of(dto.getDate(), dto.getEndTime()) : null);
		if (dto.getEndTime()==null) {
			newEntry.setCurrentProject(true);
		}
		WorkLogEntry workLogPrev = workLogEntryRepository.findFirstByEmployee_UsernameOrderByIdDesc(username);
		if (workLogPrev != null) {
			workLogPrev.setEndTime(LocalDateTime.of(dto.getDate(), dto.getStartTime()));
			workLogPrev.setCurrentProject(false);
			workLogEntryRepository.save(workLogPrev);
		}

		if (dto.getProjectId() != null) {
			Project project = projectRepository.findById(dto.getProjectId())
					.orElseThrow(() -> new RuntimeException("Project not found with id: " + dto.getProjectId()));
			newEntry.setProject(project);
		}
		if (dto.getMilestoneId() != null) {
			Milestone milestone = milestoneRepository.findById(dto.getMilestoneId())
					.orElseThrow(() -> new RuntimeException("Milestone not found with id: " + dto.getMilestoneId()));
			newEntry.setMilestone(milestone);
		}
		if (dto.getDisciplineId() != null) {
			Discipline discipline = disciplineRepository.findById(dto.getDisciplineId())
					.orElseThrow(() -> new RuntimeException("Discipline not found with id: " + dto.getDisciplineId()));
			newEntry.setDiscipline(discipline);
		}

		WorkLogEntry savedEntity = workLogEntryRepository.save(newEntry);
		return new WorkLogResponseDTO(savedEntity);
	}

	/**
	 * Retrieves all worklog entries for a specific employee for the current
	 * calendar month.
	 * 
	 * @param username The username of the employee.
	 * @return A list of DTOs ready for the UI.
	 */
	@Transactional(readOnly = true)
	public List<WorkLogResponseDTO> getEntriesForCurrentMonth(String username) {
		LocalDate today = LocalDate.now();
		LocalDate startOfMonth = today.withDayOfMonth(1);
		LocalDate endOfMonth = today.with(TemporalAdjusters.lastDayOfMonth());

		List<WorkLogEntry> entries = workLogEntryRepository
				.findByEmployee_UsernameAndDateBetweenOrderByDateAsc(username, startOfMonth, endOfMonth);

		return entries.stream().map(WorkLogResponseDTO::new).collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public List<WorkLogResponseDTO> getMyDailyWorkLogs(String username) {
		LocalDate today = LocalDate.now();
		LocalDateTime startTime=LocalDateTime.of(today, LocalTime.of(00, 00, 00));
		LocalDateTime endTime=LocalDateTime.of(today, LocalTime.of(23, 59, 59));
		List<WorkLogEntry> entries = workLogEntryRepository.findByDateAndEmployee_Username(today,startTime,endTime,username);
		return entries.stream().sorted(Comparator.comparing(WorkLogEntry::getId)).map(WorkLogResponseDTO::new)
				.collect(Collectors.toList());
	}
}