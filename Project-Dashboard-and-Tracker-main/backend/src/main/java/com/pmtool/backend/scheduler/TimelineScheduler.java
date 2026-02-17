package com.pmtool.backend.scheduler;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.pmtool.backend.DTO.TimelineRowDTO;
import com.pmtool.backend.services.TimeLineService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@EnableScheduling
@Slf4j
public class TimelineScheduler {

	private final TimeLineService timeLineService;

	@Scheduled(cron = "0 59 23 * * ?", zone = "Asia/Kolkata")
	public void saveTimeline() {
		Map<String, List<TimelineRowDTO>> timelineMap = timeLineService.buildTimelineForDay();
		timeLineService.saveTimelineForDay(timelineMap);
		log.info("Time Line Summary Saved Successfully on " + LocalDate.now());
	}
}
