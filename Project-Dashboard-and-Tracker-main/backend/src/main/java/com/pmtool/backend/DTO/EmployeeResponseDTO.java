package com.pmtool.backend.DTO;

import com.pmtool.backend.entity.Employee;
import com.pmtool.backend.enums.AccountStatus;
import com.pmtool.backend.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmployeeResponseDTO {
	// All fields from the entity EXCEPT the password
	private String employeeId;
	private String name;
	private String username;
	private Role role;
	private String departmentName;

	private String contactNo;
	private Integer age;
	private LocalDate dateOfBirth;
	private String location;
	private String fatherName;
	private String motherName;

	private String aadharNo;
	private String panNo;

	private String bankName;
	private String bankAccountNo;
	private String ifscCode;
	private String branch;
	private Double salary;
	private LocalDate joinDate;
	private LocalDate profPeriodEndDate;
	private String mailId;
	private String managerName;
	private String leadName;
	private String empDeviceCode;
	private String status;

	public EmployeeResponseDTO(Employee employee) {
		this.employeeId = employee.getEmployeeId();
		this.name = employee.getName();
		this.username = employee.getUsername();
		this.role = employee.getRole();
		this.departmentName = employee.getDepartment() != null ? employee.getDepartment().getName() : "N/A";
		this.bankAccountNo = employee.getBankAccountNo();
		this.bankName = employee.getBankName();
		this.salary = employee.getSalary();
		this.location = employee.getLocation();
		this.joinDate = employee.getJoinDate();
		this.profPeriodEndDate = employee.getProfPeriodEndDate();
		this.mailId = employee.getMailId();
		this.managerName = employee.getMgrName();
		this.leadName = employee.getTeamLeadName();
		this.empDeviceCode = employee.getEmpDeviceCode();
		this.status = employee.getStatus().name();
		// Map all the new fields
	}
}