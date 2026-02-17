package com.pmtool.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.pmtool.backend.DTO.HolidayDto;
import com.pmtool.backend.DTO.LeaveDto;
import com.pmtool.backend.enums.LeaveType;
import com.pmtool.backend.enums.Role;
import com.pmtool.backend.exception.LeaveApplyException;
import com.pmtool.backend.services.LeaveService;

import java.util.List;

@RestController
@RequestMapping("/api/leaves")
@CrossOrigin
public class LeaveController {

	@Autowired
	private LeaveService leaveService;

	@GetMapping
	@PreAuthorize("hasAnyRole('EMPLOYEE','PROJECT_MANAGER', 'HUMAN_RESOURCE')")
	public List<LeaveDto> getAll() {
		return leaveService.getAllLeaves();
	}

	@GetMapping("/types")
	@PreAuthorize("hasRole('EMPLOYEE')")
	public LeaveType[] getLeaveTypes() {
		return LeaveType.values();
	}

	@GetMapping("/employee")
	@PreAuthorize("hasRole('EMPLOYEE')")
	public List<LeaveDto> getByEmployee(Authentication authentication) {
		return leaveService.getLeavesByEmployee(authentication);
	}

	@PostMapping
	@PreAuthorize("hasRole('EMPLOYEE')")
	public LeaveDto apply(@RequestBody LeaveDto leave, Authentication authentication) {
		return leaveService.applyLeave(leave, authentication);
	}

	@PutMapping("/{id}/status")
	@PreAuthorize("hasAnyRole('PROJECT_MANAGER','TEAM_LEAD','HUMAN_RESOURCE')")
	public LeaveDto updateStatus(@PathVariable Long id, @RequestParam String status) {
		return leaveService.updateStatus(id, status.replace(" ", "_"));
	}

	@GetMapping("/getRoles/{role}")
	@PreAuthorize("hasAnyRole('PROJECT_MANAGER','TEAM_LEAD','EMPLOYEE','HUMAN_RESOURCE')")
	public List<String> getManagers(@PathVariable Role role, Authentication authentication) {
		return leaveService.getEmployeesByRole(role, authentication);
	}

	@GetMapping("/getAllByManager")
	@PreAuthorize("hasRole('PROJECT_MANAGER')")
	public List<LeaveDto> getAllByManager(Authentication authentication) {
		return leaveService.getAllByManager(authentication);
	}

	@GetMapping("/getAllByTeamLead")
	@PreAuthorize("hasRole('TEAM_LEAD')")
	public List<LeaveDto> getAllByTeamLead(Authentication authentication) {
		return leaveService.getAllByTeamLead(authentication);
	}

	@GetMapping("/getHolidays")
	@PreAuthorize("hasRole('EMPLOYEE')")
	public List<HolidayDto> getAllHolidays() {
		return leaveService.getAllHolidays();
	}

}