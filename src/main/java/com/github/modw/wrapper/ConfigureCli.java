package com.github.modw.wrapper;

import com.github.modw.ExitCode;
import com.github.modw.configuration.Cli;
import com.github.modw.configuration.ConfigurationProperties;
import com.github.modw.configuration.ConfigurationPropertiesFactory;
import com.github.modw.console.Console;
import picocli.CommandLine;

import java.util.Objects;
import java.util.concurrent.Callable;

@CommandLine.Command(
    name = "cli",
    description = "Configure ModW cli properties",
    mixinStandardHelpOptions = true)
public class ConfigureCli implements Callable<Integer> {
  static final String COMMAND_NAME = "configure cli";

  final ConfigurationProperties properties;

  @CommandLine.Option(
      names = {"-v", "--version"},
      description = "Version")
  String version;

  @CommandLine.Option(
      names = {"-g", "--groupId"},
      description = "GroupId")
  String groupId;

  @CommandLine.Option(
      names = {"-a", "--artifactId"},
      description = "ArtifactId")
  String artifactId;

  public ConfigureCli() {
    this.properties = new ConfigurationPropertiesFactory().create();
  }

  @Override
  public Integer call() throws Exception {
    Console.Display.command(COMMAND_NAME);

    ConfigurationPropertiesFactory.store(
        new ConfigurationProperties(
            new Cli(
                Objects.requireNonNullElse(groupId, properties.cli().groupId()),
                Objects.requireNonNullElse(artifactId, properties.cli().artifactId()),
                Objects.requireNonNullElse(version, properties.cli().version())),
            properties.proxy(),
            properties.repository(),
            properties.wrapper()));
    return ExitCode.OK.value();
  }
}
