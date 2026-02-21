package com.pmtool.backend.entity;

import com.pmtool.backend.enums.AccountStatus;
import com.pmtool.backend.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "employees")
@Data
//@Builder
@NoArgsConstructor
//@AllArgsConstructor
public class Employee {

	@Id
	@Column(name = "employee_id")
	private String employeeId;

	@Column(name = "employee_name", nullable = false)
	private String name;

	@Column(name = "username", nullable = false, unique = true)
	private String username;

	@Column(name = "password", length = 60, nullable = false)
	private String password;

	@Column(name = "join_date")
	private LocalDate joinDate;

	@Column(name = "prof_period_end_date")
	private LocalDate profPeriodEndDate;

	@Enumerated(EnumType.STRING)
	@Column(name = "role", nullable = false)
	private Role role;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "department_id")
	private Department department;

	@OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<WorkLogEntry> workLogEntries;

	// One employee can have multiple attendance logs
	@OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<AttendanceLog> attendanceLogs;

	@OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<Notification> notifications;

	@Column(name = "employee_location", nullable = false)
	private String location;
	@Column(name = "bank_name", nullable = false)
	private String bankName;
	@Column(name = "bank_account_no", nullable = false, unique = true)
	private String bankAccountNo;
	@Column(name = "salary", nullable = false)
	private Double salary;
	@Column(name = "mail_id", nullable = false, unique = true)
	private String mailId;
	@Column(name = "mgr_name", nullable = false)
	private String mgrName;
	@Column(name = "team_lead_name", nullable = false)
	private String teamLeadName;

	@OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<IdleLog> idelLogSet;

	@OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<TimeLineSummary> timeLineSet;

	@OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<TimerWorkLog> timerSet;

	@Column(name = "employee_device_code")
	private String empDeviceCode;

	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	private AccountStatus status = AccountStatus.ACTIVE;

	@PrePersist
	public void setDefaultStatus() {
		if (this.status == null) {
			this.status = AccountStatus.ACTIVE;
		}
	}

}