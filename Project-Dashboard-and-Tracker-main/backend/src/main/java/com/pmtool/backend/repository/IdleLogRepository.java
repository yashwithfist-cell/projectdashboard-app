package com.pmtool.backend.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pmtool.backend.entity.IdleLog;

public interface IdleLogRepository extends JpaRepository<IdleLog, Long> {
	@Query("SELECT i FROM IdleLog i WHERE i.employee.username = :username AND i.idleDate = :idleDate")
	List<IdleLog> findByDateAndEmployee(@Param("username") String username, @Param("idleDate") LocalDate idleDate);
	
}
