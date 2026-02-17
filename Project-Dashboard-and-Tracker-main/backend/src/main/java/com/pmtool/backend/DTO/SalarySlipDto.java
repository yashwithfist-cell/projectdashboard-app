package com.pmtool.backend.DTO;

import java.time.LocalDate;
import java.time.LocalTime;

import com.pmtool.backend.entity.Employee;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalarySlipDto {
	private Long id;
	private String month;
	private double basic;
	private double da;
	private double hra;
	private double conveyance;
	private double medical;
	private double special;
	private double professionalTax;
	private double tds;
	private double providentFund;
	private double totalDays;
	private double netSalary;
	private String employeeId;
	private String department;
	private String employeeName;
	private String location;
	private String bankName;
	private String bankAccountNo;
	private Double salary;
	private String companyName;
	private LocalDate joiningDate;
	private double lopDays;
	private double lopAmount;
	private double esi;
	private double employerEsi;
	private double employeeEsi;
}
