package com.github.modw.command;

import static com.github.modw.Constants.JAVA_COMMAND;
import static com.github.modw.Constants.JAVA_HOME;

import com.github.modw.ExitCode;
import com.github.modw.configuration.ConfigurationProperties;
import com.github.modw.console.Console;
import com.github.modw.maven.MavenRepository;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import picocli.CommandLine;

@Slf4j
@CommandLine.Command(name = Run.COMMAND_NAME, description = "Run Moderne CLI")
@com.github.modw.CommandLine(offlineLocalRepository = true)
public class Run implements Callable<Integer> {

  static final String COMMAND_NAME = "run";

  @CommandLine.Spec CommandLine.Model.CommandSpec spec;

  @CommandLine.Option(
      names = {"-v", "--version"},
          defaultValue = "RELEASE",
      description = "Moderne CLI file version")
  String version;

  @CommandLine.Parameters(description = "Arguments to pass to Moderne CLI")
  String[] args = new String[0];

  final ConfigurationProperties properties;
  final RemoteRepository remoteRepository;
  final MavenRepository localRepository;

  public Run(
      final ConfigurationProperties properties,
      final RemoteRepository remoteRepository,
      final MavenRepository localRepository) {
    this.properties = properties;
    this.remoteRepository = remoteRepository;
    this.localRepository = localRepository;
  }

  // java -jar /path/to/mod.jar "$@"

  @Override
  public Integer call() {
    Console.Display.command(COMMAND_NAME);

    final Artifact cliArtifact =
        MavenRepository.artifactFor(
            properties.cli().groupId(),
            properties.cli().artifactId(),
            Optional.ofNullable(version).orElse(properties.cli().version()));

    final Path javaExec = Paths.get(JAVA_HOME.toString(), JAVA_COMMAND);

    try {
      final Artifact resolved =
          localRepository.resolve(cliArtifact, Collections.singletonList(remoteRepository));

      final List<String> commandLine = new ArrayList<>();
      commandLine.add(javaExec.toString());
      commandLine.add("-jar");
      commandLine.add(resolved.getFile().getAbsolutePath());
      commandLine.addAll(Arrays.asList(args));

      Console.Display.result("Running", "[" + String.join(" ", commandLine) + "]");
      final Process process = new ProcessBuilder(commandLine).inheritIO().start();
      return process.waitFor();
    } catch (ArtifactResolutionException e) {
      Console.Display.exception(e);
      return ExitCode.GENERAL_ERROR.value();
    } catch (InterruptedException e) {
      Console.Display.exception(e);
      return ExitCode.FATAL_ERROR_SIGNAL.value();
    } catch (IOException e) {
      Console.Display.exception(e);
      return ExitCode.COMMAND_NOT_FOUND.value();
    }
  }
}
