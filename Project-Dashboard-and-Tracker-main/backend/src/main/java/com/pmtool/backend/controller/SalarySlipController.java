package com.pmtool.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.pmtool.backend.DTO.SalarySlipDto;
import com.pmtool.backend.services.SalarySlipService;
import com.pmtool.backend.util.PdfGenerator;

import java.io.ByteArrayInputStream;

@RestController
@RequestMapping("/api/salary-slips")
@CrossOrigin
public class SalarySlipController {

	@Autowired
	private SalarySlipService service;

	@GetMapping("/pdf")
	@PreAuthorize("hasRole('EMPLOYEE')")
	public ResponseEntity<byte[]> getSlipPdf(@RequestParam String date, Authentication authentication) {
		SalarySlipDto slipDto = service.calculateSalary(authentication.getName(), date);
		ByteArrayInputStream pdf = PdfGenerator.generateSlip(slipDto);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition", "inline; filename=salary-slip.pdf");

		return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(pdf.readAllBytes());
	}

}
