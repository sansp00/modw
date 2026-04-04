package com.github.modw.configuration;

import org.github.gestalt.config.annotations.Config;
import org.github.gestalt.config.annotations.ConfigPrefix;

@ConfigPrefix(prefix = "git")
public record BuildProperties(
    @Config(path = "build.time") String buildTime,
    @Config(path = "build.version") String buildVersion,
    @Config(path = "commit.id.abbrev") String commitIdAbbrev,
    @Config(path = "commit.id.full") String commitIdFull) {}
