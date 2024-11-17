package com.github.modw.maven;

import com.github.modw.Configuration;
import java.util.Objects;
import java.util.function.Supplier;
import org.eclipse.aether.repository.Authentication;
import org.eclipse.aether.repository.Proxy;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.repository.RemoteRepository.Builder;
import org.eclipse.aether.util.repository.AuthenticationBuilder;

public class RemoteRepositoryFactory implements Supplier<RemoteRepository> {

  final Configuration configuration;

  public RemoteRepositoryFactory(final Configuration configuration) {
    this.configuration = configuration;
  }

  @Override
  public RemoteRepository get() {

    final Builder builder =
        new RemoteRepository.Builder(
            configuration.getRepositoryId(),
            configuration.getRepositoryType(),
            configuration.getRepositoryUrl());
    configureProxy(builder);
    configureAuthentication(builder);
    return builder.build();
  }

  void configureAuthentication(final Builder builder) {
    if (hasAuthenticationConfiguration()) {
      Authentication authentication =
          new AuthenticationBuilder()
              .addUsername(configuration.getRepositoryUsername())
              .addPassword(configuration.getRepositoryPassword())
              .build();

      builder.setAuthentication(authentication);
    }
  }

  void configureProxy(final Builder builder) {
    Authentication authentication = null;
    Proxy proxy = null;

    if (hasProxyAuthenticationConfiguration()) {
      authentication =
          new AuthenticationBuilder()
              .addUsername(configuration.getProxyUsername())
              .addPassword(configuration.getProxyPassword())
              .build();
    }
    if (hasProxyConfiguration()) {
      proxy =
          new Proxy(
              configuration.getProxyType(),
              configuration.getProxyHost(),
              configuration.getProxyPort(),
              authentication);
    }
    builder.setProxy(proxy);
  }

  boolean hasAuthenticationConfiguration() {
    return Objects.nonNull(configuration.getRepositoryUsername())
        && Objects.nonNull(configuration.getRepositoryPassword());
  }

  boolean hasProxyConfiguration() {
    return Objects.nonNull(configuration.getProxyType())
        && Objects.nonNull(configuration.getProxyHost())
        && (configuration.getProxyPort() != -1);
  }

  boolean hasProxyAuthenticationConfiguration() {
    return Objects.nonNull(configuration.getProxyUsername())
        && Objects.nonNull(configuration.getProxyPassword());
  }
}
