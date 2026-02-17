package com.pmtool.backend.DTO;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DisciplineDTO {
	private Long id;
	private String name;
	private Long projectId;
	private Long milestoneId;

	private String projectName;
	private String milestoneName;
	private LocalDate dueDate;
	private Double hoursConsumed;

//    public DisciplineDTO() {
//    }
//
//    public DisciplineDTO(Long id, String name) {
//        this.id = id;
//        this.name = name;
//    }
//    public DisciplineDTO(Long id, String name, Long projectId) {
//        this.id = id;
//        this.name = name;
//        this.projectId = projectId;
//    }

}