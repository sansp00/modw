package com.github.modw;

import com.github.modw.configuration.Cli;
import com.github.modw.configuration.ConfigurationProperties;
import com.github.modw.configuration.ConfigurationPropertiesFactory;
import com.github.modw.configuration.Repository;
import com.github.modw.maven.MavenRepository;
import com.github.modw.maven.RemoteRepositoryFactory;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import org.eclipse.aether.repository.RemoteRepository;
import org.github.gestalt.config.exceptions.GestaltException;
import org.github.gestalt.config.source.ClassPathConfigSourceBuilder;
import org.github.gestalt.config.source.ConfigSourcePackage;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public interface CommandFixture {

  default void offer(final WireMockServer wireMockServer) {}

  default void offer(final Repository mockRepository) {}

  default boolean recordExchanges() {
    return false;
  }

  final WireMockServer wireMockServer =
      new WireMockServer(
          WireMockConfiguration.options().usingFilesUnderDirectory("src/test/resources"));

  @BeforeAll
  default void setup() {
    wireMockServer.start();
    if (recordExchanges()) {
      wireMockServer.startRecording("https://repo1.maven.org/maven2");
    }

    offer(wireMockServer);
    offer(
        new Repository(
            "mock", "default", wireMockServer.baseUrl(), Optional.empty(), Optional.empty()));
  }

  @AfterAll
  default void teardown() {
    if (recordExchanges()) {
      wireMockServer.stopRecording();
    }
    wireMockServer.stop();
  }

  default List<String> availableVersions() {
    return List.of("0.1.2", "0.1.1", "0.1.0");
  }

  default String releaseVersion() {
    return "0.1.2";
  }

  default CommandHarness harness(final Path localRepository) {

    try {
      final List<ConfigSourcePackage> configurationSources =
          List.of(ClassPathConfigSourceBuilder.builder().setResource("modw.properties").build());
      final ConfigurationProperties properties =
          ConfigurationPropertiesFactory.update(
              new ConfigurationPropertiesFactory(configurationSources).create(),
              new Cli("org.jetbrains", "dummy", "RELEASE"),
              null,
              new Repository(
                  "mock", "default", wireMockServer.baseUrl(), Optional.empty(), Optional.empty()),
              null);
      final RemoteRepository repository = new RemoteRepositoryFactory(properties).create();

      return new CommandHarness(properties, repository, new MavenRepository(localRepository));
    } catch (GestaltException e) {
      throw new IllegalStateException(e);
    }
  }
}
