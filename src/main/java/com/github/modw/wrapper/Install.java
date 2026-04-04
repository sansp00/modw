package com.github.modw.wrapper;

import com.github.modw.*;
import com.github.modw.Constants;
import com.github.modw.configuration.ConfigurationProperties;
import com.github.modw.configuration.ConfigurationPropertiesFactory;
import com.github.modw.console.Console;
import com.github.modw.maven.MavenRepository;
import com.github.modw.maven.RemoteRepositoryFactory;
import java.util.Collections;
import java.util.concurrent.Callable;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import picocli.CommandLine;

@CommandLine.Command(
    name = Install.COMMAND_NAME,
    description = "Install ModW")
public class Install implements Callable<Integer> {
  static final String COMMAND_NAME = "install";

  final ConfigurationProperties properties;
  final RemoteRepository remoteRepository;

  public Install() {
    this.properties = new ConfigurationPropertiesFactory().create();
    this.remoteRepository = new RemoteRepositoryFactory(properties).create();
  }

  @Override
  public Integer call() {
    Console.Display.command(COMMAND_NAME);

    try {
      final Artifact wrapperArtifact =
          MavenRepository.artifactFor(
              properties.wrapper().groupId(), properties.wrapper().artifactId(), properties.wrapper().version());

      final Artifact resolved =
          new MavenRepository(Constants.MODW_REPO_PATH)
              .resolve(wrapperArtifact, Collections.singletonList(remoteRepository));

      final String result =
          "%s:%s:%s => %s"
              .formatted(
                  resolved.getGroupId(),
                  resolved.getArtifactId(),
                  resolved.getVersion(),
                  resolved.getFile());
      Console.Display.result("Artifact resolved/downloaded", result);

      return ExitCode.OK.value();
    } catch (ArtifactResolutionException e) {
      Console.Display.error("Artifact unresolved");
      Console.Display.exception(e);
      return ExitCode.GENERAL_ERROR.value();
    }
  }
}
