package com.github.modw.configuration;

import org.github.gestalt.config.annotations.ConfigPrefix;

import java.util.Optional;

@ConfigPrefix(prefix = "")
public record ConfigurationProperties(
    Cli cli, Optional<Proxy> proxy, Repository repository, Wrapper wrapper) {}
