package com.pmtool.backend.controller;

import com.pmtool.backend.DTO.DisciplineDTO;
import com.pmtool.backend.DTO.MilestoneDTO;
import com.pmtool.backend.DTO.ProjectDTO;
import com.pmtool.backend.DTO.ProjectResponseDTO;
import com.pmtool.backend.entity.Project;
import com.pmtool.backend.repository.ProjectRepository;
import com.pmtool.backend.services.ProjectDataService;
import com.pmtool.backend.services.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/projects")
@CrossOrigin
public class ProjectController {

	@Autowired
	private ProjectService projectService;

	@Autowired
	private ProjectDataService projectDataService;

	@Autowired
	private ProjectRepository projectRepository;

	// ... (All GET endpoints are correct) ...
	@GetMapping
	@PreAuthorize("hasAnyRole('SYSTEM_ADMIN','SUPER_ADMIN','HUMAN_RESOURCE')")
	public ResponseEntity<List<ProjectResponseDTO>> getAllProjectsWithHours() {
		List<ProjectResponseDTO> projects = projectService.getAllProjectsWithHours();
		return ResponseEntity.ok(projects);
	}

	@GetMapping("/list")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<List<ProjectDTO>> getProjectListForDropdown() {
		List<ProjectDTO> projects = projectDataService.getAllProjects();
		return ResponseEntity.ok(projects);
	}

	@GetMapping("/{projectId}/milestones")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<List<MilestoneDTO>> getMilestonesForProject(@PathVariable Long projectId) {
		List<MilestoneDTO> milestones = projectDataService.getMilestonesByProjectId(projectId);
		return ResponseEntity.ok(milestones);
	}

	@GetMapping("/{projectId}/disciplines")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<List<DisciplineDTO>> getDisciplinesForProject(@PathVariable Long projectId) {
		List<DisciplineDTO> disciplines = projectDataService.getDisciplinesByProjectId(projectId);
		return ResponseEntity.ok(disciplines);
	}
	
	@GetMapping("/{projectId}/{milestoneId}/disciplines")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<List<DisciplineDTO>> getDisciplinesByIds(@PathVariable Long projectId,@PathVariable Long milestoneId) {
		List<DisciplineDTO> disciplines = projectDataService.getDisciplinesByIds(projectId,milestoneId);
		return ResponseEntity.ok(disciplines);
	}

	// --- THIS IS THE CORRECTED METHOD ---
	@PostMapping
	@PreAuthorize("hasAnyRole('SYSTEM_ADMIN','SUPER_ADMIN','HUMAN_RESOURCE')")
	// The return type now correctly matches the service's return type.
	public ResponseEntity<ProjectDTO> createProject(@RequestBody ProjectDTO projectDTO) {
		ProjectDTO createdProject = projectService.createProject(projectDTO);
		return new ResponseEntity<>(createdProject, HttpStatus.CREATED);
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasAnyRole('SYSTEM_ADMIN','SUPER_ADMIN','HUMAN_RESOURCE')")
	public ResponseEntity<ProjectDTO> updateProject(@PathVariable Long id, @RequestBody ProjectDTO projectDTO) {
		ProjectDTO updatedProject = projectService.updateProject(id, projectDTO);
		return ResponseEntity.ok(updatedProject);
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasAnyRole('SYSTEM_ADMIN','SUPER_ADMIN','HUMAN_RESOURCE')")
	public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
		projectService.deleteProject(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/getAllByUser")
	@PreAuthorize("hasAnyRole('PROJECT_MANAGER','TEAM_LEAD')")
	public ResponseEntity<List<ProjectResponseDTO>> getAllProjectsByUser(Authentication authentication) {
		List<ProjectResponseDTO> projects = projectService.getAllProjectsByUser(authentication);
		return ResponseEntity.ok(projects);
	}

	@GetMapping("/project/{projectId}")
	@PreAuthorize("hasAnyRole('SYSTEM_ADMIN','SUPER_ADMIN','HUMAN_RESOURCE')")
	public ResponseEntity<ProjectResponseDTO> getProjectById(@PathVariable Long projectId) {
		ProjectResponseDTO project = projectService.getProjectById(projectId);
		return ResponseEntity.ok(project);
	}

	@GetMapping("/open/{id}")
	public ResponseEntity<String> openFolder(@PathVariable Long id) throws IOException {
		Project project = projectRepository.findById(id).orElseThrow(() -> new RuntimeException("Project not found"));

		String path = project.getFilePath(); // e.g., \\192.168.1.254\projects\09.Pilot Project

		// Command to open Windows Explorer
		Runtime.getRuntime().exec("explorer.exe \"" + path + "\"");

		return ResponseEntity.ok("Folder opened in File Explorer");
	}
}