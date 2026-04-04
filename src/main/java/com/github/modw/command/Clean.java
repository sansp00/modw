package com.github.modw.command;

import com.github.modw.*;
import com.github.modw.configuration.ConfigurationProperties;
import com.github.modw.console.Console;
import com.github.modw.maven.MavenRepository;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.Callable;

import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.repository.RemoteRepository;
import picocli.CommandLine;

@CommandLine.Command(
    name = Clean.COMMAND_NAME,
    description = "Delete the local Moderne CLI versions")
@com.github.modw.CommandLine(offlineLocalRepository = true)
public class Clean implements Callable<Integer> {
  static final String COMMAND_NAME = "clean";

  @CommandLine.Spec CommandLine.Model.CommandSpec spec;

  final ConfigurationProperties properties;
  final RemoteRepository remoteRepository;
  final MavenRepository localRepository;

  public Clean(
          final ConfigurationProperties properties,
          final RemoteRepository remoteRepository,
          final MavenRepository localRepository) {
    this.properties = properties;
    this.remoteRepository = remoteRepository;
    this.localRepository = localRepository;
  }
  @Override
  public Integer call() {
    Console.Display.command(COMMAND_NAME);

    final Artifact cliArtifact =
        MavenRepository.artifactFor(
            properties.cli().groupId(), properties.cli().artifactId(), properties.cli().version());

    final Set<String> versions =
       localRepository
            .deleteVersions(cliArtifact, Collections.singletonList(remoteRepository));

    Console.Display.list("Cleaned versions", versions.stream().sorted().toList());
    return ExitCode.OK.value();
  }
}
