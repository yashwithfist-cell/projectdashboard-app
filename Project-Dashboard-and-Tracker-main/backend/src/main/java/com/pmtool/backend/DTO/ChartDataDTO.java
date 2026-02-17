package com.pmtool.backend.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChartDataDTO {
	private String employeeId;
	private String name;
	private Double value;

//	public ChartDataDTO(String employeeId, String name, Double value) {
//		this.name = name;
//		this.value = value;
//		this.employeeId = employeeId;
//	}

	public String getName() {
		return this.name;
	}

	public Double getValue() {
		return this.value;
	}

	public String getEmployeeId() {
		return employeeId;
	}

}
