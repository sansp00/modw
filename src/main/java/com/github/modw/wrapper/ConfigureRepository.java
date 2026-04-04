package com.github.modw.wrapper;

import com.github.modw.ExitCode;
import com.github.modw.configuration.ConfigurationProperties;
import com.github.modw.configuration.ConfigurationPropertiesFactory;
import com.github.modw.configuration.Repository;
import com.github.modw.console.Console;
import picocli.CommandLine;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;

@CommandLine.Command(
    name = "repository",
    description = "Configure ModW repository properties",
    mixinStandardHelpOptions = true)
public class ConfigureRepository implements Callable<Integer> {
  static final String COMMAND_NAME = "configure repository";

  final ConfigurationProperties properties;

  @CommandLine.Option(
      names = {"-i", "--id"},
      description = "Set Repository id")
  private String id;

  @CommandLine.Option(
      names = {"-t", "--type"},
      description = "Set Repository type")
  private String type;

  @CommandLine.Option(
      names = {"-u", "--url"},
      description = "Set Repository url")
  private String url;

  @CommandLine.Option(
      names = {"-n", "--username"},
      description = "Set Repository username")
  private String username;

  @CommandLine.Option(
      names = {"-w", "--password"},
      description = "Set Repository username")
  private String password;

  public ConfigureRepository() {
    this.properties = new ConfigurationPropertiesFactory().create();
  }

  @Override
  public Integer call() throws Exception {
    Console.Display.command(COMMAND_NAME);

    ConfigurationPropertiesFactory.store(
        new ConfigurationProperties(
            properties.cli(),
            properties.proxy(),
            new Repository(
                Objects.requireNonNullElse(id, properties.repository().id()),
                Objects.requireNonNullElse(type, properties.repository().type()),
                Objects.requireNonNullElse(url, properties.repository().url()),
                Optional.ofNullable(username).or(() -> properties.repository().username()),
                Optional.ofNullable(password).or(() -> properties.repository().password())),
            properties.wrapper()));
    return ExitCode.OK.value();
  }
}
