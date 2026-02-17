package com.pmtool.backend.services;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.itextpdf.text.pdf.PdfStructTreeController.returnType;
import com.pmtool.backend.DTO.AttendanceLogDto;
import com.pmtool.backend.DTO.CheckOutDTO;
import com.pmtool.backend.DTO.TimelineRowDTO;
import com.pmtool.backend.DTO.WorkLogResponseDTO;
import com.pmtool.backend.DTO.response.IdleLogDtoResponse;
import com.pmtool.backend.DTO.response.TimeLineResponseDto;
import com.pmtool.backend.component.TimelineBuilder;
import com.pmtool.backend.entity.Employee;
import com.pmtool.backend.entity.IdleLog;
import com.pmtool.backend.entity.Project;
import com.pmtool.backend.entity.TimeLineSummary;
import com.pmtool.backend.entity.WorkLogEntry;
import com.pmtool.backend.exception.BiometricFetchFailureException;
import com.pmtool.backend.exception.EmployeeNotFoundException;
import com.pmtool.backend.exception.ResourceNotFoundException;
import com.pmtool.backend.exception.TimeLineException;
import com.pmtool.backend.repository.EmployeeRepository;
import com.pmtool.backend.repository.IdleLogRepository;
import com.pmtool.backend.repository.ProjectRepository;
import com.pmtool.backend.repository.TimLineRepository;
import com.pmtool.backend.repository.WorkLogEntryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TimeLineServiceImpl implements TimeLineService {

	private final TimelineBuilder timelineBuilder;

	private final EmployeeRepository employeeRepo;

	private final SoapClientService soapClientService;

	private final WorkLogEntryRepository workLogRepository;

	private final IdleLogRepository idleLogRepository;

	private final TimLineRepository timeLineRepo;

	private final ProjectRepository projectRepository;

//	int count = 0;

	@Override
	public Map<String, List<TimelineRowDTO>> buildTimelineForDay() {

		List<Employee> employeeList = employeeRepo.findAll();

		LocalDate today = LocalDate.now();
		LocalDateTime now = LocalDateTime.now();

		Map<String, List<TimelineRowDTO>> allTimeLinesMap = new HashMap<>();

		for (Employee e : employeeList) {

			AttendanceLogDto dto;
			try {
				dto = soapClientService.fetchEmployeeData(e.getUsername());
			} catch (Exception ex) {
				log.error("Biometric failed for {}", e.getUsername(), ex);
				continue;
			}

			if (dto == null || dto.getInList().isEmpty() || dto.getInList() == null) {
				continue;
			}

			List<IdleLog> idleLogList = idleLogRepository.findByDateAndEmployee(e.getUsername(), today);

			List<IdleLogDtoResponse> idleLogRespList = idleLogList.stream()
					.map(log -> IdleLogDtoResponse.builder().idleLogId(log.getIdleLogId()).idleDate(log.getIdleDate())
							.idleDurationMillis(log.getIdleDurationMillis()).idleStartTime(log.getIdleStartTime())
							.idleEndTime(log.getIdleEndTime()).build())
					.toList();

			List<WorkLogEntry> todayLogs = workLogRepository.findTodayLogs(
					LocalDateTime.of(today, LocalTime.of(00, 00, 00)),
					LocalDateTime.of(today, LocalTime.of(23, 59, 59)), e.getUsername());

//			List<WorkLogEntry> lastOpen;
//			if (!todayLogs.isEmpty()) {
//				lastOpen = workLogRepository.findLastOpenLogBeforeToday(today, e.getUsername(), PageRequest.of(0, 1));
//				lastOpen.stream().forEach(lastOpenProject -> {
//					lastOpenProject.setStartTime(dto.getFirstCheckIn().toLocalTime());
//					lastOpenProject.setEndTime(todayLogs.stream()
//							.sorted(Comparator.comparing(WorkLogEntry::getStartTime)).findFirst().get().getStartTime());
//				});
//			} else {
//				lastOpen = workLogRepository.findLastOpenLogBeforeToday(today, e.getUsername(), PageRequest.of(0, 1));
//				lastOpen.stream().forEach(lastOpenProject -> {
//					lastOpenProject.setStartTime(dto.getFirstCheckIn().toLocalTime());
//					lastOpenProject.setEndTime(dto.getLastCheckOut().toLocalTime());
//				});
//			}

			List<WorkLogResponseDTO> workLogRespList = todayLogs.stream().map(WorkLogResponseDTO::new).toList();
			List<TimelineRowDTO> timeline = timelineBuilder.build(dto.getInList(), dto.getOutList(), idleLogRespList,
					workLogRespList, now);

			allTimeLinesMap.put(e.getEmployeeId(), timeline);
		}

		return allTimeLinesMap;
	}

	@Override
	public void saveTimelineForDay(Map<String, List<TimelineRowDTO>> timelineMap) {
		LocalDate today = LocalDate.now();
		timelineMap.forEach((empId, timelineList) -> {
			Employee employee = employeeRepo.findById(empId)
					.orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id : " + empId));
			List<TimeLineSummary> summaries = timelineList.stream().filter(timeline -> {
				LocalDateTime startTime = today.atTime(timeline.getStartMin() / 60, timeline.getStartMin() % 60);
				LocalDateTime endTime = today.atTime(timeline.getEndMin() / 60, timeline.getEndMin() % 60);

				return timeLineRepo
						.findByStartTimeAndEndTimeAndEmployee_Username(startTime, endTime, employee.getUsername())
						.isEmpty(); // only NEW ones
			}).map(timeline -> {
				long duration = (long) (timeline.getEndMin() - timeline.getStartMin()) * 60 * 1000;
				TimeLineSummary s = new TimeLineSummary();
				s.setDate(today);
				s.setType(timeline.getType());
				s.setStartTime(today.atTime(timeline.getStartMin() / 60, timeline.getStartMin() % 60));
				s.setEndTime(today.atTime(timeline.getEndMin() / 60, timeline.getEndMin() % 60));
				s.setDurationMillis(duration);
				s.setEmployee(employee);
				s.setLabel(timeline.getLabel());
				return s;
			}).toList();
			try {
				timeLineRepo.saveAll(summaries);
			} catch (Exception e) {
				throw new TimeLineException("Failed to Add TimeLine Summary");
			}

		});
	}

	@Override
	public TimelineRowDTO saveTimeLineRow(TimelineRowDTO timeLineDto, Authentication authentication) {
		LocalDate today = LocalDate.now();
		LocalDateTime startTime = toDateTime(today, timeLineDto.getStart());
		LocalDateTime endTime = toDateTime(today, timeLineDto.getEnd());
		String username = authentication.getName();
		Employee employee = employeeRepo.findByUsername(username)
				.orElseThrow(() -> new EmployeeNotFoundException("Employee not found with username : " + username));
		TimeLineSummary timeLineSummary = new TimeLineSummary();
//		timeLineSummary.setDate(today);
//		timeLineSummary.setType(timeLineDto.getType());
//		timeLineSummary.setStartTime(startTime);
//		timeLineSummary.setEndTime(endTime);
//		timeLineSummary.setDurationMillis(durationToMillis(timeLineDto.getDuration()));
//		timeLineSummary.setEmployee(employee);
//		timeLineSummary.setLabel(timeLineDto.getLabel());
//		timeLineSummary.setRowId(timeLineDto.getRowId());
//		timeLineSummary.setComment(timeLineDto.getComment());
//		timeLineSummary.setColour(timeLineDto.getColour());
//		if (timeLineDto.getType().equalsIgnoreCase("Project")) {
//			Project project = projectRepository
//					.findProjectWithMilestoneAndDiscipline(timeLineDto.getProjectName(), timeLineDto.getMilestoneName(),
//							timeLineDto.getDisciplineName())
//					.orElseThrow(() -> new ResourceNotFoundException(
//							"Project Not Found with Project Name : " + timeLineDto.getProjectName()));
//			timeLineSummary.setProject(project);
//		}
//		TimeLineSummary summary;
//		try {
//			summary = timeLineRepo.save(timeLineSummary);
//		} catch (Exception e) {
//			throw new TimeLineException("Failed to Add TimeLine Summary");
//		}
		TimeLineSummary summary;
		if (timeLineDto.getType() != "Check-in" && timeLineDto.getType() != "Idle"
				&& timeLineDto.getType() != "Checked Out" && timeLineDto.isLastRow()) {
			Optional<TimeLineSummary> timeLine = timeLineRepo
					.findTopByEmployeeEmployeeIdAndDateOrderByEndTimeDesc(employee.getEmployeeId(), today);
			if (!timeLine.isEmpty()) {
				timeLine.get().setEndTime(endTime);
				timeLine.get().setDurationMillis(durationToMillis(timeLineDto.getDuration()));
				timeLine.get().setComment(timeLineDto.getComment());
				summary = timeLineRepo.save(timeLine.get());
			} else {
				timeLineSummary.setRowId(timeLineDto.getRowId());
				timeLineSummary.setDate(today);
				timeLineSummary.setType(timeLineDto.getType());
				timeLineSummary.setStartTime(startTime);
				timeLineSummary.setEndTime(endTime);
				timeLineSummary.setDurationMillis(durationToMillis(timeLineDto.getDuration()));
				timeLineSummary.setEmployee(employee);
				timeLineSummary.setLabel(timeLineDto.getLabel());
				timeLineSummary.setComment(timeLineDto.getComment());
				timeLineSummary.setColour(timeLineDto.getColour());
				if (timeLineDto.getType().equalsIgnoreCase("Project")) {
					Project project = projectRepository
							.findProjectWithMilestoneAndDiscipline(timeLineDto.getProjectName(),
									timeLineDto.getMilestoneName(), timeLineDto.getDisciplineName())
							.orElseThrow(() -> new ResourceNotFoundException(
									"Project Not Found with Project Name : " + timeLineDto.getProjectName()));
					timeLineSummary.setProject(project);
				}
				summary = timeLineRepo.save(timeLineSummary);
			}
		} else {
			timeLineSummary.setRowId(timeLineDto.getRowId());
			timeLineSummary.setDate(today);
			timeLineSummary.setType(timeLineDto.getType());
			timeLineSummary.setStartTime(startTime);
			timeLineSummary.setEndTime(endTime);
			timeLineSummary.setDurationMillis(durationToMillis(timeLineDto.getDuration()));
			timeLineSummary.setEmployee(employee);
			timeLineSummary.setLabel(timeLineDto.getLabel());
			timeLineSummary.setComment(timeLineDto.getComment());
			timeLineSummary.setColour(timeLineDto.getColour());
			if (timeLineDto.getType().equalsIgnoreCase("Project")) {
				Project project = projectRepository
						.findProjectWithMilestoneAndDiscipline(timeLineDto.getProjectName(),
								timeLineDto.getMilestoneName(), timeLineDto.getDisciplineName())
						.orElseThrow(() -> new ResourceNotFoundException(
								"Project Not Found with Project Name : " + timeLineDto.getProjectName()));
				timeLineSummary.setProject(project);
			}

			summary = timeLineRepo.save(timeLineSummary);
		}
		return TimelineRowDTO.builder().rowId(summary.getRowId()).comment(summary.getComment())
				.label(summary.getLabel()).type(summary.getType()).start(formatTime(summary.getStartTime()))
				.end(formatTime(summary.getEndTime())).duration(formatDuration(summary.getDurationMillis())).build();
	}

	private static LocalDateTime toDateTime(LocalDate date, String timeStr) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
		LocalTime time = LocalTime.parse(timeStr, formatter);
		return LocalDateTime.of(date, time);
	}

	private static long durationToMillis(String durationStr) {
		long millis = 0;

		Matcher m = Pattern.compile("(\\d+)h").matcher(durationStr);
		if (m.find())
			millis += Long.parseLong(m.group(1)) * 60 * 60 * 1000;

		m = Pattern.compile("(\\d+)m").matcher(durationStr);
		if (m.find())
			millis += Long.parseLong(m.group(1)) * 60 * 1000;

		m = Pattern.compile("(\\d+)s").matcher(durationStr);
		if (m.find())
			millis += Long.parseLong(m.group(1)) * 1000;

		return millis;
	}

	private static String formatTime(LocalDateTime dt) {
		if (dt == null)
			return null;
		return dt.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"));
	}

	private static String formatDuration(Long millis) {
		if (millis == null)
			return "0m";

		long totalSeconds = millis / 1000;
		long hours = totalSeconds / 3600;
		long minutes = (totalSeconds % 3600) / 60;

		if (hours > 0 && minutes > 0)
			return hours + "h " + minutes + "m";
		if (hours > 0)
			return hours + "h";
		return minutes + "m";
	}

	@Override
	public List<TimelineRowDTO> getTimeLineRows(String name) {
		LocalDate today = LocalDate.now();
		List<TimeLineSummary> timeLineList = timeLineRepo.findByDateAndEmployee_Username(today, name)
				.orElse(new ArrayList<TimeLineSummary>());
		if (timeLineList.isEmpty()) {
			return new ArrayList<TimelineRowDTO>();
		}
		return timeLineList.stream().map(
				timeline -> TimelineRowDTO.builder().rowId(timeline.getRowId()).comment(timeline.getComment()).build())
				.toList();
	}

	@Override
	public List<TimelineRowDTO> getAllTimeLineRows(String employeeId, LocalDate date) {
		List<TimeLineSummary> timeLineList = timeLineRepo
				.findByDateAndEmployeeEmployeeIdOrderByEndTimeAscStartTimeAsc(date, employeeId);
		if (timeLineList.isEmpty()) {
			return new ArrayList<TimelineRowDTO>();
		}
		List<TimelineRowDTO> timelineRowDTOList = timeLineList.stream()
				.map(timeline -> TimelineRowDTO.builder().date(timeline.getDate()).rowId(timeline.getRowId())
						.comment(timeline.getComment()).label(timeline.getLabel()).type(timeline.getType())
						.start(formatTime(timeline.getStartTime())).end(formatTime(timeline.getEndTime()))
						.duration(millisToDuration(timeline.getDurationMillis())).colour(timeline.getColour()).build())
				.toList();
		return timelineRowDTOList;
	}

	@Override
	public List<TimelineRowDTO> saveAllTimeLineRows(List<TimelineRowDTO> timelineDtoList,
			Authentication authentication) {
		String username = authentication.getName();
		Employee employee = employeeRepo.findByUsername(username)
				.orElseThrow(() -> new EmployeeNotFoundException("Employee not found with username : " + username));
		String empId = employee.getEmployeeId();
		LocalDate today = LocalDate.now();
		List<TimeLineSummary> timeLineSummaryList = new ArrayList<TimeLineSummary>();
		Optional<TimeLineSummary> lastTimeLine= timeLineRepo.findTopByEmployeeEmployeeIdAndDateOrderByEndTimeDesc(empId, today);
		if (lastTimeLine.isPresent()) {
			 timeLineRepo.delete(lastTimeLine.get());
				timelineDtoList.stream().forEach(timeLineDto -> {
					LocalDateTime startTime = toDateTime(today, timeLineDto.getStart());
					LocalDateTime endTime = toDateTime(today, timeLineDto.getEnd());
					TimeLineSummary timeLineSummary = new TimeLineSummary();
					timeLineSummary.setRowId(timeLineDto.getRowId());
					timeLineSummary.setDate(today);
					timeLineSummary.setType(timeLineDto.getType());
					timeLineSummary.setStartTime(startTime);
					timeLineSummary.setEndTime(endTime);
					timeLineSummary.setDurationMillis(durationToMillis(timeLineDto.getDuration()));
					timeLineSummary.setEmployee(employee);
					timeLineSummary.setLabel(timeLineDto.getLabel());
					timeLineSummary.setComment(timeLineDto.getComment());
					timeLineSummary.setColour(timeLineDto.getColour());
					if (timeLineDto.getType().equalsIgnoreCase("Project")) {
						Project project = projectRepository
								.findProjectWithMilestoneAndDiscipline(timeLineDto.getProjectName(),
										timeLineDto.getMilestoneName(), timeLineDto.getDisciplineName())
								.orElseThrow(() -> new ResourceNotFoundException(
										"Project Not Found with Project Name : " + timeLineDto.getProjectName()));
						timeLineSummary.setProject(project);
					}

					timeLineSummaryList.add(timeLineSummary);
				});

		}else {
			timelineDtoList.stream().forEach(timeLineDto -> {
				LocalDateTime startTime = toDateTime(today, timeLineDto.getStart());
				LocalDateTime endTime = toDateTime(today, timeLineDto.getEnd());
				TimeLineSummary timeLineSummary = new TimeLineSummary();
				timeLineSummary.setRowId(timeLineDto.getRowId());
				timeLineSummary.setDate(today);
				timeLineSummary.setType(timeLineDto.getType());
				timeLineSummary.setStartTime(startTime);
				timeLineSummary.setEndTime(endTime);
				timeLineSummary.setDurationMillis(durationToMillis(timeLineDto.getDuration()));
				timeLineSummary.setEmployee(employee);
				timeLineSummary.setLabel(timeLineDto.getLabel());
				timeLineSummary.setComment(timeLineDto.getComment());
				timeLineSummary.setColour(timeLineDto.getColour());
				if (timeLineDto.getType().equalsIgnoreCase("Project")) {
					Project project = projectRepository
							.findProjectWithMilestoneAndDiscipline(timeLineDto.getProjectName(),
									timeLineDto.getMilestoneName(), timeLineDto.getDisciplineName())
							.orElseThrow(() -> new ResourceNotFoundException(
									"Project Not Found with Project Name : " + timeLineDto.getProjectName()));
					timeLineSummary.setProject(project);
				}

				timeLineSummaryList.add(timeLineSummary);
			});
		}
		List<TimeLineSummary> resultList = timeLineRepo.saveAll(timeLineSummaryList);
		return resultList.stream()
				.map(summary -> TimelineRowDTO.builder().rowId(summary.getRowId()).comment(summary.getComment())
						.label(summary.getLabel()).type(summary.getType()).start(formatTime(summary.getStartTime()))
						.end(formatTime(summary.getEndTime())).duration(formatDuration(summary.getDurationMillis()))
						.build())
				.toList();
	}

	private static String millisToDuration(long millis) {

		long hours = millis / (60 * 60 * 1000);
		millis %= (60 * 60 * 1000);

		long minutes = millis / (60 * 1000);
		millis %= (60 * 1000);

		long seconds = millis / 1000;

		StringBuilder sb = new StringBuilder();

		if (hours > 0)
			sb.append(hours).append("h ");
		if (minutes > 0)
			sb.append(minutes).append("m ");
		if (seconds > 0)
			sb.append(seconds).append("s");

		return sb.toString().trim();
	}

}
