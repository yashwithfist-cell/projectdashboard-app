package com.pmtool.backend.controller;

import com.pmtool.backend.DTO.CreateEmployeeDTO;
import com.pmtool.backend.DTO.EmployeeResponseDTO;
import com.pmtool.backend.DTO.UpdateEmployeeDTO;
import com.pmtool.backend.DTO.response.ApiResponse;
import com.pmtool.backend.enums.AccountStatus;
import com.pmtool.backend.enums.Role;
import com.pmtool.backend.services.EmployeeService;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@CrossOrigin
public class EmployeeController {

	@Autowired
	private EmployeeService employeeService;

	@GetMapping
	@PreAuthorize("hasAnyRole('SYSTEM_ADMIN','HUMAN_RESOURCE')")
	public ResponseEntity<List<EmployeeResponseDTO>> getAllEmployees(@RequestParam(required = false) String search) {
		return ResponseEntity.ok(employeeService.getAllEmployees(search));
	}

	@PostMapping
	@PreAuthorize("hasAnyRole('SYSTEM_ADMIN','HUMAN_RESOURCE')")
	public ResponseEntity<EmployeeResponseDTO> createEmployee(@RequestBody CreateEmployeeDTO createDto) {
		EmployeeResponseDTO newEmployee = employeeService.createEmployee(createDto);
		return new ResponseEntity<>(newEmployee, HttpStatus.CREATED);
	}

	@DeleteMapping("/{employeeId}")
	@PreAuthorize("hasAnyRole('SYSTEM_ADMIN','HUMAN_RESOURCE')")
	public ResponseEntity<Void> deleteEmployee(@PathVariable String employeeId) {
		// This call will now work because the method exists in the corrected service
		employeeService.deleteEmployee(employeeId);
		return ResponseEntity.noContent().build();
	}

	@PutMapping("/{employeeId}")
	@PreAuthorize("hasAnyRole('SYSTEM_ADMIN','HUMAN_RESOURCE')")
	public ResponseEntity<EmployeeResponseDTO> updateEmployee(@PathVariable String employeeId,
			@RequestBody UpdateEmployeeDTO updateDto) {
		EmployeeResponseDTO updatedEmployee = employeeService.updateEmployee(employeeId, updateDto);
		return ResponseEntity.ok(updatedEmployee);
	}

	@GetMapping("/getAllRoles")
	@PreAuthorize("hasAnyRole('SYSTEM_ADMIN','HUMAN_RESOURCE')")
	public Role[] getAllRoles() {
		return Role.values();
	}

	@GetMapping("/getRoles/{role}")
	@PreAuthorize("hasAnyRole('PROJECT_MANAGER','TEAM_LEAD','EMPLOYEE','SYSTEM_ADMIN','HUMAN_RESOURCE')")
	public List<String> getManagers(@PathVariable Role role) {
		return employeeService.getEmployeesByRole(role);
	}

	@GetMapping("/getProjectManagers/{role}")
	@PreAuthorize("hasAnyRole('PROJECT_MANAGER','TEAM_LEAD','EMPLOYEE','SYSTEM_ADMIN','HUMAN_RESOURCE','SUPER_ADMIN')")
	public List<EmployeeResponseDTO> getAllManagers(@PathVariable Role role) {
		return employeeService.getManagersByRole(role);
	}

	@GetMapping("/getEmployee")
	@PreAuthorize("hasRole('EMPLOYEE')")
	public ResponseEntity<ApiResponse> getEmployeeByUsername(Authentication authentication,
			HttpServletRequest request) {
		return ResponseEntity.ok(ApiResponse.success(employeeService.getEmployeeByUsername(authentication.getName()),
				"Employee Data Fetched Successfully", request.getRequestURI()));
	}

	@PutMapping("/account/status/{employeeId}/{newStatus}")
	@PreAuthorize("hasAnyRole('SYSTEM_ADMIN','HUMAN_RESOURCE')")
	public ResponseEntity<ApiResponse> updateAccountStatus(@PathVariable String employeeId,@PathVariable AccountStatus newStatus, HttpServletRequest request) {
		EmployeeResponseDTO employeeStatus = employeeService.updateAccountStatus(newStatus, employeeId);
		return ResponseEntity.ok(ApiResponse.success(employeeStatus,
				"Employee account status updated with id : " + employeeId,
				request.getRequestURI()));
	}
}