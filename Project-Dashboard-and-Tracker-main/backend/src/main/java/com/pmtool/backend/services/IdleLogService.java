package com.pmtool.backend.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;

import com.pmtool.backend.DTO.request.IdleLogDtoRequest;
import com.pmtool.backend.DTO.response.IdleLogDtoResponse;

public interface IdleLogService {
//	IdleLogDto startIdleSession(IdleLogDto dto,Authentication authentication);
	IdleLogDtoResponse endIdleSession(IdleLogDtoRequest dto,Authentication authentication);

	Map<Long, List<IdleLogDtoResponse>> getIdleLog(LocalDate idleDate, Authentication authentication);

}
