package com.pmtool.backend.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.itextpdf.text.pdf.PdfStructTreeController.returnType;
import com.pmtool.backend.DTO.response.ApiResponse;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	private ResponseEntity<Object> buildResponse(HttpStatus status, String error, String message) {
		Map<String, Object> body = new HashMap<>();
		body.put("status", status.value());
		body.put("error", error);
		body.put("message", message);
		body.put("timestamp", LocalDateTime.now());
		return ResponseEntity.status(status).body(body);
	}

	@ExceptionHandler(LeaveApplyException.class)
	public ResponseEntity<Object> handleLeaveApplyException(LeaveApplyException ex) {
		return buildResponse(HttpStatus.BAD_REQUEST, "Leave Apply Failed", ex.getMessage());
	}

	@ExceptionHandler(BiometricFetchFailureException.class)
	public ResponseEntity<Object> handleBiometricFetchFailureException(BiometricFetchFailureException ex) {
		return buildResponse(HttpStatus.BAD_REQUEST, "Biometric Data Fetch Operation Failed!!!", ex.getMessage());
	}

	@ExceptionHandler(EmployeeNotFoundException.class)
	public ResponseEntity<Object> handleEmployeeNotFoundException(EmployeeNotFoundException ex) {
		return buildResponse(HttpStatus.BAD_REQUEST, "Employee Not Found", ex.getMessage());
	}

	@ExceptionHandler(ManagerNotFoundException.class)
	public ResponseEntity<Object> handleManagerNotFoundException(ManagerNotFoundException ex) {
		return buildResponse(HttpStatus.BAD_REQUEST, "Manager Not Found", ex.getMessage());
	}

	@ExceptionHandler(TeamLeadNotFoundException.class)
	public ResponseEntity<Object> handleTeamLeadNotFoundException(TeamLeadNotFoundException ex) {
		return buildResponse(HttpStatus.BAD_REQUEST, "Team Lead Not Found", ex.getMessage());
	}

	@ExceptionHandler(LeaveNotFoundException.class)
	public ResponseEntity<Object> handleLeaveNotFoundException(LeaveNotFoundException ex) {
		return buildResponse(HttpStatus.BAD_REQUEST, "Leave Not Found", ex.getMessage());
	}

	@ExceptionHandler(LeaveNotAllowedException.class)
	public ResponseEntity<Object> handleLeaveNotAllowedException(LeaveNotAllowedException ex) {
		return buildResponse(HttpStatus.BAD_REQUEST, "Leave Not Allowed", ex.getMessage());
	}

	@ExceptionHandler(SystemAttendanceNotFoundException.class)
	public ResponseEntity<Object> handleNotificationNotFoundExceptions(SystemAttendanceNotFoundException ex) {
		return buildResponse(HttpStatus.BAD_REQUEST, "System Attendance Not Found", ex.getMessage());
	}

	@ExceptionHandler(NotificationNotFoundException.class)
	public ResponseEntity<Object> handleNotificationInsertionExceptions(NotificationNotFoundException ex) {
		return buildResponse(HttpStatus.BAD_REQUEST, "Notification Insertion Failed", ex.getMessage());
	}

	@ExceptionHandler(DisciplineInsertionFailedException.class)
	public ResponseEntity<Object> handleDisciplineInsertionFailedException(DisciplineInsertionFailedException ex) {
		return buildResponse(HttpStatus.BAD_REQUEST, "Discipline Insertion Failed", ex.getMessage());
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ApiResponse> handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest req) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(ApiResponse.builder().success(false).message(ex.getMessage()).errorCode("RESOURCE_NOT_FOUND")
						.timestamp(LocalDateTime.now()).path(req.getRequestURI()).build());
	}

	@ExceptionHandler(NotificationSendingFailedException.class)
	public ResponseEntity<ApiResponse> handleNotificationSendingFailed(NotificationSendingFailedException ex,
			HttpServletRequest req) {
		return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
				.body(ApiResponse.builder().success(false).message(ex.getMessage())
						.errorCode("Notification Sending Failed").timestamp(LocalDateTime.now())
						.path(req.getRequestURI()).build());
	}

	@ExceptionHandler(TimeLineException.class)
	public ResponseEntity<ApiResponse> handleTimeLineException(TimeLineException ex, HttpServletRequest req) {
		return ResponseEntity.status(HttpStatus.FAILED_DEPENDENCY)
				.body(ApiResponse.builder().success(false).message(ex.getMessage()).errorCode("Time Line Error")
						.timestamp(LocalDateTime.now()).path(req.getRequestURI()).build());
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> handleOtherExceptions(Exception ex) {
		return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", ex.getMessage());
	}
}
