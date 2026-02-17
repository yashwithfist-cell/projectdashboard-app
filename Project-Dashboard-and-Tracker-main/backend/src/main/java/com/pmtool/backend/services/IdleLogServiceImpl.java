package com.pmtool.backend.services;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import com.pmtool.backend.DTO.request.IdleLogDtoRequest;
import com.pmtool.backend.DTO.response.IdleLogDtoResponse;
import com.pmtool.backend.entity.Employee;
import com.pmtool.backend.entity.IdleLog;
import com.pmtool.backend.repository.EmployeeRepository;
import com.pmtool.backend.repository.IdleLogRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IdleLogServiceImpl implements IdleLogService {

	private final IdleLogRepository repo;

	private final EmployeeRepository empRepo;

//	@Override
//	public IdleLogDto startIdleSession(IdleLogDto dto, Authentication authentication) {
//		IdleLog log = repo.findByDateAndEmployee(authentication.getName(), dto.getIdleDate());
//		if (log != null) {
////			 log = repo.findByDateAndEmployee(authentication.getName(), dto.getIdleDate());
//			Employee emp = empRepo.findByUsername(authentication.getName())
//					.orElseThrow(() -> new RuntimeException("Employee Not Found !!!!"));
//
//			log.setIdleDate(dto.getIdleDate());
//			log.setIdleStartTime(dto.getIdleStartTime());
//			log.setIdleEndTime(dto.getIdleStartTime());
//			log.setIdleDurationMillis(log.getIdleDurationMillis());
//			log.setEmployee(emp);
//		} else {
//
//			Employee emp = empRepo.findByUsername(authentication.getName())
//					.orElseThrow(() -> new RuntimeException("Employee Not Found !!!!"));
//			log = new IdleLog();
//			log.setIdleDate(LocalDate.now());
//			log.setIdleStartTime(dto.getIdleStartTime());
//			log.setIdleEndTime(dto.getIdleStartTime());
//			log.setIdleDurationMillis(0L);
//			log.setEmployee(emp);
//		}
//		IdleLog idleLog = repo.save(log);
//		return IdleLogDto.builder().idleLogId(idleLog.getIdleLogId()).idleDate(idleLog.getIdleDate())
//				.idleStartTime(idleLog.getIdleStartTime()).idleEndTime(idleLog.getIdleEndTime())
//				.idleDurationMillis(idleLog.getIdleDurationMillis()).build();
//	}

	@Override
	public IdleLogDtoResponse endIdleSession(IdleLogDtoRequest dto, Authentication authentication) {
		Employee emp = empRepo.findByUsername(authentication.getName())
				.orElseThrow(() -> new RuntimeException("Employee Not Found !!!!"));
		IdleLog log = new IdleLog();
		log.setIdleDate(dto.getIdleDate());
		LocalDateTime idleStart = Instant.ofEpochMilli(dto.getIdleStartTime()).atZone(ZoneId.systemDefault())
				.toLocalDateTime();

		LocalDateTime idleEnd = Instant.ofEpochMilli(dto.getIdleEndTime()).atZone(ZoneId.systemDefault())
				.toLocalDateTime();
		log.setIdleStartTime(idleStart);
		log.setIdleEndTime(idleEnd);
		log.setIdleDurationMillis(dto.getIdleDurationMillis());
		log.setEmployee(emp);

		IdleLog idleLog = repo.save(log);
		return IdleLogDtoResponse.builder().idleLogId(idleLog.getIdleLogId()).idleDate(idleLog.getIdleDate())
				.idleStartTime(idleLog.getIdleStartTime()).idleEndTime(idleLog.getIdleEndTime())
				.idleDurationMillis(idleLog.getIdleDurationMillis()).build();
	}

	@Override
	public Map<Long, List<IdleLogDtoResponse>> getIdleLog(LocalDate idleDate, Authentication authentication) {
		List<IdleLog> logs = repo.findByDateAndEmployee(authentication.getName(), idleDate);
		List<IdleLogDtoResponse> logResponseList = logs.stream()
				.map(log -> IdleLogDtoResponse.builder().idleLogId(log.getIdleLogId()).idleDate(log.getIdleDate())
						.idleStartTime(log.getIdleStartTime()).idleEndTime(log.getIdleEndTime())
						.idleDurationMillis(log.getIdleDurationMillis()).build())
				.toList();
		Long totalIdleDurMillis = logResponseList.stream().mapToLong(IdleLogDtoResponse::getIdleDurationMillis).sum();
		Map<Long, List<IdleLogDtoResponse>> idleLogMap = new HashMap<Long, List<IdleLogDtoResponse>>();
		idleLogMap.put(totalIdleDurMillis, logResponseList);
		return idleLogMap;
	}

}
