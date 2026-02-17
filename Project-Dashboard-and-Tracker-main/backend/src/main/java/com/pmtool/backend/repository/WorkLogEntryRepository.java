package com.pmtool.backend.repository;

import com.pmtool.backend.DTO.ChartDataDTO;
import com.pmtool.backend.DTO.DetailedReportDTO;
import com.pmtool.backend.DTO.ReportDTO;
import com.pmtool.backend.entity.WorkLogEntry;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository // <-- BEST PRACTICE: Add the @Repository annotation
public interface WorkLogEntryRepository extends JpaRepository<WorkLogEntry, Long> {

	// For the employee's own timesheet view
	List<WorkLogEntry> findByEmployee_UsernameAndDateBetweenOrderByDateAsc(String username, LocalDate startDate,
			LocalDate endDate);

	// For the admin's Master Data (Project-based) report
	@Query("SELECT new com.pmtool.backend.DTO.ReportDTO(e.name, p.name, SUM(w.hoursWorked)) " + "FROM WorkLogEntry w "
			+ "JOIN w.employee e " + "JOIN w.project p " + "WHERE w.date BETWEEN :startDate AND :endDate "
			+ "GROUP BY e.name, p.name " + "ORDER BY e.name, p.name")
	List<ReportDTO> getProjectReportData(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

	// For the admin's Milestone Data report
	@Query("SELECT new com.pmtool.backend.DTO.ReportDTO(e.name, d.name, SUM(w.hoursWorked)) "
			+ "FROM WorkLogEntry w JOIN w.employee e JOIN w.discipline d " + "WHERE w.milestone.id = :milestoneId "
			+ "GROUP BY e.name, d.name")
	List<ReportDTO> getMilestoneReportData(@Param("milestoneId") Long milestoneId);

	// For the Dashboard Bar Chart: Hours per Project
	@Query("SELECT new com.pmtool.backend.DTO.ChartDataDTO(w.employee.employeeId, p.name, SUM(w.hoursWorked)) "
			+ "FROM WorkLogEntry w JOIN w.project p "
			+ "GROUP BY w.employee.employeeId, p.name ORDER BY SUM(w.hoursWorked) DESC")
	List<ChartDataDTO> findHoursPerProject();

	// For the Dashboard Pie Chart: Hours per Employee
	@Query("SELECT new com.pmtool.backend.DTO.ChartDataDTO(e.employeeId, e.name, SUM(w.hoursWorked)) "
			+ "FROM WorkLogEntry w JOIN w.employee e " + "GROUP BY e.employeeId, e.name")
	List<ChartDataDTO> findHoursPerEmployee();

	// For a more advanced, detailed report (if you need it later)
	@Query("SELECT new com.pmtool.backend.DTO.DetailedReportDTO(e.name, d.name, SUM(w.hoursWorked)) "
			+ "FROM WorkLogEntry w JOIN w.employee e JOIN w.discipline d " + "WHERE w.project.id = :projectId "
			+ "AND (:milestoneId IS NULL OR w.milestone.id = :milestoneId) " + "GROUP BY e.name, d.name")
	List<DetailedReportDTO> getDetailedReportData(@Param("projectId") Long projectId,
			@Param("milestoneId") Long milestoneId);

//	List<WorkLogEntry> findByEmployee_Username(String username);
	@Query("""
			SELECT w
			FROM WorkLogEntry w
			WHERE w.employee.username = :username
			AND (
			        w.date = :today
			     OR (w.endTime >= :start AND w.endTime < :end)
			)
			""")
	List<WorkLogEntry> findByDateAndEmployee_Username(@Param("today") LocalDate today,
			@Param("start") LocalDateTime start, @Param("end") LocalDateTime end, @Param("username") String username);

//	@Query("SELECT w FROM WorkLogEntry w " + "JOIN FETCH w.employee " + // always present
//			"LEFT JOIN FETCH w.project " + // optional
//			"LEFT JOIN FETCH w.discipline " + // optional
//			"LEFT JOIN FETCH w.milestone " + // optional
//			"WHERE w.date = :date AND w.employee.username = :username")
//	List<WorkLogEntry> findByDateAndEmployeeUsernameWithAllAssociations(@Param("date") LocalDate date,
//			@Param("username") String username);
//
//	@Query("""
//			    SELECT w FROM WorkLogEntry w
//			    JOIN FETCH w.employee
//			    LEFT JOIN FETCH w.project
//			    LEFT JOIN FETCH w.discipline
//			    LEFT JOIN FETCH w.milestone
//			    WHERE w.employee.username = :username
//			    ORDER BY w.startTime DESC
//			""")
//	List<WorkLogEntry> findLastProjectWorkLog(@Param("username") String username, Pageable pageable);

//	@Query("""
//			    SELECT w FROM WorkLogEntry w
//			    JOIN FETCH w.employee
//			    LEFT JOIN FETCH w.project
//			    LEFT JOIN FETCH w.milestone
//			    LEFT JOIN FETCH w.discipline
//			    WHERE w.date = :date
//			      AND w.employee.username = :username
//			    ORDER BY w.startTime ASC
//			""")
//	List<WorkLogEntry> findTodayLogs(@Param("date") LocalDate date, @Param("username") String username);

	@Query("""
			    SELECT w FROM WorkLogEntry w
			    JOIN FETCH w.employee
			    LEFT JOIN FETCH w.project
			    LEFT JOIN FETCH w.milestone
			    LEFT JOIN FETCH w.discipline
			    WHERE w.employee.username = :username AND ((w.startTime>= :fromDate AND w.startTime<= :toDate) OR (w.endTime>= :fromDate AND w.endTime<= :toDate))
			    ORDER BY w.startTime ASC
			""")
	List<WorkLogEntry> findTodayLogs(@Param("fromDate") LocalDateTime fromDate, @Param("toDate") LocalDateTime toDate,
			@Param("username") String username);

	@Query("""
			    SELECT w FROM WorkLogEntry w
			    JOIN FETCH w.employee
			    LEFT JOIN FETCH w.project
			    LEFT JOIN FETCH w.milestone
			    LEFT JOIN FETCH w.discipline
			    WHERE w.employee.username = :username
			      AND w.date < :date
			      AND w.endTime IS NULL
			    ORDER BY w.startTime DESC
			""")
	List<WorkLogEntry> findLastOpenLogBeforeToday(@Param("date") LocalDate date, @Param("username") String username,
			Pageable pageable);

	WorkLogEntry findFirstByEmployee_UsernameOrderByIdDesc(String username);

}