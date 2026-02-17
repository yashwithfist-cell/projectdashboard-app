package com.pmtool.backend.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pmtool.backend.DTO.NotificationDTO;
import com.pmtool.backend.services.NotificationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
@CrossOrigin
public class NotificationController {

	private final NotificationService notificationService;

	@GetMapping()
	@PreAuthorize("hasAnyRole('EMPLOYEE','SYSTEM_ADMIN','PROJECT_MANAGER','TEAM_LEAD','HUMAN_RESOURCE')")
	public ResponseEntity<List<NotificationDTO>> getAll(Authentication authentication) {
		return ResponseEntity.ok(notificationService.getNotifications(authentication));
	}

	@PutMapping("/read/{id}")
	@PreAuthorize("hasAnyRole('EMPLOYEE','SYSTEM_ADMIN','PROJECT_MANAGER','TEAM_LEAD','HUMAN_RESOURCE')")
	public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
		notificationService.markAsRead(id);
		return ResponseEntity.ok().build();
	}

	@PutMapping("/read-all/{username}")
	@PreAuthorize("hasAnyRole('EMPLOYEE','SYSTEM_ADMIN','PROJECT_MANAGER','TEAM_LEAD','HUMAN_RESOURCE')")
	public ResponseEntity<Void> markAllAsRead(@PathVariable String username) {
		notificationService.markAllAsRead(username);
		return ResponseEntity.ok().build();
	}

	@PutMapping("/probStatus/{id}/{prStatus}")
	@PreAuthorize("hasAnyRole('EMPLOYEE','SYSTEM_ADMIN','PROJECT_MANAGER','TEAM_LEAD','HUMAN_RESOURCE')")
	public ResponseEntity<Void> addProbStatusAndComment(@PathVariable Long id, @PathVariable String prStatus,
			@RequestBody Map<String, String> body) {
		String comment = body.get("comment");
		notificationService.addProbStatusAndComment(id, prStatus, comment);
		return ResponseEntity.ok().build();
	}

	@PutMapping("/logStatus/{trainingDesc}")
	@PreAuthorize("hasAnyRole('EMPLOYEE','SYSTEM_ADMIN','PROJECT_MANAGER','TEAM_LEAD','HUMAN_RESOURCE')")
	public ResponseEntity<Long> addLogStatusAndComment(@PathVariable String trainingDesc,
			Authentication authentication) {
		Long notificationId = notificationService.addSystemLogNotification(trainingDesc, authentication);
		return ResponseEntity.ok(notificationId);
	}

	@PutMapping("/updateLogStatus/{id}")
	@PreAuthorize("hasAnyRole('EMPLOYEE','SYSTEM_ADMIN','PROJECT_MANAGER','TEAM_LEAD','HUMAN_RESOURCE')")
	public ResponseEntity<Void> updateLogStatus(@PathVariable Long id) {
		notificationService.updateSystemLogNotification(id);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/getTrainingStatus/{notifId}")
	@PreAuthorize("hasAnyRole('EMPLOYEE','SYSTEM_ADMIN','PROJECT_MANAGER','TEAM_LEAD','HUMAN_RESOURCE')")
	public ResponseEntity<Boolean> getApprovedLogNotification(@PathVariable Long notifId) {
		Boolean isApproved=notificationService.getApprovedLogNotification(notifId);
		return ResponseEntity.ok(isApproved);
	}
}
