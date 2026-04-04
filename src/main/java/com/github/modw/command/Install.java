package com.github.modw.command;

import com.github.modw.*;
import com.github.modw.configuration.ConfigurationProperties;
import com.github.modw.console.Console;
import com.github.modw.maven.MavenRepository;
import java.io.File;
import java.nio.file.Path;
import java.util.concurrent.Callable;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.installation.InstallationException;
import org.eclipse.aether.repository.RemoteRepository;
import picocli.CommandLine;

@CommandLine.Command(
    name = Install.COMMAND_NAME,
    description = "Install Moderne CLI to local repository")
@com.github.modw.CommandLine
public class Install implements Callable<Integer> {
  static final String COMMAND_NAME = "install";

  @CommandLine.Spec CommandLine.Model.CommandSpec spec;

  @CommandLine.Option(
      names = {"-f", "--file"},
      description = "Moderne CLI jar file",
      required = true)
  File file;

  @CommandLine.Option(
      names = {"-v", "--version"},
      description = "Moderne CLI file version",
      required = true)
  String version;

  final ConfigurationProperties properties;
  final RemoteRepository remoteRepository;
  final MavenRepository localRepository;

  public Install(
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
                properties.cli().groupId(), properties.cli().artifactId(), version)
            .setFile(file);

    try {
      localRepository.install(cliArtifact);
      Console.Display.result("Artifact installed", cliArtifact.toString());
      return ExitCode.OK.value();
    } catch (InstallationException e) {
      Console.Display.error("Artifact not installed to local repository");
      Console.Display.exception(e);
      return ExitCode.GENERAL_ERROR.value();
    }
  }
}
