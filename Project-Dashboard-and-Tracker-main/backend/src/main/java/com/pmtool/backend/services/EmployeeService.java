package com.pmtool.backend.services;

import com.pmtool.backend.DTO.CreateEmployeeDTO;
import com.pmtool.backend.DTO.EmployeeResponseDTO;
import com.pmtool.backend.entity.Department;
import com.pmtool.backend.entity.Employee;
import com.pmtool.backend.enums.AccountStatus;
import com.pmtool.backend.enums.Role;
import com.pmtool.backend.exception.EmployeeNotFoundException;
import com.pmtool.backend.repository.DepartmentRepository;
import com.pmtool.backend.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import com.pmtool.backend.DTO.CreateEmployeeDTO; // You will need to update this DTO
import com.pmtool.backend.DTO.UpdateEmployeeDTO; // You will need to create this DTO

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

	@Autowired
	private EmployeeRepository employeeRepository;

	@Autowired
	private DepartmentRepository departmentRepository;

	@Transactional
	public EmployeeResponseDTO createEmployee(CreateEmployeeDTO dto) {
		if (employeeRepository.findByUsername(dto.getUsername()).isPresent()) {
			throw new RuntimeException("Username already exists!");
		}
		Department department = departmentRepository.findById(dto.getDepartmentId())
				.orElseThrow(() -> new RuntimeException("Department not found"));

		Employee newEmployee = new Employee();
		newEmployee.setEmployeeId(dto.getEmployeeId());
		newEmployee.setName(dto.getName());
		newEmployee.setUsername(dto.getUsername());
		newEmployee.setPassword(dto.getPassword());
		newEmployee.setRole(dto.getRole());
		newEmployee.setDepartment(department);
		newEmployee.setJoinDate(dto.getJoinDate());
		newEmployee.setBankAccountNo(dto.getBankAccountNo());
		newEmployee.setBankName(dto.getBankName());
		newEmployee.setSalary(dto.getSalary());
		newEmployee.setLocation(dto.getLocation());
		newEmployee.setProfPeriodEndDate(dto.getProfPeriodEndDate());
		newEmployee.setMailId(dto.getMailId());
		newEmployee.setMgrName(dto.getManagerName());
		newEmployee.setTeamLeadName(dto.getLeadName());
		newEmployee.setEmpDeviceCode(dto.getEmpDeviceCode());

		Employee savedEmployee = employeeRepository.save(newEmployee);
		return new EmployeeResponseDTO(savedEmployee);
	}

	@Transactional(readOnly = true)
	public List<EmployeeResponseDTO> getAllEmployees(String search) {
		if (search != null && !search.isBlank()) {
			return employeeRepository.findByNameContainingIgnoreCaseOrEmployeeIdContainingIgnoreCase(search, search)
					.stream().sorted(Comparator.comparing((Employee e) -> getPriority(e.getEmployeeId()))
							.thenComparing(Employee::getEmployeeId))
					.map(EmployeeResponseDTO::new).toList();
		}
		List<EmployeeResponseDTO> empResList = employeeRepository.findAll().stream().sorted(Comparator
				.comparing((Employee e) -> getPriority(e.getEmployeeId())).thenComparing(Employee::getEmployeeId))
				.map(EmployeeResponseDTO::new).toList();
		return empResList;
	}

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

	public EmployeeResponseDTO updateAccountStatus(AccountStatus newStatus, String employeeId) {
		Employee employee = employeeRepository.findById(employeeId)
				.orElseThrow(() -> new UsernameNotFoundException("Employee not found with id : " + employeeId));
		employee.setStatus(newStatus);
		Employee employeeResponse = employeeRepository.save(employee);
		return new EmployeeResponseDTO(employeeResponse);
	}

	private int getPriority(String id) {
		if (id.startsWith("FES") && !id.startsWith("FEST")) {
			return 1; // First group
		} else if (id.startsWith("FEST")) {
			return 2; // Second group
		} else {
			return 3; // Others
		}
	}

}