package com.pmtool.backend.DTO;

import java.time.LocalDate;

import com.pmtool.backend.enums.Role;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class UpdateEmployeeDTO {
	private String name;
	private Long departmentId;
	private Role role;
	private LocalDate joinDate;
	private String location;
	private String bankName;
	private String bankAccountNo;
	private Double salary;
	private LocalDate profPeriodEndDate;
	private String mailId;
	private String managerName;
	private String leadName;
	private String empDeviceCode;
}
