package com.github.modw.command;

import java.io.File;
import java.util.Collections;

import com.github.modw.*;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.installation.InstallationException;
import org.eclipse.aether.repository.RemoteRepository;

public class Install implements Command {
  final Configuration configuration;
  final RemoteRepositoryFactory remoteRepositorySupplier;
  final File file;
  final String version;

  public Install(final Configuration configuration, final File file, final String version) {
    this.configuration = configuration;
    this.remoteRepositorySupplier = new RemoteRepositoryFactory(configuration);
    this.file = file;
    this.version = version;
  }

  public int execute(final String... args) {
    final RemoteRepository cliRepository = remoteRepositorySupplier.get();

    final Artifact cliArtifact =
        MavenRepository.getArtifact(
                configuration.getCliGroupId(), configuration.getCliArtifactId(), version)
            .setFile(file);

    try {
      new MavenRepository(configuration.repoPath())
          .installArtifact(cliArtifact, Collections.singletonList(cliRepository));
    } catch (InstallationException e) {
      return ExitCode.GENERAL_ERROR.value();
    }
    return ExitCode.OK.value();
  }
}
