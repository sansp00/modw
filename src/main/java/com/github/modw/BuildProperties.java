package com.github.modw;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/** modw build properties */
public class BuildProperties {
  final Properties properties;

  public BuildProperties() {
    properties = new Properties();
    try (final InputStream inputStream =
        this.getClass().getClassLoader().getResourceAsStream("git.properties")) {
      properties.load(inputStream);
    } catch (IOException e) {
      System.out.println(
          "Unable to read internal git.properties file, build properties will be unavailable");
      System.out.printf("See error: %s%n", e);
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
