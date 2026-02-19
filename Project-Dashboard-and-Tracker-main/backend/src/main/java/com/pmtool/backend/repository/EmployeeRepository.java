package com.pmtool.backend.repository;

import com.pmtool.backend.entity.Employee;
import com.pmtool.backend.enums.Role;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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

	Page<Employee> findByNameContainingIgnoreCaseOrEmployeeIdContainingIgnoreCase(String name, String employeeId,
			Pageable pageable);

}
