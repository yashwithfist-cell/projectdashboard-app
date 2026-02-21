package com.pmtool.backend.repository;

import com.pmtool.backend.entity.Employee;
import com.pmtool.backend.enums.Role;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, String> {
	Optional<Employee> findByUsername(String username);

//    Optional <Employee> findByEmployeeId(String id);
	boolean existsByUsername(String username);

	boolean existsByEmployeeId(String employeeId);

	List<Employee> findByRole(Role role);

	Employee findByEmployeeId(String id);

	Optional<List<Employee>> findByProfPeriodEndDate(LocalDate probDate);

	Employee findByEmpDeviceCode(String empDeviceCode);

	List<Employee> findByNameContainingIgnoreCaseOrEmployeeIdContainingIgnoreCase(String name, String employeeId);

//	@Query(value = """
//			SELECT * FROM employees e
//			WHERE LOWER(e.employee_name) LIKE LOWER(CONCAT('%', :search, '%'))
//			   OR LOWER(e.employee_id) LIKE LOWER(CONCAT('%', :search, '%'))
//			ORDER BY
//			   REGEXP_REPLACE(e.employee_id, '\\d+', '') ASC,
//			   CAST(REGEXP_REPLACE(e.employee_id, '\\D+', '', 'g') AS INTEGER) ASC
//			""", countQuery = """
//			SELECT COUNT(*) FROM employees e
//			WHERE LOWER(e.employee_name) LIKE LOWER(CONCAT('%', :search, '%'))
//			   OR LOWER(e.employee_id) LIKE LOWER(CONCAT('%', :search, '%'))
//			""", nativeQuery = true)
//	Page<Employee> searchEmployeesSorted(@Param("search") String search, Pageable pageable);
//
//	@Query(value = """
//			SELECT * FROM employees e
//			ORDER BY
//			    REGEXP_REPLACE(e.employee_id, '\\d+', '') ASC,
//			    CAST(REGEXP_REPLACE(e.employee_id, '\\D+', '', 'g') AS INTEGER) ASC
//			""", countQuery = "SELECT COUNT(*) FROM employees", nativeQuery = true)
//	Page<Employee> findAllSorted(Pageable pageable);

}
