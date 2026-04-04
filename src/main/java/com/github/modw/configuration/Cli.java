package com.github.modw.configuration;

import org.github.gestalt.config.annotations.Config;
import org.github.gestalt.config.annotations.ConfigPrefix;

@ConfigPrefix(prefix ="cli")
public record Cli(
    @Config(path = "groupId", defaultVal = "io.moderne") String groupId,
    @Config(path = "artifactId", defaultVal = "moderne-cli") String artifactId,
    @Config(path = "version", defaultVal = "RELEASE") String version) {}
