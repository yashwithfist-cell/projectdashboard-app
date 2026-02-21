package com.pmtool.backend.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pmtool.backend.DTO.response.TimeLineResponseDto;
import com.pmtool.backend.entity.TimeLineSummary;

public interface TimLineRepository extends JpaRepository<TimeLineSummary, String> {

	public Optional<TimeLineSummary> findByStartTimeAndEndTimeAndEmployee_Username(LocalDateTime startTime,
			LocalDateTime endTime, String username);

	public Optional<List<TimeLineSummary>> findByDateAndEmployee_Username(LocalDate date, String name);

	public Optional<TimeLineSummary> findByCurrentProject(boolean currentProject);

	public List<TimeLineSummary> findByDateAndEmployeeEmployeeIdOrderByEndTimeAscStartTimeAsc(LocalDate date,
			String employeeId);

	@Query("""
			SELECT t FROM TimeLineSummary t
			WHERE (t.startTime = :startTime AND t.endTime = :endTime AND t.employee.username = :username)
			   OR (t.startTime = :startTime AND t.employee.username = :username)
			""")
	Optional<TimeLineSummary> findTimeline(@Param("startTime") LocalDateTime startTime,
			@Param("endTime") LocalDateTime endTime, @Param("username") String username);

	@Query("""
			SELECT new com.pmtool.backend.DTO.response.TimeLineResponseDto(
			    t.project.name,
			    SUM(t.durationMillis),
			    t.project.estimatedHours
			)
			FROM TimeLineSummary t
			GROUP BY t.project.name,t.project.estimatedHours
			""")
	public List<TimeLineResponseDto> findHoursByProject();

	@Query("""
			    SELECT new com.pmtool.backend.DTO.response.TimeLineResponseDto(
			        t.employee.employeeId,
			        t.employee.name,
			        SUM(t.durationMillis)
			    )
			    FROM TimeLineSummary t
			    WHERE t.type = 'Project' AND t.employee.role = com.pmtool.backend.enums.Role.EMPLOYEE
			    GROUP BY t.employee.employeeId,t.employee.name
			""")
	public List<TimeLineResponseDto> findHoursByEmployee();

	Optional<TimeLineSummary> findTopByEmployeeEmployeeIdAndDateOrderByEndTimeDesc(String employeeId, LocalDate date);

	long countByEmployeeEmployeeIdAndDate(String employeeId, LocalDate date);

	long deleteByEmployeeEmployeeIdAndDate(String employeeId, LocalDate date);

	@Query("""
			    SELECT new com.pmtool.backend.DTO.response.TimeLineResponseDto(
			       t.employee.employeeId,
			      t.employee.name,
			      t.project.id,
			      t.project.name,
			      SUM(t.durationMillis)
			    )
			    FROM TimeLineSummary t
			    WHERE t.type = 'Project' AND t.employee.role = com.pmtool.backend.enums.Role.EMPLOYEE
			     GROUP BY
			      t.employee.employeeId,
			      t.employee.name,
			      t.project.id,
			      t.project.name
			  ORDER BY
			      t.employee.name,
			      t.project.name
			""")
	public List<TimeLineResponseDto> findHoursByEmployeeAndProject();

	@Query("""
			SELECT new com.pmtool.backend.DTO.response.TimeLineResponseDto(
			    t.project.name,
			    SUM(t.durationMillis)
			)
			FROM TimeLineSummary t
			WHERE t.date BETWEEN :start AND :end
			GROUP BY t.project.name
			""")
	public List<TimeLineResponseDto> findMonthlyHoursByProject(LocalDate start, LocalDate end);

}
