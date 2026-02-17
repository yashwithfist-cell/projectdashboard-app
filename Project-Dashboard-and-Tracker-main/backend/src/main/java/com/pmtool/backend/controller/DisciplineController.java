package com.pmtool.backend.controller;

import com.pmtool.backend.DTO.DisciplineDTO;
import com.pmtool.backend.DTO.MilestoneDTO;
import com.pmtool.backend.DTO.MilestoneResponseDTO;
import com.pmtool.backend.services.DisciplineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/disciplines")
@CrossOrigin
public class DisciplineController {

	@Autowired
	private DisciplineService disciplineService;

	/**
	 * GET /api/disciplines/all Provides a simple list of all disciplines for admin
	 * dropdowns.
	 */
	@GetMapping("/all")
	@PreAuthorize("hasAnyRole('SYSTEM_ADMIN','SUPER_ADMIN','HUMAN_RESOURCE')")
	public ResponseEntity<List<DisciplineDTO>> getAllDisciplinesForDropdown() {
		List<DisciplineDTO> disciplines = disciplineService.getAllDisciplines();
		return ResponseEntity.ok(disciplines);
	}

	/**
	 * POST /api/disciplines Creates a new discipline. Restricted to SYSTEM_ADMIN.
	 */
	@PostMapping("/addDiscipline")
	@PreAuthorize("hasAnyRole('SYSTEM_ADMIN','SUPER_ADMIN','HUMAN_RESOURCE')")
	public ResponseEntity<DisciplineDTO> createDiscipline(@RequestBody DisciplineDTO disciplineDTO) {
		DisciplineDTO createdDiscipline = disciplineService.createDiscipline(disciplineDTO);
		return new ResponseEntity<>(createdDiscipline, HttpStatus.CREATED);
	}

	/**
	 * DELETE /api/disciplines/{id} Deletes a discipline by its ID. Restricted to
	 * SYSTEM_ADMIN.
	 */
	@DeleteMapping("/deleteDiscipline/{id}")
	@PreAuthorize("hasAnyRole('SYSTEM_ADMIN','SUPER_ADMIN','HUMAN_RESOURCE')")
	public ResponseEntity<Void> deleteDiscipline(@PathVariable Long id) {
		disciplineService.deleteDiscipline(id);
		return ResponseEntity.noContent().build();
	}

	@PutMapping("/updateDiscipline/{id}")
	@PreAuthorize("hasAnyRole('SYSTEM_ADMIN','SUPER_ADMIN','HUMAN_RESOURCE')")
	public ResponseEntity<DisciplineDTO> updateDiscipline(@PathVariable Long id,
			@RequestBody DisciplineDTO disciplineDTO) { // <-- FIXED
		return ResponseEntity.ok(disciplineService.updateDiscipline(id, disciplineDTO));
	}

	@GetMapping("/getDisciplineByProjId/{projectId}")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<Set<DisciplineDTO>> getDisciplinesByProjId(@PathVariable Long projectId) {
		Set<DisciplineDTO> disciplines = disciplineService.getDisciplinesByProjId(projectId);
		return ResponseEntity.ok(disciplines);
	}

	@GetMapping("/disciplineSet/{milestoneId}")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<Set<DisciplineDTO>> getDisciplinesByMilestoneId(@PathVariable Long milestoneId) {
		Set<DisciplineDTO> disciplines = disciplineService.getDisciplinesByMilestoneId(milestoneId);
		return ResponseEntity.ok(disciplines);
	}
}