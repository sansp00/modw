package com.github.modw.maven;

import com.github.modw.configuration.ConfigurationProperties;
import org.eclipse.aether.repository.Authentication;
import org.eclipse.aether.repository.Proxy;
import org.eclipse.aether.util.repository.AuthenticationBuilder;

public class RemoteRepositoryFactory {

  private final ConfigurationProperties properties;

  public RemoteRepositoryFactory(final ConfigurationProperties properties) {
    this.properties = properties;
  }

  /**
   * Creates a RemoteRepository instance
   *
   * @return RemoteRepository
   */
  public org.eclipse.aether.repository.RemoteRepository create() {
    final org.eclipse.aether.repository.RemoteRepository.Builder builder =
        new org.eclipse.aether.repository.RemoteRepository.Builder(
            properties.repository().id(),
            properties.repository().type(),
            properties.repository().url());
    configureProxy(builder);
    configureAuthentication(builder);
    return builder.build();
  }

  void configureAuthentication(
      final org.eclipse.aether.repository.RemoteRepository.Builder builder) {
    if (hasAuthenticationConfiguration()) {
      Authentication authentication =
          new AuthenticationBuilder()
              .addUsername(properties.repository().username().orElse(""))
              .addPassword(properties.repository().password().orElse(""))
              .build();

      builder.setAuthentication(authentication);
    }
  }

  void configureProxy(final org.eclipse.aether.repository.RemoteRepository.Builder builder) {
    Authentication authentication = null;
    Proxy proxy = null;

    if (hasProxyConfiguration()) {
      authentication =
          new AuthenticationBuilder()
              .addUsername(
                  properties
                      .proxy()
                      .flatMap(com.github.modw.configuration.Proxy::username)
                      .orElse(""))
              .addPassword(
                  properties
                      .proxy()
                      .flatMap(com.github.modw.configuration.Proxy::password)
                      .orElse(""))
              .build();
    }
    if (hasProxyConfiguration()) {
      proxy =
          new Proxy(
              properties.proxy().map(com.github.modw.configuration.Proxy::type).orElse(""),
              properties.proxy().map(com.github.modw.configuration.Proxy::host).orElse(""),
              properties.proxy().map(com.github.modw.configuration.Proxy::port).orElse(-1),
              authentication);
    }
    builder.setProxy(proxy);
  }

  boolean hasAuthenticationConfiguration() {
    return properties.repository().username().isPresent();
  }

  boolean hasProxyConfiguration() {
    return properties.proxy().isPresent();
  }

  boolean hasProxyAuthenticationConfiguration() {
    return properties.proxy().map(com.github.modw.configuration.Proxy::username).isPresent();
  }
}
