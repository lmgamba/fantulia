package com.fantulia.backend.health;

import java.util.Map;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.info.GitProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

// @RestController: combines @Controller + @ResponseBody, so every method's
// return value is serialized straight to the response body (JSON here)
// instead of being resolved as a view name.
@RestController
public class HealthController {

    private final GitProperties gitProperties;

    // GitProperties only exists as a bean when target/classes/git.properties
    // was generated (see the git-commit-id-maven-plugin in pom.xml), so it's
    // injected as optional via ObjectProvider rather than a hard constructor
    // dependency — constructor injection stays the only injection style used.
    public HealthController(ObjectProvider<GitProperties> gitProperties) {
        this.gitProperties = gitProperties.getIfAvailable();
    }

    // @GetMapping("/api/health"): maps HTTP GET on this exact path to this method.
    @GetMapping("/api/health")
    public Map<String, String> health() {
        String version = gitProperties != null ? gitProperties.getShortCommitId() : "unknown";
        return Map.of("status", "ok", "version", version);
    }
}
