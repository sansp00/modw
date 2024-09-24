package com.github.modw;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Properties;

/** modw build properties */
public class BuildProperties {
  final Properties properties;

  public BuildProperties() {
    properties = new Properties();

    final URL gitPropertiesURL = this.getClass().getResource("git.properties");
    if (Objects.nonNull(gitPropertiesURL)) {
      try (final BufferedReader reader =
          Files.newBufferedReader(Paths.get(gitPropertiesURL.toURI()))) {
        properties.load(reader);
      } catch (IOException | URISyntaxException e) {
        System.out.println(
            "Unable to read internal git.properties file, build properties will be unavailable");
        System.out.printf("See error: %s%n", e.toString());
      }
    } else {
      System.out.println("Missing internal git.properties file, build properties will be unavailable ...");
    }
  }

  public String getBuildTime() {
    return properties.getProperty("git.build.time", "N/A");
  }

  public String getVersion() {
    return properties.getProperty("git.build.version", "N/A");
  }

  public String getIdAbbreviated() {
    return properties.getProperty("git.commit.id.abbrev", "N/A");
  }

  public String getIdFull() {
    return properties.getProperty("git.commit.id.full", "N/A");
  }
}
