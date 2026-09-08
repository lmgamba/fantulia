package com.fantulia.backend.health;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;

import com.fantulia.backend.TestcontainersConfiguration;

// @SpringBootTest(webEnvironment = RANDOM_PORT): boots the full application
// on a real HTTP port, so this exercises the actual servlet stack rather
// than calling the controller method directly.
// @AutoConfigureTestRestTemplate: since Spring Boot 4, the TestRestTemplate
// bean is opt-in (it moved into its own module) rather than automatic just
// from webEnvironment = RANDOM_PORT.
// @Import(TestcontainersConfiguration.class): registers the @ServiceConnection
// Postgres container from that class (see its own comment) as the datasource
// for this test — Spring's context caching reuses one container across every
// test class that imports this same configuration, instead of one per class.
@Import(TestcontainersConfiguration.class)
@AutoConfigureTestRestTemplate
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class HealthControllerIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void healthReturnsOkStatusAndCommitVersion() {
        ResponseEntity<Map> response = restTemplate.getForEntity("/api/health", Map.class);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).containsEntry("status", "ok");
        assertThat(response.getBody()).containsKey("version");
    }
}
