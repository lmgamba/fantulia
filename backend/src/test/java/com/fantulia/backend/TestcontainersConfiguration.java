package com.fantulia.backend;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

// @TestConfiguration: a @Configuration class only picked up in tests
// (via @Import), never by the main application context.
// proxyBeanMethods = false: this config has no inter-bean method calls to
// intercept, so CGLIB proxying of this class would be pure overhead.
@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

	// @ServiceConnection: Spring Boot auto-detects this as a Postgres
	// connection and wires the datasource to point at the container's
	// mapped port/credentials — no manual @DynamicPropertySource needed.
	// Spring's test context caching means every test class that @Imports
	// this same configuration shares one running container instead of
	// starting a fresh one per class.
	@Bean
	@ServiceConnection
	PostgreSQLContainer postgresContainer() {
		return new PostgreSQLContainer(DockerImageName.parse("postgres:17-alpine"));
	}

}
