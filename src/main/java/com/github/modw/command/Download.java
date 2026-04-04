package com.github.modw.command;

import com.github.modw.*;
import com.github.modw.Constants;
import com.github.modw.configuration.Cli;
import com.github.modw.configuration.ConfigurationProperties;
import com.github.modw.configuration.ConfigurationPropertiesFactory;
import com.github.modw.console.Console;
import com.github.modw.maven.MavenRepository;
import com.github.modw.maven.RemoteRepositoryFactory;

import java.nio.file.Path;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import picocli.CommandLine;

@CommandLine.Command(
    name = Download.COMMAND_NAME,
    description = "Download Moderne CLI to local repository")
@com.github.modw.CommandLine
public class Download implements Callable<Integer> {
  static final String COMMAND_NAME = "download";

  @CommandLine.Spec CommandLine.Model.CommandSpec spec;

  @CommandLine.Option(
      names = {"-v", "--version"},
      defaultValue = "RELEASE",
      description = "Specific version to download")
  String version;

  @CommandLine.Option(
      names = {"-p", "--persist"},
      defaultValue = "false",
      description = "Persist the downloaded version")
  boolean persist;

  final ConfigurationProperties properties;
  final RemoteRepository remoteRepository;
  final MavenRepository localRepository;

  public Download(
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

    try {
      final Artifact cliArtifact =
          MavenRepository.artifactFor(
              properties.cli().groupId(), properties.cli().artifactId(), version);

      final Artifact resolved =
          localRepository.resolve(cliArtifact, Collections.singletonList(remoteRepository));

      if (persist) {
        ConfigurationPropertiesFactory.store(
            new ConfigurationProperties(
                new Cli(resolved.getGroupId(), resolved.getArtifactId(), resolved.getVersion()),
                properties.proxy(),
                properties.repository(),
                properties.wrapper()));
      }

      final String result =
          "%s:%s:%s => %s (persisted=%s)"
              .formatted(
                  resolved.getGroupId(),
                  resolved.getArtifactId(),
                  resolved.getVersion(),
                  resolved.getFile(),
                  Boolean.toString(persist));
      Console.Display.result("Artifact resolved/downloaded", result);

      return ExitCode.OK.value();
    } catch (ArtifactResolutionException e) {
      Console.Display.error("Artifact unresolved");
      Console.Display.exception(e);
      return ExitCode.GENERAL_ERROR.value();
    }
  }
}
