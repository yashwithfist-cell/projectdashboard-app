package com.pmtool.backend.services;

import com.pmtool.backend.DTO.ProjectDTO;
import com.pmtool.backend.DTO.ProjectResponseDTO;
import com.pmtool.backend.entity.Project;
import com.pmtool.backend.entity.ProjectAssignment;
import com.pmtool.backend.exception.EmployeeNotFoundException;
import com.pmtool.backend.repository.DisciplineRepository;
import com.pmtool.backend.repository.EmployeeRepository;
import com.pmtool.backend.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.pmtool.backend.entity.Discipline;
import com.pmtool.backend.entity.Employee;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectService {

	@Autowired
	private ProjectRepository projectRepository;
	@Autowired
	private DisciplineRepository disciplineRepository;
	@Autowired
	private EmployeeRepository employeeRepository;

	private ProjectDTO convertToDTO(Project project) {
		return new ProjectDTO(project.getId(), project.getName(), project.getClientName());
	}

	@Transactional(readOnly = true)
	public List<ProjectDTO> getAllProjects() {
		return projectRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
	}

	@Transactional
	public ProjectDTO createProject(ProjectDTO projectDTO) {
		Project project = new Project();
		project.setName(projectDTO.getName());
		project.setClientName(projectDTO.getClientName());
		Employee employee = employeeRepository.findById(projectDTO.getProjectManagerId())
				.orElseThrow(() -> new EmployeeNotFoundException(
						"Project Manager Not Found with id : " + projectDTO.getProjectManagerId()));
		project.setEmployee(employee);
		Project savedProject = projectRepository.save(project); // Save project first to get an ID

		// Now, associate the disciplines
		if (projectDTO.getDisciplineIds() != null && !projectDTO.getDisciplineIds().isEmpty()) {
			List<Discipline> disciplinesToUpdate = disciplineRepository.findAllById(projectDTO.getDisciplineIds());
			for (Discipline discipline : disciplinesToUpdate) {
				discipline.setProject(savedProject); // Link each discipline to the new project
			}
			disciplineRepository.saveAll(disciplinesToUpdate);
		}

		return new ProjectDTO(savedProject.getId(), savedProject.getName(), savedProject.getClientName());
	}

	@Transactional
	public ProjectDTO updateProject(Long id, ProjectDTO projectDTO) {
		Project existingProject = projectRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Project not found"));

		existingProject.setName(projectDTO.getName());
		existingProject.setClientName(projectDTO.getClientName());
		Employee employee = employeeRepository.findById(projectDTO.getProjectManagerId())
				.orElseThrow(() -> new EmployeeNotFoundException(
						"Project Manager Not Found with id : " + projectDTO.getProjectManagerId()));
		existingProject.setEmployee(employee);
		Project updatedProject = projectRepository.save(existingProject);

		if (projectDTO.getDisciplineIds() != null && !projectDTO.getDisciplineIds().isEmpty()) {
			List<Discipline> disciplinesToUpdate = disciplineRepository.findAllById(projectDTO.getDisciplineIds());
			for (Discipline discipline : disciplinesToUpdate) {
				discipline.setProject(updatedProject); // Link each discipline to the new project
			}
			disciplineRepository.saveAll(disciplinesToUpdate);
		}

		// Handle discipline updates (this is a more complex operation,
		// often involving clearing old associations and setting new ones)
		// For now, we will focus on the create logic.

		return new ProjectDTO(updatedProject.getId(), updatedProject.getName(), updatedProject.getClientName());
	}

	@Transactional
	public void deleteProject(Long id) {
		if (!projectRepository.existsById(id)) {
			throw new RuntimeException("Project not found with id: " + id);
		}
		projectRepository.deleteById(id);
	}

	/**
	 * This is the special method that fetches projects along with their consumed
	 * hours. It's used by the admin's main project table.
	 */
	@Transactional(readOnly = true)
	public List<ProjectResponseDTO> getAllProjectsWithHours() {
		return projectRepository.findAllWithHoursConsumed();
	}

	public List<ProjectResponseDTO> getAllProjectsByUser(Authentication authentication) {
		List<Project> projectList = null;
		String userName = authentication.getName();
		boolean isProjectManager = authentication.getAuthorities().stream()
				.anyMatch(a -> a.getAuthority().equals("ROLE_PROJECT_MANAGER"));

		boolean isTeamLead = authentication.getAuthorities().stream()
				.anyMatch(a -> a.getAuthority().equals("ROLE_TEAM_LEAD"));
		if (isProjectManager) {
			projectList = projectRepository.findAllByUserName(userName);
		} else if (isTeamLead) {
			projectList = projectRepository.findAllProjectsByTeamLead(userName);
		}

		return projectList.stream().map(project -> ProjectResponseDTO.builder().id(project.getId())
				.name(project.getName()).clientName(project.getClientName()).hoursConsumed(project.getAssignments()
						.stream().mapToLong(ProjectAssignment::getTotalWorkedSeconds).reduce(0L, Long::sum) / 3600.0)
				.build()).toList();
	}

	public ProjectResponseDTO getProjectById(Long projectId) {
		Project project = projectRepository.findById(projectId)
				.orElseThrow(() -> new RuntimeException("Project not found"));
		return ProjectResponseDTO.builder().id(project.getId()).name(project.getName()).build();
	}

} // <-- The closing brace for the class now correctly goes at the end of the
	// file.