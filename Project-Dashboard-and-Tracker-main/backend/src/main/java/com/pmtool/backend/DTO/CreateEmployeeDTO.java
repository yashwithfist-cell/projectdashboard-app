package com.pmtool.backend.DTO;

import com.pmtool.backend.enums.Role;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateEmployeeDTO {
	private String employeeId;
	private String name;
	private String username;
	private String password;
	private Role role;
	private Long departmentId;

	// Family Information
	private String fatherName;
	private String motherName;

	// Document Information
	private String aadharNo;
	private String panNo;

	// Bank Information
	private String bankName;
	private String bankAccountNo;
	private String ifscCode;
	private String branch;
	private LocalDate joinDate;
	private Double salary;
	private String location;
	private LocalDate profPeriodEndDate;
	private String mailId;
	private String managerName;
	private String leadName;
	private String empDeviceCode;

}