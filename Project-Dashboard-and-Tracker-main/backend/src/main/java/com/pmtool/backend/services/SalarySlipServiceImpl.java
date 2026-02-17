package com.pmtool.backend.services;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.pmtool.backend.DTO.SalarySlipDto;
import com.pmtool.backend.constants.SalarySlipConstants;
import com.pmtool.backend.entity.Employee;
import com.pmtool.backend.exception.EmployeeNotFoundException;
import com.pmtool.backend.repository.EmployeeRepository;
import com.pmtool.backend.repository.LeaveRepository;

@Service
public class SalarySlipServiceImpl implements SalarySlipService {

	@Autowired
	private EmployeeRepository employeeRepository;

	@Autowired
	LeaveRepository leaveRepository;

	private record TaxSlab(double limit, double percent) {
	}

	@Override
	public SalarySlipDto calculateSalary(String username, String date) {
		double profTax = 0.0;
		String[] parts = date.split("-");
		int year = Integer.parseInt(parts[0]);
		int month = Integer.parseInt(parts[1]);
		Employee emp = employeeRepository.findByUsername(username)
				.orElseThrow(() -> new EmployeeNotFoundException("Employee not found with username : " + username));
		double yearlySal = (emp.getSalary() == null) ? 0.0 : emp.getSalary();

		List<TaxSlab> slabs = List.of(new TaxSlab(400000, 0), new TaxSlab(800000, 5), new TaxSlab(1200000, 10),
				new TaxSlab(1600000, 15), new TaxSlab(2000000, 20), new TaxSlab(2400000, 25),
				new TaxSlab(Double.MAX_VALUE, 30));

		double percent = 0;
		for (TaxSlab s : slabs) {
			if (yearlySal <= s.limit()) {
				percent = s.percent();
				break;
			}
		}
		double yearlyTds = (yearlySal * percent) / 100;
		double baseSalary = yearlySal / 12;
		LocalDate startOfMonth = LocalDate.of(year, month, 1);
		LocalDate endOfMonth = startOfMonth.withDayOfMonth(startOfMonth.lengthOfMonth());
		double unpaidLeaves = leaveRepository.sumUnpaidLeaveByUsername(username, startOfMonth, endOfMonth).orElse(0.0);
		int workingDays = startOfMonth.lengthOfMonth();
		double perDaySalary = baseSalary / workingDays;

		double deduction = unpaidLeaves * perDaySalary;
		double netSalary = baseSalary - deduction;
		if (netSalary >= 25000) {
			profTax = 200;
//			netSalary = netSalary - profTax;
		}
		double specialHra = Math.round((baseSalary * 25) / 100);
		return SalarySlipDto.builder().bankAccountNo(emp.getBankAccountNo()).bankName(emp.getBankName().toUpperCase())
				.location(emp.getLocation().toUpperCase()).salary(Math.round(netSalary * 100.0) / 100.0)
				.employeeName(emp.getName().toUpperCase()).companyName(SalarySlipConstants.COMPANY_NAME)
				.month(Month.of(month).name().toUpperCase()).department(emp.getDepartment().getName().toUpperCase())
				.professionalTax(profTax).tds(Math.round(yearlyTds / 12.0)).totalDays(workingDays - unpaidLeaves)
				.hra(specialHra).special(specialHra).basic((baseSalary * 50) / 100).lopDays(unpaidLeaves * perDaySalary)
				.joiningDate(emp.getJoinDate()).employeeId(emp.getEmployeeId()).build();

	}
}
