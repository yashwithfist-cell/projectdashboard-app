package com.pmtool.backend.controller;

import com.pmtool.backend.entity.Employee;
import com.pmtool.backend.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin
public class AuthController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @GetMapping("/user")
    public ResponseEntity<?> getCurrentUser(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No user is currently authenticated.");
        }
        String username = principal.getName();

        Optional<Employee> employeeOpt = employeeRepository.findByUsername(username);
        if (employeeOpt.isEmpty()) {
            throw new RuntimeException("Authenticated user not found in database: " + username);
        }
        Employee employee = employeeOpt.get();

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("username", employee.getUsername());
        userInfo.put("role", employee.getRole().name());
        userInfo.put("employeeName", employee.getName()); // <-- FIXED
        userInfo.put("employeeId", employee.getEmployeeId());
        userInfo.put("status", employee.getStatus().name());

        return ResponseEntity.ok(userInfo);
    }
}