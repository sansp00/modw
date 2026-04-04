package com.github.modw.maven;

import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.repository.LocalRepository;

import java.nio.file.Path;
import java.util.function.Supplier;

public class RepositorySystemSessionSupplier implements Supplier<RepositorySystemSession> {
  final RepositorySystem repositorySystem;
  final Path repositoryPath;
  final boolean offline;

  public RepositorySystemSessionSupplier(
      final RepositorySystem repositorySystem, final Path repositoryPath, final boolean offline) {
    this.repositorySystem = repositorySystem;
    this.repositoryPath = repositoryPath;
    this.offline = offline;
  }

  @Override
  public RepositorySystemSession get() {
    final DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();
    session.setLocalRepositoryManager(
        repositorySystem.newLocalRepositoryManager(
            session, new LocalRepository(repositoryPath.toString())));
    session.setOffline(offline);
    session.setTransferListener(new LoggingTransferListener());
    return session;
  }
}
