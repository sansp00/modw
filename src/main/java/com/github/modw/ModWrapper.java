package com.github.modw;

import com.github.modw.command.*;
import com.github.modw.configuration.ConfigurationProperties;
import com.github.modw.configuration.ConfigurationPropertiesFactory;
import com.github.modw.console.Console;
import com.github.modw.maven.RemoteRepositoryFactory;
import com.github.modw.wrapper.Configure;
import org.apache.commons.io.output.WriterOutputStream;
import org.eclipse.aether.repository.RemoteRepository;
import picocli.CommandLine;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

@CommandLine.Command(
    name = ModWrapper.COMMAND_NAME,
    description = "Moderne CLI wrapper to ease version management",
    mixinStandardHelpOptions = true,
    subcommands = {
      Available.class,
      Clean.class,
      Download.class,
      Install.class,
      Installed.class,
      Run.class,
      Configure.class
    },
    version = "1.0")
public class ModWrapper implements Runnable {
  static final String COMMAND_NAME = "modw";

  @CommandLine.Spec CommandLine.Model.CommandSpec spec;

  @Override
  public void run() {
    Console.Display.logo(Constants.MODW_LOGO);
  }

  public static void main(String[] args) {
    final Logger root = Logger.getLogger("");
    root.setLevel(Level.OFF);
    for (Handler handler : root.getHandlers()) {
      handler.setLevel(Level.OFF);
    }

    Console.setup();

    int exitCode =
        new CommandLine(new ModWrapper(), new CommandLineFactory().create())
            .setColorScheme(Console.colorScheme())
            .setParameterExceptionHandler(
                (exception, arguments) -> {
                  final CommandLine commandLine = exception.getCommandLine();
                  Console.Display.error("Invalid input: " + commandLine);
                  Console.Display.exception(exception);
                  return commandLine.getCommandSpec().exitCodeOnInvalidInput();
                })
            .setExecutionExceptionHandler(
                (exception, commandLine, parseResult) -> {
                  Console.Display.error("Error executing: " + commandLine);
                  Console.Display.exception(exception);
                  return commandLine.getCommandSpec().exitCodeOnExecutionException();
                })
            .execute(args);

    Console.teardown();
    System.exit(exitCode);
  }
}
