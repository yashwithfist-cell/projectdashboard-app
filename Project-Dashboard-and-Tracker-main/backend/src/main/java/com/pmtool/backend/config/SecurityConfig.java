package com.pmtool.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.Customizer;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.cors(cors -> cors.configurationSource(corsConfigurationSource())).csrf(csrf -> csrf.disable())
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(authz -> authz
						// --- CRITICAL FIX: Allow OPTIONS preflight requests ---
						.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

						// --- General Authenticated Access for Viewing Data ---
						.requestMatchers(HttpMethod.GET, "/api/projects/**", "/api/milestones/**",
								"/api/disciplines/**")
						.authenticated()

						// --- Employee-Specific Endpoints ---
						.requestMatchers("/api/worklogs/my/**").hasRole("EMPLOYEE").requestMatchers("/api/leaves/**")
						.hasAnyRole("EMPLOYEE", "PROJECT_MANAGER", "TEAM_LEAD", "HUMAN_RESOURCE")
						.requestMatchers("/api/attendancelog/**")
						.hasAnyRole("EMPLOYEE", "SYSTEM_ADMIN", "PROJECT_MANAGER", "HUMAN_RESOURCE")
						.requestMatchers("/api/salary-slips/**").hasAnyRole("EMPLOYEE", "HUMAN_RESOURCE")
						.requestMatchers("/api/systemattendance/**")
						.hasAnyRole("EMPLOYEE", "SYSTEM_ADMIN", "PROJECT_MANAGER", "HUMAN_RESOURCE")
						.requestMatchers("/api/notifications/**")
						.hasAnyRole("EMPLOYEE", "SYSTEM_ADMIN", "PROJECT_MANAGER", "TEAM_LEAD", "HUMAN_RESOURCE")
						.requestMatchers("/api/idleLog/**").hasRole("EMPLOYEE")

						// --- Admin-Only Endpoints for Management ---
						.requestMatchers("/api/employees/**")
						.hasAnyRole("SYSTEM_ADMIN", "HUMAN_RESOURCE", "PROJECT_MANAGER", "TEAM_LEAD", "EMPLOYEE",
								"SUPER_ADMIN")
						.requestMatchers("/api/departments/**").hasAnyRole("SYSTEM_ADMIN", "HUMAN_RESOURCE")
						.requestMatchers("/api/reports/**").hasRole("SYSTEM_ADMIN").requestMatchers("/api/dashboard/**")
						.hasAnyRole("SYSTEM_ADMIN", "HUMAN_RESOURCE", "SUPER_ADMIN")

						.requestMatchers("/api/assignment/**").hasAnyRole("PROJECT_MANAGER", "TEAM_LEAD", "EMPLOYEE")
						.requestMatchers("/api/milestones/getUserMilestones")
						.hasAnyRole("SYSTEM_ADMIN", "PROJECT_MANAGER", "TEAM_LEAD")
						// Write operations for projects, milestones, and disciplines are admin-only.
						.requestMatchers(HttpMethod.POST, "/api/projects/**", "/api/milestones/**",
								"/api/disciplines/**")
						.hasAnyRole("SYSTEM_ADMIN", "SUPER_ADMIN", "HUMAN_RESOURCE")
						.requestMatchers(HttpMethod.PUT, "/api/projects/**", "/api/milestones/**",
								"/api/disciplines/**")
						.hasAnyRole("SYSTEM_ADMIN", "SUPER_ADMIN", "HUMAN_RESOURCE")
						.requestMatchers(HttpMethod.DELETE, "/api/projects/**", "/api/milestones/**",
								"/api/disciplines/**")
						.hasAnyRole("SYSTEM_ADMIN", "SUPER_ADMIN", "HUMAN_RESOURCE")
						.requestMatchers("/api/projects/getAllByUser").hasAnyRole("PROJECT_MANAGER", "TEAM_LEAD")
						.requestMatchers("/api/timeline/**").hasAnyRole("EMPLOYEE","SYSTEM_ADMIN","HUMAN_RESOURCE")
						// --- Default Rules ---
						.requestMatchers("/user").authenticated().anyRequest().authenticated())
				.httpBasic(Customizer.withDefaults());

		return http.build();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();

		// --- UPDATED: Allow requests from your React app's origin ---
//		configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", // Local development
//				"http://192.168.1.254:3000", // Your server IP (UPDATED)
//				"http://192.168.1.40:3000", // Keep your original if needed
//				"http://localhost", // Local without port
//				"http://192.168.1.254", // Server IP without port
//				"http://192.168.1.4:3000"
//		));

		configuration.setAllowedOrigins(Arrays.asList("http://192.168.1.37:3000", // LAN frontend
				"http://localhost:3000", "http://localhost:3001", // For local dev on server
				"http://192.168.1.254:3000","http://192.168.1.34:3001"));

		// --- EXPANDED: Allow all necessary HTTP methods ---
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH"));

		// --- EXPANDED: Allow all necessary headers ---
		configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With", "Accept",
				"Origin", "Access-Control-Request-Method", "Access-Control-Request-Headers"));

		// --- Allow credentials for HTTP Basic Auth ---
		configuration.setAllowCredentials(true);

		// --- Cache preflight response for 1 hour ---
		configuration.setMaxAge(3600L);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return NoOpPasswordEncoder.getInstance();
	}
}
