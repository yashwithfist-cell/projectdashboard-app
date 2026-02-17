package com.pmtool.backend.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class MilestoneDTO {
    private Long id;
    private String name;
    private Long projectId;
    private LocalDate dueDate; // <-- ADD THIS FIELD
    private Long disciplineId;

    // This constructor is useful for simple lists
    public MilestoneDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    // This constructor is useful for richer data
    public MilestoneDTO(Long id, String name, Long projectId) {
        this.id = id;
        this.name = name;
        this.projectId = projectId;

    }
}