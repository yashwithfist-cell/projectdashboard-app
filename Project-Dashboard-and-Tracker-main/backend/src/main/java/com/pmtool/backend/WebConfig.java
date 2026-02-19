package com.pmtool.backend; // replace with your actual package if different

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**") // Allow all endpoints
//                        .allowedOrigins("http://localhost:3000", "http://192.168.1.4:3000")
						.allowedOrigins("http://192.168.1.37:3000","http://192.168.1.254:3000","http://localhost:3000","http://localhost:3001","http://192.168.1.34:3001")// Your React frontend
						.allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS").allowedHeaders("*")
						.allowCredentials(true);
			}
		};
	}
}
