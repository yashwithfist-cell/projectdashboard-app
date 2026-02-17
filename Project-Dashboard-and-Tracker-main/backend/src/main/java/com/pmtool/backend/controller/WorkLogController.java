package com.pmtool.backend.controller;

import com.pmtool.backend.DTO.WorkLogEntryDTO;
import com.pmtool.backend.DTO.WorkLogResponseDTO; // <-- Make sure this is imported
import com.pmtool.backend.services.WorkLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/worklogs")
@CrossOrigin
public class WorkLogController {

    @Autowired
    private WorkLogService workLogService;

    // ... your GET method is correct ...
    @GetMapping("/my")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<List<WorkLogResponseDTO>> getMyCurrentMonthWorkLogs(Authentication authentication) {
        String username = authentication.getName();
        List<WorkLogResponseDTO> worklogs = workLogService.getEntriesForCurrentMonth(username);
        return ResponseEntity.ok(worklogs);
    }

    // --- THIS IS THE FIXED METHOD ---
    @PostMapping("/my")
    @PreAuthorize("hasRole('EMPLOYEE')")
    // FIX #1: Change the return type here to match the service
    public ResponseEntity<WorkLogResponseDTO> createMyWorkLog(@RequestBody WorkLogEntryDTO dto, Authentication authentication) {
        String username = authentication.getName();

        // This call now correctly returns a WorkLogResponseDTO
        WorkLogResponseDTO savedEntryDto = workLogService.saveNewEntry(dto, username);

        // FIX #2: The body of the response is now the DTO
        return ResponseEntity.status(HttpStatus.CREATED).body(savedEntryDto);
    }
    
    @GetMapping("/myDailyWorkLog")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<List<WorkLogResponseDTO>> getMyDailyWorkLogs(Authentication authentication) {
        String username = authentication.getName();
        List<WorkLogResponseDTO> worklogs = workLogService.getMyDailyWorkLogs(username);
        return ResponseEntity.ok(worklogs);
    }
}