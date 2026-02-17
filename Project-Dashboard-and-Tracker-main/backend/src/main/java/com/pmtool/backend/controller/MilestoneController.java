package com.pmtool.backend.controller;

import com.pmtool.backend.DTO.MilestoneDTO; // You already have this DTO
import com.pmtool.backend.DTO.MilestoneResponseDTO;
import com.pmtool.backend.services.MilestoneService; // Create this service
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/milestones")
@CrossOrigin
public class MilestoneController {

	@Autowired
	private MilestoneService milestoneService;

	@GetMapping
	public ResponseEntity<List<MilestoneResponseDTO>> getAllMilestones() {
		return ResponseEntity.ok(milestoneService.getAllMilestones());
	}

	@PostMapping
	@PreAuthorize("hasAnyRole('SYSTEM_ADMIN','SUPER_ADMIN','HUMAN_RESOURCE')")
	public ResponseEntity<MilestoneResponseDTO> createMilestone(@RequestBody MilestoneDTO milestoneDTO) {
		return ResponseEntity.status(HttpStatus.CREATED).body(milestoneService.createMilestone(milestoneDTO));
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasAnyRole('SYSTEM_ADMIN','SUPER_ADMIN','HUMAN_RESOURCE')")
	public ResponseEntity<Void> deleteMilestone(@PathVariable Long id) { // <-- FIXED
		milestoneService.deleteMilestone(id);
		return ResponseEntity.noContent().build();
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasAnyRole('SYSTEM_ADMIN','SUPER_ADMIN','HUMAN_RESOURCE')")
	public ResponseEntity<MilestoneResponseDTO> updateMilestone(@PathVariable Long id,
			@RequestBody MilestoneDTO milestoneDTO) { // <-- FIXED
		return ResponseEntity.ok(milestoneService.updateMilestone(id, milestoneDTO));
	}

	@PostMapping("/getUserMilestones")
	@PreAuthorize("hasAnyRole('PROJECT_MANAGER','SYSTEM_ADMIN','TEAM_LEAD')")
	public ResponseEntity<List<MilestoneResponseDTO>> getUserMilestones(@RequestBody List<Long> projectIds) {
		return ResponseEntity.ok(milestoneService.getUserMilestones(projectIds));
	}

//	@GetMapping("/getMilestonesByDiscId/{disciplineId}/")
//	@PreAuthorize("isAuthenticated()")
//	public ResponseEntity<Set<MilestoneResponseDTO>> getMilestonesByDiscId(@PathVariable Long disciplineId) {
//		Set<MilestoneResponseDTO> milestones = milestoneService.getMilestonesByDiscId(disciplineId);
//		return ResponseEntity.ok(milestones);
//	}
	
	@GetMapping("/getAllMilestones")
	@PreAuthorize("hasRole('SYSTEM_ADMIN')")
	public ResponseEntity<Set<MilestoneResponseDTO>> getAllMilestonesData() {
		return ResponseEntity.ok(milestoneService.getAllMilestonesData());
	}
	
	@GetMapping("/getMilestonesByProjId/{projectId}")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<List<MilestoneResponseDTO>> getMilestonesByProjId(@PathVariable Long projectId) {
		return ResponseEntity.ok(milestoneService.getMilestonesByProjId(projectId));
	}
}