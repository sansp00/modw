package com.github.modw.wrapper;

import com.github.modw.ExitCode;
import com.github.modw.configuration.Cli;
import com.github.modw.configuration.ConfigurationProperties;
import com.github.modw.configuration.ConfigurationPropertiesFactory;
import com.github.modw.configuration.Repository;
import com.github.modw.console.Console;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Optional;
import java.util.concurrent.Callable;
import picocli.CommandLine;

import static com.github.modw.Constants.*;

@CommandLine.Command(name = "setup", description = "Setup ModW", mixinStandardHelpOptions = true)
public class Setup implements Callable<Integer> {
  static final String COMMAND_NAME = "setup";

  @CommandLine.Option(
      names = {"-op", "--override-properties"},
      description = "Override properties file",
      defaultValue = "true")
  boolean overrideProperties;

  @CommandLine.Option(
      names = {"-os", "--override-scripts"},
      description = "Override script files",
      defaultValue = "true")
  boolean overrideScripts;

  public Setup() { // NoOp
  }

  @Override
  public Integer call() throws Exception {
    Console.Display.command(COMMAND_NAME);
    if (MODW_PATH.toFile().mkdirs()) {
      Console.Display.success("Created ModW directory at %s".formatted(MODW_PATH));
    } else {
      Console.Display.warning("ModW directory at %s exists".formatted(MODW_PATH));
    }

    if (MODW_REPO_PATH.toFile().mkdirs()) {
      Console.Display.success("Created ModW repository directory at %s".formatted(MODW_REPO_PATH));
    } else {
      Console.Display.warning("ModW repository directory at %s exists".formatted(MODW_REPO_PATH));
    }

    if (!MODW_PROPERTIES_PATH.toFile().exists() || overrideProperties) {
      ConfigurationPropertiesFactory.store(
          new ConfigurationProperties(
              new Cli("io.moderne", "moderne-cli", "RELEASE"),
              Optional.empty(),
              new Repository(
                  "central",
                  "maven",
                  "https://repo.maven.apache.org/maven2/",
                  Optional.empty(),
                  Optional.empty()),
              new com.github.modw.configuration.Wrapper("github.modw", "modw", "RELEASE")));
      Console.Display.success("Created ModW property file at %s".formatted(MODW_PROPERTIES_PATH));
    } else {
      Console.Display.warning("ModW property file at %s exists".formatted(MODW_REPO_PATH));
    }

    try {
      var modwShPath = MODW_PATH.resolve("modw.sh");
      if (extractRessoure("scripts/modw.sh", modwShPath, overrideScripts)) {
        Console.Display.success("Created ModW script at %s".formatted(modwShPath));
      } else {
        Console.Display.warning("ModW shell at %s exists".formatted(modwShPath));
      }

      var modwPs1Path = MODW_PATH.resolve("modw.ps1");
      if (extractRessoure("scripts/modw.ps1", modwPs1Path, overrideScripts)) {
        Console.Display.success("Created ModW script at %s".formatted(modwPs1Path));
      } else {
        Console.Display.warning("ModW script at %s exists".formatted(modwPs1Path));
      }

      var modwCmdPath = MODW_PATH.resolve("modw.cmd");
      if (extractRessoure("scripts/modw.cms", modwCmdPath, overrideScripts)) {
        Console.Display.success("Created ModW script at %s".formatted(modwCmdPath));
      } else {
        Console.Display.warning("ModW script at %s exists".formatted(modwCmdPath));
      }

    } catch (IOException e) {
      Console.Display.error("ModW setup failed to setup wrapper scripts");
      Console.Display.exception(e);
      return ExitCode.GENERAL_ERROR.value();
    }

    Console.Display.success("ModW setup completed");
    Console.Display.message(
        "Please review the properties file at %s to configure ModW and run the wrapper install command");
    Console.Display.message(
        "Once done, add the shell appropriate ModW script in %s to your path".formatted(MODW_PATH));

    return ExitCode.OK.value();
  }

  static boolean extractRessoure(
      final String resourcePath, final Path destination, final boolean override)
      throws IOException {
    try (final InputStream is = Setup.class.getResourceAsStream(resourcePath)) {
      if (is == null) {
        throw new IOException("Resource not found: " + resourcePath);
      }

      if (destination.toFile().exists() && !override) {
        return false;
      }

      if (!destination.getParent().toFile().exists()) {
        Files.createDirectories(destination.getParent());
      }

      Files.copy(is, destination, StandardCopyOption.REPLACE_EXISTING);
      Files.setPosixFilePermissions(destination, PosixFilePermissions.fromString("rwxr-xr-x"));
      return true;
    }
  }
}
