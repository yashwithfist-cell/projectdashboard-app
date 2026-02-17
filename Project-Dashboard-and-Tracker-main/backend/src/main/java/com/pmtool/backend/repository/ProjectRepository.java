package com.pmtool.backend.repository;

import com.pmtool.backend.DTO.ProjectResponseDTO;
import com.pmtool.backend.entity.Discipline;
import com.pmtool.backend.entity.Project;
import com.pmtool.backend.entity.WorkLogEntry;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

	@Query("SELECT d FROM Discipline d WHERE d.project.id = :projectId")
	List<Discipline> findDisciplinesByProjectId(@Param("projectId") Long projectId);

//	@Query("SELECT new com.pmtool.backend.DTO.ProjectResponseDTO( p.id, p.name, p.clientName,SUM(w.hoursWorked), p.employee.employeeId) FROM WorkLogEntry w RIGHT JOIN w.project p GROUP BY p.id,p.name,p.clientName, p.employee.employeeId ORDER BY p.name")
	@Query("""
			SELECT new com.pmtool.backend.DTO.ProjectResponseDTO(
			    p.id,
			    p.name,
			    p.clientName,
			    COALESCE(SUM(a.totalWorkedSeconds), 0) / 3600.0,
			    p.employee.employeeId
			)
			FROM Project p
			LEFT JOIN p.assignments a
			GROUP BY p.id, p.name, p.clientName, p.employee.employeeId
			ORDER BY p.name
			""")

	List<ProjectResponseDTO> findAllWithHoursConsumed();

	@Query("SELECT p FROM Project p WHERE p.employee.username = :username GROUP BY p.id")
	List<Project> findAllByUserName(@Param("username") String username);

	@Query("""
			  SELECT DISTINCT p
			  FROM Project p
			  LEFT JOIN FETCH p.assignments pa
			  WHERE pa.employee.username = :username
			""")
	List<Project> findAllProjectsByTeamLead(@Param("username") String username);

	@Query("""
			SELECT DISTINCT p
			FROM Project p
			JOIN FETCH p.milestones m
			JOIN FETCH m.disciplines d
			WHERE p.name = :projectName
			AND m.name = :milestoneName
			AND d.name = :disciplineName
			""")
	Optional<Project> findProjectWithMilestoneAndDiscipline(String projectName, String milestoneName,
			String disciplineName);

}
