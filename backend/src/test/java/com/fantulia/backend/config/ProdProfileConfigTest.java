package com.fantulia.backend.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.boot.web.server.autoconfigure.ServerProperties;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

// @SpringJUnitConfig: runs the test inside a Spring context. The
// ConfigDataApplicationContextInitializer makes that context load
// application.yml and the active profile's file the same way the real app
// does, without starting a web server or a database, so this test is fast.
// @ActiveProfiles("prod"): activates application-prod.yml. A misspelled or
// deleted profile file would otherwise fail silently and leave the API
// listening on every network interface.
@SpringJUnitConfig(
        classes = ProdProfileConfigTest.Config.class,
        initializers = ConfigDataApplicationContextInitializer.class)
@ActiveProfiles("prod")
class ProdProfileConfigTest {

    // @EnableConfigurationProperties: binds the `server.*` properties into a
    // ServerProperties bean so the test can read what the profile resolved to.
    @EnableConfigurationProperties(ServerProperties.class)
    static class Config {
    }

    @Autowired
    private ServerProperties serverProperties;

    @Test
    void prodProfileBindsTheApiToLoopbackOnly() {
        assertThat(serverProperties.getAddress())
                .as("server.address must be set in application-prod.yml, otherwise the API listens on every network interface")
                .isNotNull();
        assertThat(serverProperties.getAddress().getHostAddress()).isEqualTo("127.0.0.1");
    }
}
