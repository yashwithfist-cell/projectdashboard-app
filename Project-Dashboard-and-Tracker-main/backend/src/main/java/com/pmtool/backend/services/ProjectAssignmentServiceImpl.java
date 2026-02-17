package com.pmtool.backend.services;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pmtool.backend.DTO.DisciplineDTO;
import com.pmtool.backend.DTO.MilestoneDTO;
import com.pmtool.backend.DTO.request.ProjectAssignmentRequest;
import com.pmtool.backend.DTO.response.ProjectAssignmentResponse;
import com.pmtool.backend.entity.Discipline;
import com.pmtool.backend.entity.Employee;
import com.pmtool.backend.entity.Milestone;
import com.pmtool.backend.entity.Project;
import com.pmtool.backend.entity.ProjectAssignment;
import com.pmtool.backend.enums.AssignmentStatus;
import com.pmtool.backend.enums.TaskStatus;
import com.pmtool.backend.exception.ResourceNotFoundException;
import com.pmtool.backend.repository.DisciplineRepository;
import com.pmtool.backend.repository.EmployeeRepository;
import com.pmtool.backend.repository.MilestoneRepository;
import com.pmtool.backend.repository.ProjectAssignmentRepo;
import com.pmtool.backend.repository.ProjectRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectAssignmentServiceImpl implements ProjectAssignmentService {

	private final ProjectAssignmentRepo projectAssignmentRepo;
	private final EmployeeRepository employeeRepo;
	private final ProjectRepository projectRepo;
	private final MilestoneRepository milestoneRepo;
	private final DisciplineRepository disciplineRepo;

	@Override
	public ProjectAssignmentResponse assignProject(ProjectAssignmentRequest req, Authentication authentication) {
		Employee employee = employeeRepo.findById(req.getTeamLeadId())
				.orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

		Project project = projectRepo.findById(req.getProjectId())
				.orElseThrow(() -> new ResourceNotFoundException("Project not found"));

		Milestone milestone = milestoneRepo.findById(req.getMilestoneId())
				.orElseThrow(() -> new ResourceNotFoundException("Milestone not found"));

		Discipline discipline = disciplineRepo.findById(req.getDisciplineId())
				.orElseThrow(() -> new ResourceNotFoundException("Discipline not found"));

		ProjectAssignment assignment = new ProjectAssignment();
		assignment.setEmployee(employee);
		assignment.setProject(project);
		assignment.setMilestone(milestone);
		assignment.setDiscipline(discipline);
		assignment.setStartDate(req.getStartDate());
		assignment.setDueDate(req.getDueDate());
		assignment.setStatus(req.getStatus());
		assignment.setHeadName(authentication.getName());
		projectAssignmentRepo.save(assignment);
		return ProjectAssignmentResponse.fromEntity(assignment);
	}

	@Override
	public List<ProjectAssignmentResponse> getAllAssignments(Authentication authentication) {
		boolean isEmployee = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority)
				.anyMatch(role -> role.equals("ROLE_EMPLOYEE"));
		List<ProjectAssignment> projAssignments = null;
		if (isEmployee) {
			projAssignments = projectAssignmentRepo.findAllByUsername(authentication.getName())
					.orElseThrow(() -> new ResourceNotFoundException("Project Assignment not found"));

		} else {
			projAssignments = projectAssignmentRepo.findAllByHeadName(authentication.getName());
		}

		return projAssignments.stream().map(ProjectAssignmentResponse::fromEntity).collect(Collectors.toList());
	}

	@Override
	public ProjectAssignmentResponse updateAssignment(Long id, ProjectAssignmentRequest request,
			Authentication authentication) {
		ProjectAssignment projassignment = projectAssignmentRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Assignment not found"));

		Employee employee = employeeRepo.findById(request.getTeamLeadId())
				.orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

		Project project = projectRepo.findById(request.getProjectId())
				.orElseThrow(() -> new ResourceNotFoundException("Project not found"));

		Milestone milestone = milestoneRepo.findById(request.getMilestoneId())
				.orElseThrow(() -> new ResourceNotFoundException("Milestone not found"));

		Discipline discipline = disciplineRepo.findById(request.getDisciplineId())
				.orElseThrow(() -> new ResourceNotFoundException("Discipline not found"));

		projassignment.setEmployee(employee);
		projassignment.setProject(project);
		projassignment.setMilestone(milestone);
		projassignment.setDiscipline(discipline);
		projassignment.setStartDate(request.getStartDate());
		projassignment.setDueDate(request.getDueDate());
		boolean isEmployee = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority)
				.anyMatch(role -> role.equals("ROLE_EMPLOYEE"));
		if (isEmployee) {
			projassignment.setStatus(request.getStatus());
		}
		projectAssignmentRepo.save(projassignment);
		return ProjectAssignmentResponse.fromEntity(projassignment);
	}

	@Override
	public void deleteAssignmentById(Long id) {
		ProjectAssignment projassignment = projectAssignmentRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Assignment not found"));
		projectAssignmentRepo.delete(projassignment);
	}

	@Override
	public List<MilestoneDTO> getMilestonesByProjAssignId(Long projectId, Authentication authentication) {
		return projectAssignmentRepo.findMilestonesByProjAssignId(projectId, authentication.getName()).stream().map(
				milestone -> new MilestoneDTO(milestone.getId(), milestone.getName(), milestone.getProject().getId()))
				.collect(Collectors.toList());
	}

	@Override
	public List<DisciplineDTO> getDisciplinesByProjAssignId(Long projectId, Authentication authentication) {
		return projectAssignmentRepo.findDisciplinesByProjAssignId(projectId, authentication.getName()).stream()
				.map(discipline -> DisciplineDTO.builder().id(discipline.getId()).name(discipline.getName())
						.projectId(discipline.getProject().getId()).projectName(discipline.getProject().getName())
						.build())
				.collect(Collectors.toList());
	}

	@Override
	public ProjectAssignmentResponse updateAssignmentStatus(Long id, String status, Authentication authentication) {
		ProjectAssignment projassignment = projectAssignmentRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Assignment not found"));
		projassignment.setStatus(AssignmentStatus.valueOf(status));
		ProjectAssignment resultAssign = projectAssignmentRepo.save(projassignment);
		return ProjectAssignmentResponse.fromEntity(resultAssign);
	}

	@Override
	public List<ProjectAssignmentResponse> getAllTeamLeadAssignments(Authentication authentication) {
		List<ProjectAssignment> projAssignments = projectAssignmentRepo.findAllByUsername(authentication.getName())
				.orElseThrow(() -> new ResourceNotFoundException("Project Assignment not found"));
		return projAssignments.stream().map(ProjectAssignmentResponse::fromEntity).collect(Collectors.toList());
	}

	@Override
	public ProjectAssignmentResponse updateAssignmentComment(Long id, String comment, Authentication authentication) {
		ProjectAssignment projassignment = projectAssignmentRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Assignment not found"));
		projassignment.setComment(comment);
		ProjectAssignment resultAssign = projectAssignmentRepo.save(projassignment);
		return ProjectAssignmentResponse.fromEntity(resultAssign);
	}

	@Override
	public List<String> getComments(Long id, Authentication authentication) {
		List<String> comments = projectAssignmentRepo.findAllCommentById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Comments not found"));
		return comments;
	}

	@Override
	public ProjectAssignmentResponse startTimer(Long id) {
		ProjectAssignment pa = projectAssignmentRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Assignment not found"));

		if (!pa.isTimerRunning()) {
			pa.setLastStartedAt(LocalDateTime.now());
			pa.setTimerRunning(true);
		}

		ProjectAssignment resultAssign = projectAssignmentRepo.save(pa);
		return ProjectAssignmentResponse.fromEntity(resultAssign);
	}

	@Override
	public ProjectAssignmentResponse pauseTimer(Long id) {
		ProjectAssignment pa = projectAssignmentRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Assignment not found"));

		if (pa.isTimerRunning() && pa.getLastStartedAt() != null) {

			long sec = Duration.between(pa.getLastStartedAt(), LocalDateTime.now()).getSeconds();
			pa.setTotalWorkedSeconds(pa.getTotalWorkedSeconds() + sec);

			pa.setTimerRunning(false);
			pa.setLastStartedAt(null);
		}

		ProjectAssignment resultAssign = projectAssignmentRepo.save(pa);
		return ProjectAssignmentResponse.fromEntity(resultAssign);
	}

	@Override
	public ProjectAssignmentResponse stopTimer(Long id) {
		ProjectAssignment pa = projectAssignmentRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Assignment not found"));

		// Finalize any running time
		if (pa.isTimerRunning() && pa.getLastStartedAt() != null) {

			long sec = Duration.between(pa.getLastStartedAt(), LocalDateTime.now()).getSeconds();
			pa.setTotalWorkedSeconds(pa.getTotalWorkedSeconds() + sec);
		}

		pa.setTimerRunning(false);
		pa.setLastStartedAt(null);

		ProjectAssignment resultAssign = projectAssignmentRepo.save(pa);
		return ProjectAssignmentResponse.fromEntity(resultAssign);
	}

	@Override
	public ProjectAssignmentResponse updateHeadStatus(Long id, TaskStatus status, String comment) {
		ProjectAssignment projassignment = projectAssignmentRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Assignment not found"));
		projassignment.setHeadStatus(status);
		projassignment.setHeadComment(comment);
		if (status.toString().startsWith("APPROVED")) {
			projassignment.setFinalized(true);
		}
		ProjectAssignment resultAssign = projectAssignmentRepo.save(projassignment);
		return ProjectAssignmentResponse.builder().id(resultAssign.getProjAssignId())
				.taskStatus(resultAssign.getHeadStatus()).headComment(resultAssign.getHeadComment())
				.finalized(resultAssign.getFinalized()).build();
	}
}
