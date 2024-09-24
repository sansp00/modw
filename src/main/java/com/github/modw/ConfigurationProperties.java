package com.github.modw;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;
import java.util.Properties;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.apache.commons.io.file.PathUtils;

/** modw configuration properties implementation based on basic Properties. */
public class ConfigurationProperties implements Configuration {
  static final String REPOSITORY_ID_KEY = "repository.id";
  static final String REPOSITORY_ID_DEFAULT = "central";

  static final String REPOSITORY_TYPE_KEY = "repository.key";
  static final String REPOSITORY_TYPE_DEFAULT = "default";

  static final String REPOSITORY_URL_KEY = "repository.url";
  static final String REPOSITORY_URL_DEFAULT = "https://repo1.maven.org/maven2/";

  static final String REPOSITORY_USERNAME_KEY = "repository.username";
  static final String REPOSITORY_USERNAME_DEFAULT = "username";

  static final String REPOSITORY_PASSWORD_KEY = "repository.password";
  static final String REPOSITORY_PASSWORD_DEFAULT = "password";

  static final String CLI_GROUPID_KEY = "cli.groupId";
  static final String CLI_GROUPID_DEFAULT = "io.moderne";

  static final String CLI_ARTIFACTID_KEY = "cli.artifactId";
  static final String CLI_ARTIFACTID_DEFAULT = "moderne-cli";

  static final String CLI_VERSION_KEY = "cli.version";
  static final String CLI_VERSION_DEFAULT = "RELEASE";

  static final String PROXY_TYPE_KEY = "proxy.type";
  static final String PROXY_TYPE_DEFAULT = "https";

  static final String PROXY_HOST_KEY = "proxy.host";
  static final String PROXY_HOST_DEFAULT = "localhost";

  static final String PROXY_PORT_KEY = "proxy.port";
  static final int PROXY_PORT_DEFAULT = -1;

  static final String PROXY_USERNAME_KEY = "proxy.username";
  static final String PROXY_USERNAME_DEFAULT = "username";

  static final String PROXY_PASSWORD_KEY = "proxy.password";
  static final String PROXY_PASSWORD_DEFAULT = "password";

  final Properties properties;

  final Function<String, String> envOrVal =
      val ->
          Objects.nonNull(val) && val.startsWith("%ENV.")
              ? System.getenv().get(val.substring(5))
              : val;

  public ConfigurationProperties() {
    properties = new Properties();
    try {
      init();
    } catch (IOException e) {
      System.out.println("Unable to initialize directory structure, please validate installation");
      System.out.printf("See error: %s%n", e.toString());
    }
    try (BufferedReader reader = Files.newBufferedReader(propertiesFile().toFile().toPath())) {
      properties.load(reader);
    } catch (IOException e) {
      System.out.printf("Unable to read %s file, please validate installation%n", propertiesFile());
      System.out.printf("See error: %s%n", e.toString());
    }
  }

  void init() throws IOException {
    final File root = modwPath().toFile();
    final File properties = propertiesFile().toFile();
    final File repo = repoPath().toFile();

    if (!root.exists()) {
      PathUtils.createParentDirectories(modwPath());
    }
    if (!repo.exists()) {
      PathUtils.createParentDirectories(repoPath());
    }
    if (!properties.exists()) {
      properties.createNewFile();
    }
  }

  public String generate() {
    final BiFunction<String, String, String> property =
        (key, value) -> String.format("%s=%s%n", key, value);
    final Function<String, String> comment = val -> String.format("# %s%n", val);
    final StringBuilder sb = new StringBuilder();
    sb.append(comment.apply("Moderne CLI GAV"));
    sb.append(property.apply(CLI_GROUPID_KEY, CLI_GROUPID_DEFAULT));
    sb.append(property.apply(CLI_ARTIFACTID_KEY, CLI_ARTIFACTID_DEFAULT));
    sb.append(property.apply(CLI_VERSION_KEY, CLI_VERSION_DEFAULT));
    sb.append(comment.apply("Repository configuration"));
    sb.append(property.apply(REPOSITORY_ID_KEY, REPOSITORY_ID_DEFAULT));
    sb.append(property.apply(REPOSITORY_TYPE_KEY, REPOSITORY_TYPE_DEFAULT));
    sb.append(property.apply(REPOSITORY_URL_KEY, REPOSITORY_URL_DEFAULT));
    sb.append(comment.apply("Repository authentication configuration (Optional)"));
    sb.append(property.apply(REPOSITORY_USERNAME_KEY, REPOSITORY_USERNAME_DEFAULT));
    sb.append(property.apply(REPOSITORY_PASSWORD_KEY, REPOSITORY_PASSWORD_DEFAULT));
    sb.append(comment.apply("Repository proxy configuration (Optional)"));
    sb.append(property.apply(PROXY_TYPE_KEY, PROXY_TYPE_DEFAULT));
    sb.append(property.apply(PROXY_HOST_KEY, PROXY_HOST_DEFAULT));
    sb.append(property.apply(PROXY_PORT_KEY, String.valueOf(PROXY_PORT_DEFAULT)));
    sb.append(comment.apply("Repository proxy user configuration (Optional)"));
    sb.append(property.apply(PROXY_USERNAME_KEY, PROXY_USERNAME_DEFAULT));
    sb.append(property.apply(PROXY_PASSWORD_KEY, PROXY_PASSWORD_DEFAULT));
    return sb.toString();
  }

  public String getCliGroupId() {
    return envOrVal.apply(properties.getProperty(CLI_GROUPID_KEY, CLI_GROUPID_DEFAULT));
  }

  public String getCliArtifactId() {
    return envOrVal.apply(properties.getProperty(CLI_ARTIFACTID_KEY, CLI_ARTIFACTID_DEFAULT));
  }

  public String getCliVersion() {
    return envOrVal.apply(properties.getProperty(CLI_VERSION_KEY, CLI_VERSION_DEFAULT));
  }

  public String getRepositoryId() {
    return envOrVal.apply(properties.getProperty(REPOSITORY_ID_KEY, REPOSITORY_ID_DEFAULT));
  }

  public String getRepositoryType() {
    return envOrVal.apply(properties.getProperty(REPOSITORY_TYPE_KEY, REPOSITORY_TYPE_DEFAULT));
  }

  public String getRepositoryUrl() {
    return envOrVal.apply(properties.getProperty(REPOSITORY_URL_KEY, REPOSITORY_URL_DEFAULT));
  }

  @Override
  public String getProxyType() {
    return envOrVal.apply(properties.getProperty(PROXY_TYPE_KEY));
  }

  @Override
  public String getProxyHost() {
    return envOrVal.apply(properties.getProperty(PROXY_HOST_KEY));
  }

  @Override
  public int getProxyPort() {
    return Integer.parseInt(
        envOrVal.apply(properties.getProperty(PROXY_PORT_KEY, String.valueOf(PROXY_PORT_DEFAULT))));
  }

  @Override
  public String getProxyUsername() {
    return envOrVal.apply(properties.getProperty(PROXY_USERNAME_KEY));
  }

  @Override
  public String getProxyPassword() {
    return envOrVal.apply(properties.getProperty(PROXY_PASSWORD_KEY));
  }

  @Override
  public String getRepositoryUsername() {
    return envOrVal.apply(properties.getProperty(REPOSITORY_USERNAME_KEY));
  }

  @Override
  public String getRepositoryPassword() {
    return envOrVal.apply(properties.getProperty(REPOSITORY_PASSWORD_KEY));
  }
}
