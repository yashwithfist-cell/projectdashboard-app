package com.pmtool.backend.scheduler;

import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import com.pmtool.backend.constants.BiometricDeviceConstants;
import com.pmtool.backend.exception.BiometricFetchFailureException;
import com.pmtool.backend.services.SoapClientService;

@Configuration
public class DailyAttendanceScheduler {

	@Autowired
	private SoapClientService soapClientService;

	@Value("${biometric.username}")
	private String username;

	@Value("${biometric.password}")
	private String password;

	@Value("${biometric.device.serial}")
	private String serial;

	@Scheduled(cron = "0 59 23 * * *") // Every day at 23:59
	public void markDailyAttendanceSummary() {
		LocalDate today = LocalDate.now();
		String fromDate = today.toString() + BiometricDeviceConstants.START_TIME;
		String toDate = today.toString() + BiometricDeviceConstants.END_TIME;
		try {
			soapClientService.fetchData(fromDate, toDate, serial, username, password);
		} catch (Exception e) {
			throw new BiometricFetchFailureException("Biometric Records Fetch Failed");
		}
	}
	
	@Scheduled(cron = "0 59 23 * * *") // Every day at 23:59
	public void checkInOutStatus() {
		LocalDate today = LocalDate.now();
		String fromDate = today.toString() + BiometricDeviceConstants.START_TIME;
		String toDate = today.toString() + BiometricDeviceConstants.END_TIME;
		try {
			soapClientService.fetchData(fromDate, toDate, serial, username, password);
		} catch (Exception e) {
			throw new BiometricFetchFailureException("Biometric Records Fetch Failed");
		}
	}

}
