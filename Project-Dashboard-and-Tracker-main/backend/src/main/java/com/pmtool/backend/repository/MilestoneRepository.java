package com.pmtool.backend.repository;

import com.pmtool.backend.DTO.MilestoneResponseDTO; // Corrected package name if needed
import com.pmtool.backend.entity.Milestone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MilestoneRepository extends JpaRepository<Milestone, Long> {

	// Method for cascading dropdowns
	@Query("SELECT m FROM Milestone m WHERE m.project.id = :projectId")
	List<Milestone> findMilestonesByProjectId(@Param("projectId") Long projectId);

	// Method for the admin page to get milestones with calculated hours
//	@Query("SELECT new com.pmtool.backend.DTO"
//			+ ".MilestoneResponseDTO(m.id, m.name, p.name, m.dueDate, SUM(a.hoursWorked)) " + "FROM Milestone m "
//			+ "JOIN m.project p " + "LEFT JOIN m.workLogEntries w " + "GROUP BY m.id, m.name, p.name, m.dueDate "
//			+ "ORDER BY p.name, m.name")
	@Query("""
			SELECT new com.pmtool.backend.DTO.MilestoneResponseDTO(
			    m.id,
			    m.name,
			    p.name,
			    m.dueDate,
			    COALESCE(SUM(a.totalWorkedSeconds), 0) / 3600.0
			)
			FROM Milestone m
			JOIN m.project p
			LEFT JOIN m.projectAssignments a
			GROUP BY m.id, m.name, p.name, m.dueDate
			ORDER BY p.name, m.name
			""")
	List<MilestoneResponseDTO> findAllWithHoursConsumed();
	
	@Query("SELECT m FROM Milestone m WHERE m.project.id IN :projectIds")
	List<Milestone> findMilestonesByProjectIds(@Param("projectIds") List<Long> projectIds);

}