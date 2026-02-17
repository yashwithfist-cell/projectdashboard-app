package com.pmtool.backend.services;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.pmtool.backend.DTO.AttendanceLogDto;
import com.pmtool.backend.constants.BiometricDeviceConstants;
import com.pmtool.backend.entity.AttendanceLog;
import com.pmtool.backend.entity.Employee;
import com.pmtool.backend.entity.Notification;
import com.pmtool.backend.entity.SystemAttendance;
import com.pmtool.backend.enums.AttendanceStatus;
import com.pmtool.backend.enums.Status;
import com.pmtool.backend.exception.EmployeeNotFoundException;
import com.pmtool.backend.exception.ResourceNotFoundException;
import com.pmtool.backend.exception.SystemAttendanceNotFoundException;
import com.pmtool.backend.repository.AttendanceRepository;
import com.pmtool.backend.repository.EmployeeRepository;
import com.pmtool.backend.repository.NotificationRepository;
import com.pmtool.backend.repository.SystemAttendanceRepository;
import com.pmtool.backend.repository.WorkLogEntryRepository;
import com.pmtool.backend.util.AttendanceUtils;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SoapClientService {

	@Autowired
	private AttendanceRepository repository;

	@Autowired
	private EmployeeRepository employeeRepository;

	@Autowired
	private SystemAttendanceRepository attendanceRepository;

	@Autowired
	private NotificationRepository notificationRepository;

	@Value("${etimetrack.soap.endpoint}")
	private String endpoint;

	@Value("${etimetrack.soap.action}")
	private String soapAction;

	private AttendanceStatus status;

	private final RestTemplate restTemplate = new RestTemplate();

	@Value("${biometric.username}")
	private String username;

	@Value("${biometric.password}")
	private String password;

	@Value("${biometric.device.serial}")
	private String serial;

	@Autowired
	private WorkLogEntryRepository workLogEntryRepo;

	public void fetchData(String fromDate, String toDate, String serial, String user, String pass) throws Exception {
		String xml = buildSoapRequest(fromDate, toDate, serial, user, pass);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.TEXT_XML);
		headers.add(BiometricDeviceConstants.SOAP_ACTION, soapAction);
		HttpEntity<String> entity = new HttpEntity<>(xml, headers);
		ResponseEntity<String> response = restTemplate.postForEntity(endpoint, entity, String.class);
		String responseXml = response.getBody();
		if (responseXml == null || !responseXml.contains("<strDataList>")) {
			log.info("⚠️ No log data found in response");
		}
		Pattern pattern = Pattern.compile("<strDataList>(.*?)</strDataList>", Pattern.DOTALL);
		Matcher matcher = pattern.matcher(responseXml);
		String dataList = "";
		if (matcher.find()) {
			dataList = matcher.group(1).trim();
		}
		List<AttendanceLogDto> logs = Arrays.stream(dataList.split("\n")).map(String::trim)
				.filter(line -> !line.isEmpty()).map(line -> line.split("\\t")).filter(parts -> parts.length == 3)
				.map(parts -> new AttendanceLogDto(parts[0],
						LocalDateTime.parse(parts[1],
								DateTimeFormatter.ofPattern(BiometricDeviceConstants.YYYY_MM_DD_HH_MM_SS)),
						parts[2].toLowerCase()))
				.collect(Collectors.toList());
		Map<String, Map<LocalDate, List<AttendanceLogDto>>> grouped = logs.stream().collect(Collectors.groupingBy(
				AttendanceLogDto::getEmpDeviceCode, Collectors.groupingBy(log -> log.getTimestamp().toLocalDate())));
		List<Map<String, String>> result = new ArrayList<>();
		List<AttendanceLog> resultList = new ArrayList<>();
		for (var empEntry : grouped.entrySet()) {
			String empDeviceCode = empEntry.getKey();
			Employee employee = employeeRepository.findByEmpDeviceCode(empDeviceCode);
			if (employee != null) {
				for (var dayEntry : empEntry.getValue().entrySet()) {
					LocalDate date = dayEntry.getKey();
					List<AttendanceLogDto> dayLogs = dayEntry.getValue().stream()
							.sorted(Comparator.comparing(AttendanceLogDto::getTimestamp)).collect(Collectors.toList());
					Duration total = Duration.ZERO;
					LocalDateTime inTime = null;
					LocalDateTime firstCheckin = AttendanceUtils.getFirstCheckIn(dayLogs);
					LocalDateTime lastCheckout = AttendanceUtils.getLastCheckOut(dayLogs);
					for (AttendanceLogDto log : dayLogs) {
						if (log.getDirection().equals(BiometricDeviceConstants.IN)) {
							inTime = log.getTimestamp();

						} else if (log.getDirection().equals(BiometricDeviceConstants.OUT) && inTime != null) {
							total = total.plus(Duration.between(inTime, log.getTimestamp()));
							inTime = null;
						}
					}
					result.add(Map.of(BiometricDeviceConstants.EMP_DEVICE_CODE, empDeviceCode,
							BiometricDeviceConstants.DATE, date.toString(), BiometricDeviceConstants.TOTAL_HOURS_WORKED,
							AttendanceUtils.formatDuration(total), BiometricDeviceConstants.FIRST_CHECK_IN,
							firstCheckin.toString(), BiometricDeviceConstants.LAST_CHECK_OUT, lastCheckout.toString(),
							BiometricDeviceConstants.TOTAL_HOURS,
							AttendanceUtils.formatDuration(Duration.between(firstCheckin, lastCheckout))));

					resultList.add(AttendanceLog.builder().employee(employee).date(date)
							.totalHoursWorked(AttendanceUtils.formatDuration(total)).inTime(firstCheckin)
							.outTime(lastCheckout)
							.totalHours(AttendanceUtils.formatDuration(Duration.between(firstCheckin, lastCheckout)))
							.status(attendanceStatus(firstCheckin, lastCheckout, total)).build());
				}
			} else {
				continue;
			}
		}
		repository.saveAll(resultList);
		sendNotification(resultList);
		resultList.clear();
		List<Employee> absEmpList = repository.findAllAbsentEmployees();
		absEmpList.stream().forEach(absEmp -> {
			resultList.add(AttendanceLog.builder().employee(absEmp).date(LocalDate.now()).totalHoursWorked("")
					.inTime(LocalDateTime.of(1970, 1, 1, 0, 0)).outTime(LocalDateTime.of(1970, 1, 1, 0, 0))
					.totalHours("").status(AttendanceStatus.ABSENT).build());
		});
		repository.saveAll(resultList);

	}

	private void sendNotification(List<AttendanceLog> attendanceList) {
		List<Notification> notificationList = new ArrayList<>();

		attendanceList.stream().forEach(log -> {
			try {
				SystemAttendance sysAtt = attendanceRepository.findByDateAndEmployee(log.getEmployee().getUsername(),
						log.getDate());
				if (sysAtt != null) {
					long seconds = convertToSeconds(log.getTotalHoursWorked());
					long timeDiff = seconds - sysAtt.getTotalWorkMs();
					if (timeDiff > 1800000) {
						Notification notification = new Notification();
						notification.setTitle("Maintain Work Hour");
						notification.setMessage(log.getEmployee().getUsername()
								+ " your todays work hour difference between biometric log and system log is "
								+ convertToHours(timeDiff) + " Please maintain proper work hour");
						notification.setCreatedAt(LocalDateTime.now());
						notification.setEmployee(log.getEmployee());
						notification.setStatus(Status.COMPLETED);
						notification.setUsername(log.getEmployee().getUsername());

//						notificationList.add(Notification.builder().title("Maintain Work Hour")
//								.message(log.getEmployee().getUsername()
//										+ " your todays work hour difference between biometric log and system log is "
//										+ convertToHours(timeDiff) + " Please maintain proper work hour")
//								.username(log.getEmployee().getUsername()).createdAt(LocalDateTime.now()).build());
					}
				}
			} catch (Exception e) {
				throw new SystemAttendanceNotFoundException(
						"System attendance not found for username : " + log.getEmployee().getUsername());
			}
		});

		notificationRepository.saveAll(notificationList);
	}

	private long convertToSeconds(String totalHoursWorked) {
		String parts[] = totalHoursWorked.split(":");
		long hours = Long.parseLong(parts[0]);
		long minutes = Long.parseLong(parts[1]);
		long seconds = Long.parseLong(parts[2]);
		return (hours * 3600 * 1000) + (minutes * 60 * 1000) + (seconds * 1000);
	}

	private String convertToHours(long totalMs) {
		long totalSeconds = totalMs / 1000;
		long hours = totalSeconds / 3600;
		long minutes = (totalSeconds % 3600) / 60;
		long seconds = totalSeconds % 60;
		return hours + "h " + minutes + "m " + seconds + "s ";
	}

	private AttendanceStatus attendanceStatus(LocalDateTime firstCheckin, LocalDateTime lastCheckout, Duration total) {
		if (firstCheckin == null && lastCheckout == null) {
			return status = AttendanceStatus.ABSENT;
		} else if (total.toHours() >= 8) {
			return status = AttendanceStatus.PRESENT;
		} else if (total.toHours() >= 4 && total.toHours() < 8) {
			return status = AttendanceStatus.HALF_DAY;
		} else {
			return status = AttendanceStatus.SHORT_HOURS;
		}
	}

	private String buildSoapRequest(String fromDate, String toDate, String serial, String user, String pass) {
		return "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
				+ "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
				+ "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" "
				+ "xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" + "<soap:Body>"
				+ "<GetTransactionsLog xmlns=\"http://tempuri.org/\">" + "<FromDateTime>" + fromDate + "</FromDateTime>"
				+ "<ToDateTime>" + toDate + "</ToDateTime>" + "<SerialNumber>" + serial + "</SerialNumber>"
				+ "<UserName>" + user + "</UserName>" + "<UserPassword>" + pass + "</UserPassword>"
				+ "<strDataList></strDataList>" + "</GetTransactionsLog>" + "</soap:Body>" + "</soap:Envelope>";
	}

	public AttendanceLogDto fetchEmployeeData(String userName) throws Exception {
		long totalMillis = 0;
		LocalDateTime lastIn = null;
		LocalDateTime lastOut = null;
		LocalDate today = LocalDate.now();
		String fromDate = today.toString() + BiometricDeviceConstants.START_TIME;
		String toDate = today.toString() + BiometricDeviceConstants.END_TIME;
		String xml = buildSoapRequest(fromDate, toDate, serial, username, password);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.TEXT_XML);
		headers.add(BiometricDeviceConstants.SOAP_ACTION, soapAction);
		HttpEntity<String> entity = new HttpEntity<>(xml, headers);
		ResponseEntity<String> response = restTemplate.postForEntity(endpoint, entity, String.class);
		String responseXml = response.getBody();
		if (responseXml == null || !responseXml.contains("<strDataList>")) {
			log.info("⚠️ No log data found in response");
		}
		Pattern pattern = Pattern.compile("<strDataList>(.*?)</strDataList>", Pattern.DOTALL);
		Matcher matcher = pattern.matcher(responseXml);
		String dataList = "";
		if (matcher.find()) {
			dataList = matcher.group(1).trim();
		}
		Employee employee = employeeRepository.findByUsername(userName)
				.orElseThrow(() -> new EmployeeNotFoundException("Employee not found with username : " + userName));
		List<AttendanceLogDto> logs = Arrays.stream(dataList.split("\n")).map(String::trim)
				.filter(line -> !line.isEmpty()).map(line -> line.split("\\t")).filter(parts -> parts.length == 3)
				.map(parts -> new AttendanceLogDto(parts[0],
						LocalDateTime.parse(parts[1],
								DateTimeFormatter.ofPattern(BiometricDeviceConstants.YYYY_MM_DD_HH_MM_SS)),
						parts[2].toLowerCase()))
//				.filter(log -> log.getEmpId().equals(employee.getEmployeeId())) // ✅ filter here
				.filter(log -> log.getEmpDeviceCode().equals(employee.getEmpDeviceCode())).collect(Collectors.toList());

		List<AttendanceLogDto> dayLogs = logs.stream().sorted(Comparator.comparing(AttendanceLogDto::getTimestamp))
				.collect(Collectors.toList());
//		LocalDateTime firstCheckin = AttendanceUtils.getFirstCheckIn(dayLogs);
//		if (firstCheckin != null) {
//			return firstCheckin;
//		}
//		return null;
		AttendanceLogDto dto = new AttendanceLogDto();
		for (AttendanceLogDto log : dayLogs) {

			if ("in".equalsIgnoreCase(log.getDirection())) {
				lastIn = log.getTimestamp();
				dto.getInList().add(log.getTimestamp());
			}

			if ("out".equalsIgnoreCase(log.getDirection()) && lastIn != null) {
				totalMillis += Duration.between(lastIn, log.getTimestamp()).toMillis();
				lastOut = log.getTimestamp();
				lastIn = null;
				dto.getOutList().add(log.getTimestamp());
			}
		}

		dto.setTotalWorkedMillis(totalMillis);
		dto.setLastCheckOut(lastOut);

		dto.setFirstCheckIn(logs.stream().filter(l -> "in".equalsIgnoreCase(l.getDirection()))
				.map(AttendanceLogDto::getTimestamp).findFirst().orElse(null));

		if (lastIn != null) {
			dto.setCurrentlyCheckedIn(true);
			dto.setLastCheckIn(lastIn);
		} else {
			dto.setCurrentlyCheckedIn(false);
		}

		return dto;

	}
}