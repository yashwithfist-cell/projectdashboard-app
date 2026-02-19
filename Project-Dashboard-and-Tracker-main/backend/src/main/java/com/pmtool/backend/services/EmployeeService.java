package com.pmtool.backend.services;

import com.pmtool.backend.DTO.CreateEmployeeDTO;
import com.pmtool.backend.DTO.EmployeeResponseDTO;
import com.pmtool.backend.entity.Department;
import com.pmtool.backend.entity.Employee;
import com.pmtool.backend.enums.Role;
import com.pmtool.backend.exception.EmployeeNotFoundException;
import com.pmtool.backend.repository.DepartmentRepository;
import com.pmtool.backend.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.pmtool.backend.DTO.CreateEmployeeDTO; // You will need to update this DTO
import com.pmtool.backend.DTO.UpdateEmployeeDTO; // You will need to create this DTO
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

	@Autowired
	private EmployeeRepository employeeRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired // <-- FIX: You were missing this injection
	private DepartmentRepository departmentRepository;

	@Transactional
	public EmployeeResponseDTO createEmployee(CreateEmployeeDTO dto) {
		if (employeeRepository.findByUsername(dto.getUsername()).isPresent()) {
			throw new RuntimeException("Username already exists!");
		}
		Department department = departmentRepository.findById(dto.getDepartmentId())
				.orElseThrow(() -> new RuntimeException("Department not found"));

		Employee newEmployee = Employee.builder().employeeId(dto.getEmployeeId()).name(dto.getName())
				.username(dto.getUsername()).password(dto.getPassword()).role(dto.getRole()).department(department)
				.joinDate(dto.getJoinDate()).bankAccountNo(dto.getBankAccountNo()).bankName(dto.getBankName())
				.salary(dto.getSalary()).location(dto.getLocation()).profPeriodEndDate(dto.getProfPeriodEndDate())
				.mailId(dto.getMailId()).mgrName(dto.getManagerName()).teamLeadName(dto.getLeadName())
				.empDeviceCode(dto.getEmpDeviceCode()).build();
//        newEmployee.setEmployeeId(dto.getEmployeeId());
//        newEmployee.setName(dto.getName());
//        newEmployee.setUsername(dto.getUsername());
//        newEmployee.setPassword(dto.getPassword());
//        newEmployee.setRole(dto.getRole());
//        newEmployee.setDepartment(department);

		Employee savedEmployee = employeeRepository.save(newEmployee);
		return new EmployeeResponseDTO(savedEmployee);
	}

	// <-- FIX: The duplicate method is now removed. This is the only
	// getAllEmployees() method.
	@Transactional(readOnly = true) // <-- FIX: Correct annotation syntax
	public List<EmployeeResponseDTO> getAllEmployees() {
		return employeeRepository.findAll().stream().map(EmployeeResponseDTO::new).collect(Collectors.toList());
	}

	// --- We will add the deleteEmployee method here in the next step to fix the
	// controller error ---
	@Transactional
	public void deleteEmployee(String employeeId) {
		if (!employeeRepository.existsById(employeeId)) {
			throw new RuntimeException("Employee not found with id: " + employeeId);
		}
		employeeRepository.deleteById(employeeId);
	}

	@Transactional
	public EmployeeResponseDTO updateEmployee(String employeeId, UpdateEmployeeDTO dto) {
		Employee existingEmployee = employeeRepository.findById(employeeId)
				.orElseThrow(() -> new RuntimeException("Employee not found"));

		Department department = departmentRepository.findById(dto.getDepartmentId())
				.orElseThrow(() -> new RuntimeException("Department not found"));

		// Update fields from the DTO
		existingEmployee.setName(dto.getName());
		existingEmployee.setDepartment(department);
		existingEmployee.setRole(dto.getRole());
		existingEmployee.setJoinDate(dto.getJoinDate());
		;
		existingEmployee.setBankAccountNo(dto.getBankAccountNo());
		existingEmployee.setBankName(dto.getBankName());
		existingEmployee.setLocation(dto.getLocation());
		existingEmployee.setSalary(dto.getSalary());
		existingEmployee.setProfPeriodEndDate(dto.getProfPeriodEndDate());
		existingEmployee.setMailId(dto.getMailId());
		existingEmployee.setMgrName(dto.getManagerName());
		existingEmployee.setTeamLeadName(dto.getLeadName());
		existingEmployee.setEmpDeviceCode(dto.getEmpDeviceCode());

		// You can add more fields to update here as needed

		Employee updatedEmployee = employeeRepository.save(existingEmployee);
		return new EmployeeResponseDTO(updatedEmployee);
	}

	public List<String> getEmployeesByRole(Role role) {
		List<Employee> employeeList = employeeRepository.findByRole(role);
		return employeeList.stream().map(emp -> emp.getUsername()).toList();
	}

	public List<EmployeeResponseDTO> getManagersByRole(Role role) {
		List<Employee> employeeList = employeeRepository.findByRole(role);
		return employeeList.stream()
				.map(emp -> EmployeeResponseDTO.builder().employeeId(emp.getEmployeeId()).name(emp.getName()).build())
				.toList();
	}

	public EmployeeResponseDTO getEmployeeByUsername(String name) {
		Employee employee = employeeRepository.findByUsername(name)
				.orElseThrow(() -> new EmployeeNotFoundException("Employee Not Found with username : " + name));
		return new EmployeeResponseDTO(employee);
	}

	public Page<EmployeeResponseDTO> getEmployees(int page, int size, String sortField, String sortDir, String search) {
		Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortField).descending() : Sort.by(sortField).ascending();
		Pageable pageable = PageRequest.of(page, size, sort);

		if (search != null && !search.isBlank()) {
			return employeeRepository
					.findByNameContainingIgnoreCaseOrEmployeeIdContainingIgnoreCase(search, search, pageable)
					.map(EmployeeResponseDTO::new);
		}

		return employeeRepository.findAll(pageable).map(EmployeeResponseDTO::new);
	}

}