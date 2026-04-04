package com.github.modw;

import com.github.modw.configuration.ConfigurationProperties;
import com.github.modw.maven.MavenRepository;
import org.eclipse.aether.repository.RemoteRepository;

public record CommandHarness(
    ConfigurationProperties properties, RemoteRepository remoteRepository, MavenRepository localRepository) {}
