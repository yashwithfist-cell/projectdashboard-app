package com.pmtool.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pmtool.backend.entity.TimerWorkLog;

public interface TimerWorkLogRepository extends JpaRepository<TimerWorkLog, Long> {

}
