package com.github.modw.configuration;

import com.github.modw.Constants;
import com.github.modw.ModWrapperException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

import org.github.gestalt.config.Gestalt;
import org.github.gestalt.config.builder.GestaltBuilder;
import org.github.gestalt.config.exceptions.GestaltException;
import org.github.gestalt.config.source.*;

public class ConfigurationPropertiesFactory {
  final Gestalt gestalt;

  public ConfigurationPropertiesFactory(final List<ConfigSourcePackage> configSources) {
    try {
      final GestaltBuilder builder = new GestaltBuilder();
      configSources.forEach(builder::addSource);
      builder.setTreatMissingValuesAsErrors(false);
      this.gestalt = builder.build();
      gestalt.loadConfigs();
    } catch (GestaltException e) {
      throw new ModWrapperException(e);
    }
  }

  public ConfigurationPropertiesFactory() {
    this(configSources());
  }

  static List<ConfigSourcePackage> configSources() {
    try {
      return List.of(
          ClassPathConfigSourceBuilder.builder().setResource("modw.properties").build(),
          FileConfigSourceBuilder.builder()
              .setFile(Constants.MODW_PROPERTIES_PATH.toFile())
              .build(),
          EnvironmentConfigSourceBuilder.builder().setFailOnErrors(false).build(),
          SystemPropertiesConfigSourceBuilder.builder().setFailOnErrors(false).build());
    } catch (GestaltException e) {
      throw new ModWrapperException(e);
    }
  }

  public ConfigurationProperties create() {
    try {
      return gestalt.getConfig("", ConfigurationProperties.class);
    } catch (GestaltException e) {
      throw new ModWrapperException(e);
    }
  }

  public static ConfigurationProperties update(
      final ConfigurationProperties properties, final Repository repository) {
    return new ConfigurationProperties(
        properties.cli(), properties.proxy(), repository, properties.wrapper());
  }

  public static ConfigurationProperties update(
      final ConfigurationProperties properties, final Cli cli) {
    return new ConfigurationProperties(
        cli, properties.proxy(), properties.repository(), properties.wrapper());
  }

  public static ConfigurationProperties update(
      final ConfigurationProperties properties, final Proxy proxy) {
    return new ConfigurationProperties(
        properties.cli(),
        Optional.ofNullable(proxy),
        properties.repository(),
        properties.wrapper());
  }

  public static ConfigurationProperties update(
      final ConfigurationProperties properties, final Wrapper wrapper) {
    return new ConfigurationProperties(
        properties.cli(), properties.proxy(), properties.repository(), wrapper);
  }

  public static ConfigurationProperties update(
      final ConfigurationProperties properties,
      final Cli cli,
      final Proxy proxy,
      final Repository repository,
      final Wrapper wrapper) {

    return new ConfigurationProperties(
        Optional.ofNullable(cli).orElse(properties.cli()),
        Optional.ofNullable(Optional.ofNullable(proxy).orElse(properties.proxy().orElse(null))),
        Optional.ofNullable(repository).orElse(properties.repository()),
        Optional.ofNullable(wrapper).orElse(properties.wrapper()));
  }

  static void setProperties(final Repository repository, final Properties properties) {
    properties.setProperty("repository.id", repository.id());
    properties.setProperty("repository.type", repository.type());
    properties.setProperty("repository.url", repository.url());
    repository
        .username()
        .ifPresent(username -> properties.setProperty("repository.username", username));
    repository
        .password()
        .ifPresent(password -> properties.setProperty("repository.password", password));
  }

  static void setProperties(final Proxy proxy, final Properties properties) {
    properties.setProperty("proxy.type", proxy.type());
    properties.setProperty("proxy.host", proxy.host());
    properties.setProperty("proxy.port", String.valueOf(proxy.port()));
    proxy.username().ifPresent(username -> properties.setProperty("proxy.username", username));
    proxy.password().ifPresent(password -> properties.setProperty("proxy.password", password));
  }

  static void setProperties(final Cli cli, final Properties properties) {
    properties.setProperty("cli.groupId", cli.groupId());
    properties.setProperty("cli.version", cli.version());
    properties.setProperty("cli.artifactId", cli.artifactId());
  }

  static void setProperties(final Wrapper wrapper, final Properties properties) {
    properties.setProperty("wrapper.groupId", wrapper.groupId());
    properties.setProperty("wrapper.version", wrapper.version());
    properties.setProperty("wrapper.artifactId", wrapper.artifactId());
  }

  public static void store(final ConfigurationProperties configurationProperties) {
    final Properties properties = new Properties();

    try {
      try (final FileInputStream inputStream =
          new FileInputStream(Constants.MODW_PROPERTIES_PATH.toFile())) {
        properties.load(inputStream);
      }

      setProperties(configurationProperties.wrapper(), properties);
      setProperties(configurationProperties.cli(), properties);
      setProperties(configurationProperties.repository(), properties);
      configurationProperties.proxy().ifPresent(proxy -> setProperties(proxy, properties));

      try (FileOutputStream outputStream =
          new FileOutputStream(Constants.MODW_PROPERTIES_PATH.toFile())) {
        properties.store(outputStream, "Updated via Java Properties");
      }

    } catch (IOException e) {
      throw new ModWrapperException(e);
    }
  }
}
