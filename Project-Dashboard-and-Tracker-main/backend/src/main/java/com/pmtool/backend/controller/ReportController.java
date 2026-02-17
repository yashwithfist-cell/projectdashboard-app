package com.pmtool.backend.controller;

import com.pmtool.backend.DTO.DetailedReportDTO;
import com.pmtool.backend.DTO.ReportDTO;
import com.pmtool.backend.services.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;
import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("/api/reports")
@CrossOrigin
public class ReportController{
    @Autowired
    private ReportService reportService;

    @GetMapping("/master")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<List<ReportDTO>> getMasterReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(reportService.generateReport(startDate, endDate));
    }
    @GetMapping("/milestone/{milestoneId}")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<List<ReportDTO>> getMilestoneReport(@PathVariable Long milestoneId){
      return ResponseEntity.ok(reportService.generateMilestoneReport(milestoneId));
}
    @GetMapping("/detailed")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<List<DetailedReportDTO>> getDetailedReport(
            @RequestParam Long projectId,
            @RequestParam(required = false) Long milestoneId) { // milestoneId is now optional

        return ResponseEntity.ok(reportService.generateDetailedReport(projectId, milestoneId));
    }
}
