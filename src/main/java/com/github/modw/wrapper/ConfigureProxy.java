package com.github.modw.wrapper;

import com.github.modw.ExitCode;
import com.github.modw.configuration.ConfigurationProperties;
import com.github.modw.configuration.ConfigurationPropertiesFactory;
import com.github.modw.configuration.Proxy;
import com.github.modw.console.Console;
import picocli.CommandLine;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;

@CommandLine.Command(
    name = "proxy",
    description = "Configure ModW proxy properties",
    mixinStandardHelpOptions = true)
public class ConfigureProxy implements Callable<Integer> {

  static final String COMMAND_NAME = "configure proxy";

  final ConfigurationProperties properties;

  @CommandLine.Option(
      names = {"-t", "--type"},
      description = "Set Proxy type")
  private String type;

  @CommandLine.Option(
      names = {"-h", "--host"},
      description = "Set Proxy host")
  private String host;

  @CommandLine.Option(
      names = {"-p", "--port"},
      description = "Set Proxy port")
  private Integer port;

  @CommandLine.Option(
      names = {"-n", "--username"},
      description = "Set Proxy username")
  private String username;

  @CommandLine.Option(
      names = {"-w", "--password"},
      description = "Set Proxy username")
  private String password;

  public ConfigureProxy() {
    this.properties = new ConfigurationPropertiesFactory().create();
  }

  @Override
  public Integer call() throws Exception {
    Console.Display.command(COMMAND_NAME);

    ConfigurationPropertiesFactory.store(
        new ConfigurationProperties(
            properties.cli(),
            Optional.of(
                new Proxy(
                    Objects.requireNonNullElse(
                        type, properties.proxy().map(Proxy::type).orElse(null)),
                    Objects.requireNonNullElse(
                        host, properties.proxy().map(Proxy::host).orElse(null)),
                    Objects.requireNonNullElse(
                        port, properties.proxy().map(Proxy::port).orElse(null)),
                    Optional.ofNullable(username)
                        .or(() -> properties.proxy().flatMap(Proxy::username)),
                    Optional.ofNullable(password)
                        .or(() -> properties.proxy().flatMap(Proxy::password)))),
            properties.repository(),
            properties.wrapper()));
    return ExitCode.OK.value();
  }
}
