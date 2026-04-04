package com.github.modw.command;

import static com.github.modw.command.VersionFilter.latest;

import com.github.modw.*;
import com.github.modw.configuration.ConfigurationProperties;
import com.github.modw.console.Console;
import com.github.modw.maven.MavenRepository;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.repository.RemoteRepository;
import picocli.CommandLine;

@CommandLine.Command(
    name = Installed.COMMAND_NAME,
    description = "Display the installed Moderne CLI versions")
@com.github.modw.CommandLine(offlineLocalRepository = true)
public class Installed implements Callable<Integer> {
  static final String COMMAND_NAME = "installed";

  @CommandLine.Spec CommandLine.Model.CommandSpec spec;

  @CommandLine.Option(
      names = {"-f", "--filter"},
      description = "Version filter [LATEST_MAJORS, LATEST_MINORS, LATEST_PATCHES, NONE]",
      defaultValue = "NONE")
  String filter;

  final ConfigurationProperties properties;
  final RemoteRepository remoteRepository;
  final MavenRepository localRepository;

  public Installed(
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
        localRepository.installedVersions(cliArtifact, Collections.singletonList(remoteRepository));

    final List<String> filteredVersions =
        latest(versions.stream().sorted().toList(), VersionFilter.valueOf(filter));

    Console.Display.list("Installed versions", filteredVersions);

    return ExitCode.OK.value();
  }
}
