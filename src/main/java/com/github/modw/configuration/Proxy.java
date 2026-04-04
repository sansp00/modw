package com.github.modw.configuration;

import java.util.Optional;
import org.github.gestalt.config.annotations.Config;
import org.github.gestalt.config.annotations.ConfigPrefix;

@ConfigPrefix(prefix ="proxy")
public record Proxy(
    @Config(path = "type") String type,
    @Config(path = "host") String host,
    @Config(path = "port") Integer port,
    @Config(path = "username") Optional<String> username,
    @Config(path = "password") Optional<String> password) {}
