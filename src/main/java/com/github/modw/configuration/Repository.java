package com.github.modw.configuration;

import java.util.Optional;
import org.github.gestalt.config.annotations.Config;
import org.github.gestalt.config.annotations.ConfigPrefix;

@ConfigPrefix(prefix="repository")
public record Repository(
    @Config(path = "id", defaultVal = "central") String id,
    @Config(path = "type", defaultVal = "default") String type,
    @Config(path = "url", defaultVal = "https://repo1.maven.org/maven2/") String url,
    @Config(path = "username") Optional<String> username,
    @Config(path = "password") Optional<String> password) {}
