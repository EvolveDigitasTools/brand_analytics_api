package com.pluuginstore.brand_analytics;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(
		info = @Info(
				title = "Warehouse Management System API",
				version = "1.0",
				description = "API documentation for Warehouse Management System"
		)
)
@SpringBootApplication
public class BrandAnalyticsApplication {

	public static void main(String[] args) {
		SpringApplication.run(BrandAnalyticsApplication.class, args);
	}

}
