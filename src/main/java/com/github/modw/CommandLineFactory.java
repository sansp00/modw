package com.github.modw;

import com.github.modw.configuration.ConfigurationProperties;
import com.github.modw.configuration.ConfigurationPropertiesFactory;
import com.github.modw.maven.MavenRepository;
import com.github.modw.maven.RemoteRepositoryFactory;
import org.eclipse.aether.repository.RemoteRepository;
import picocli.CommandLine;

import java.nio.file.Path;

public class CommandLineFactory {
  public CommandLine.IFactory create() {
    final ConfigurationProperties properties = new ConfigurationPropertiesFactory().create();
    final RemoteRepository remoteRepository = new RemoteRepositoryFactory(properties).create();
    final Path localRepositoryLocation = Constants.MODW_REPO_PATH;

    return new CommandLine.IFactory() {
      @Override
      public <K> K create(final Class<K> cls) throws Exception {
        if (cls.isAnnotationPresent(com.github.modw.CommandLine.class)) {
          com.github.modw.CommandLine commandLineAnnotation = cls.getAnnotation(com.github.modw.CommandLine.class);
          final MavenRepository localRepository = new MavenRepository(localRepositoryLocation, commandLineAnnotation.offlineLocalRepository());
          return cls.getConstructor(
                  ConfigurationProperties.class, RemoteRepository.class, MavenRepository.class)
              .newInstance(properties, remoteRepository, localRepository);
        }

        // Fallback to default Picocli behavior for subcommands/other classes
        return CommandLine.defaultFactory().create(cls);
      }
    };
  }
}
