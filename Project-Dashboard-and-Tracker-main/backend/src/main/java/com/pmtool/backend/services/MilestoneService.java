package com.pmtool.backend.services;

import com.pmtool.backend.DTO.MilestoneDTO; // Corrected package name if needed
import com.pmtool.backend.DTO.MilestoneResponseDTO; // Corrected package name if needed
import com.pmtool.backend.entity.Discipline;
import com.pmtool.backend.entity.Milestone;
import com.pmtool.backend.entity.Project;
import com.pmtool.backend.exception.ResourceNotFoundException;
import com.pmtool.backend.repository.DisciplineRepository;
import com.pmtool.backend.repository.MilestoneRepository;
import com.pmtool.backend.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MilestoneService {

	@Autowired
	private MilestoneRepository milestoneRepository;
	@Autowired
	private ProjectRepository projectRepository;

	@Autowired
	private DisciplineRepository disciplineRepository;

	// This is the correct method for the admin's main milestone page.
	@Transactional(readOnly = true)
	public List<MilestoneResponseDTO> getAllMilestones() {
		return milestoneRepository.findAllWithHoursConsumed();
	}

	@Transactional
	public MilestoneResponseDTO createMilestone(MilestoneDTO milestoneDTO) {
		Project project = projectRepository.findById(milestoneDTO.getProjectId())
				.orElseThrow(() -> new RuntimeException("Project not found"));
//		Discipline discipline = disciplineRepository.findById(milestoneDTO.getDisciplineId())
//				.orElseThrow(() -> new ResourceNotFoundException("Discipline not found"));
		Set<Discipline> disciplineSet = disciplineRepository.findDisciplinesByMilestone_Id(milestoneDTO.getId());
		Milestone milestone = new Milestone();
		milestone.setName(milestoneDTO.getName());
		milestone.setProject(project);
		milestone.setDueDate(milestoneDTO.getDueDate());
//		milestone.setDiscipline(discipline);
		milestone.setDisciplines(disciplineSet);

		Milestone savedMilestone = milestoneRepository.save(milestone);
		return new MilestoneResponseDTO(savedMilestone);
	}

	@Transactional
	public MilestoneResponseDTO updateMilestone(Long milestoneId, MilestoneDTO milestoneDTO) {
		Milestone existingMilestone = milestoneRepository.findById(milestoneId)
				.orElseThrow(() -> new RuntimeException("Milestone not found"));

		if (milestoneDTO.getProjectId() != null
				&& !milestoneDTO.getProjectId().equals(existingMilestone.getProject().getId())) {
			Project project = projectRepository.findById(milestoneDTO.getProjectId())
					.orElseThrow(() -> new RuntimeException("New project not found"));
			existingMilestone.setProject(project);
		}

		existingMilestone.setName(milestoneDTO.getName());
		existingMilestone.setDueDate(milestoneDTO.getDueDate());

		Milestone updatedMilestone = milestoneRepository.save(existingMilestone);
		return new MilestoneResponseDTO(updatedMilestone);
	}

	@Transactional
	public void deleteMilestone(Long milestoneId) {
		if (!milestoneRepository.existsById(milestoneId)) {
			throw new RuntimeException("Milestone not found with id: " + milestoneId);
		}
		milestoneRepository.deleteById(milestoneId);
	}

	@Transactional
	public List<MilestoneResponseDTO> getUserMilestones(List<Long> projectIds) {
		List<Milestone> milestoneList = milestoneRepository.findMilestonesByProjectIds(projectIds);
		return milestoneList.stream().map(milestone -> new MilestoneResponseDTO(milestone)).toList();
	}

//	@Transactional(readOnly = true)
//	public Set<MilestoneResponseDTO> getMilestonesByDiscId(Long disciplineId) {
//		return milestoneRepository.findByDiscipline_Id(disciplineId).stream()
//				.map(milestone -> new MilestoneResponseDTO(milestone)).collect(Collectors.toSet());
//	}

	@Transactional(readOnly = true)
	public Set<MilestoneResponseDTO> getAllMilestonesData() {
		return milestoneRepository.findAll().stream().map(milestone -> new MilestoneResponseDTO(milestone))
				.collect(Collectors.toSet());
	}

	public List<MilestoneResponseDTO> getMilestonesByProjId(Long projectId) {
		return milestoneRepository.findByProjectWithDisciplines(projectId).stream()
				.map(milestone -> new MilestoneResponseDTO(milestone)).collect(Collectors.toList());
	}

}