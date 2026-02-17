package com.pmtool.backend.services;

import com.pmtool.backend.DTO.DisciplineDTO; // Corrected package name if needed
import com.pmtool.backend.entity.Discipline;
import com.pmtool.backend.entity.Milestone;
import com.pmtool.backend.entity.Project;
import com.pmtool.backend.exception.DisciplineInsertionFailedException;
import com.pmtool.backend.exception.ResourceNotFoundException;
import com.pmtool.backend.repository.DisciplineRepository;
import com.pmtool.backend.repository.MilestoneRepository;
import com.pmtool.backend.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DisciplineService {

	@Autowired
	private DisciplineRepository disciplineRepository;

	@Autowired
	private ProjectRepository projectRepository;

	@Autowired
	MilestoneRepository milestoneRepository;

	// Helper method to convert an Entity to a DTO
	private DisciplineDTO convertToDTO(Discipline discipline) {
		// Includes the projectId for context, which is good practice
		return DisciplineDTO.builder().id(discipline.getId()).name(discipline.getName())
				.projectId(discipline.getProject().getId()).projectName(discipline.getProject().getName()).build();
	}

	/**
	 * Retrieves a list of all disciplines.
	 * 
	 * @return A list of DisciplineDTOs.
	 */
	@Transactional(readOnly = true)
	public List<DisciplineDTO> getAllDisciplines() {
		return disciplineRepository.findAll().stream().map(this::convertToDTO) // Using the richer DTO is better
				.collect(Collectors.toList());
	}

	/**
	 * Creates a new discipline and links it to a project.
	 * 
	 * @param disciplineDTO The DTO containing the name and projectId.
	 * @return The created discipline as a DTO.
	 */
	@Transactional
	public DisciplineDTO createDiscipline(DisciplineDTO disciplineDTO) {
		Discipline savedDiscipline = null;
		Project project = projectRepository.findById(disciplineDTO.getProjectId())
				.orElseThrow(() -> new RuntimeException("Project not found with id: " + disciplineDTO.getProjectId()));

		Milestone milestone = milestoneRepository.findById(disciplineDTO.getMilestoneId()).orElseThrow(
				() -> new ResourceNotFoundException("Milestone not found with id: " + disciplineDTO.getMilestoneId()));

		Discipline discipline = new Discipline();
		discipline.setName(disciplineDTO.getName());
		discipline.setProject(project);
		discipline.setMilestone(milestone);

		try {
			savedDiscipline = disciplineRepository.save(discipline);
		} catch (Exception e) {
			throw new DisciplineInsertionFailedException("Failed to insert Discipline");
		}
		return convertToDTO(savedDiscipline);
	}

	/**
	 * Updates an existing discipline's name and potentially its project.
	 * 
	 * @param id            The ID of the discipline to update.
	 * @param disciplineDTO The DTO with the new data.
	 * @return The updated discipline as a DTO.
	 */
	@Transactional
	public DisciplineDTO updateDiscipline(Long id, DisciplineDTO disciplineDTO) {
		Discipline existingDiscipline = disciplineRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Discipline not found with id: " + id));

		existingDiscipline.setName(disciplineDTO.getName());

		// Handle changing the project if a new projectId is provided
		if (disciplineDTO.getProjectId() != null
				&& !disciplineDTO.getProjectId().equals(existingDiscipline.getProject().getId())) {
			Project newProject = projectRepository.findById(disciplineDTO.getProjectId())
					.orElseThrow(() -> new ResourceNotFoundException(
							"New project not found with id: " + disciplineDTO.getProjectId()));
			existingDiscipline.setProject(newProject);
		}

		if (disciplineDTO.getMilestoneId() != null
				&& !disciplineDTO.getMilestoneId().equals(existingDiscipline.getMilestone().getId())) {
			Milestone newMilestone = milestoneRepository.findById(disciplineDTO.getMilestoneId())
					.orElseThrow(() -> new ResourceNotFoundException(
							"New Milestone not found with id: " + disciplineDTO.getMilestoneId()));
			existingDiscipline.setMilestone(newMilestone);
		}

		Discipline updatedDiscipline = disciplineRepository.save(existingDiscipline);
		return convertToDTO(updatedDiscipline);
	}

	/**
	 * Deletes a discipline by its ID.
	 * 
	 * @param id The ID of the discipline to delete.
	 */
	@Transactional
	public void deleteDiscipline(Long id) {
		if (!disciplineRepository.existsById(id)) {
			throw new RuntimeException("Discipline not found with id: " + id);
		}
		disciplineRepository.deleteById(id);
	}

	public Set<DisciplineDTO> getDisciplinesByProjId(Long projectId) {
		Set<Discipline> disciplineSet = disciplineRepository.findDisciplinesByProject_Id(projectId);
		return disciplineSet.stream().map(discipline -> DisciplineDTO.builder().id(discipline.getId())
				.name(discipline.getName()).milestoneId(discipline.getMilestone().getId()).milestoneName(discipline.getMilestone().getName()).build())
				.collect(Collectors.toSet());
	}

	public Set<DisciplineDTO> getDisciplinesByMilestoneId(Long milestoneId) {
		return disciplineRepository.findDisciplinesByMilestone_Id(milestoneId).stream()
				.map(discipline -> DisciplineDTO.builder().id(discipline.getId()).name(discipline.getName())
						.milestoneId(discipline.getMilestone().getId()).build())
				.collect(Collectors.toSet());
	}
}