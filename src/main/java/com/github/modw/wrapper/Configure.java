package com.github.modw.wrapper;

import com.github.modw.ExitCode;
import com.github.modw.console.Console;
import org.apache.commons.io.output.WriterOutputStream;
import picocli.CommandLine;

import java.io.PrintStream;
import java.util.concurrent.Callable;

@CommandLine.Command(
    name = Configure.COMMAND_NAME,
    description = "Configure ModW properties",
    mixinStandardHelpOptions = true,
    subcommands = {ConfigureCli.class, ConfigureProxy.class, ConfigureRepository.class})
public class Configure implements Callable<Integer> {
  static final String COMMAND_NAME = "configure";

  @Override
  public Integer call() throws Exception {

    CommandLine.usage(this, Console.out());
    return ExitCode.OK.value(); // Or a specific exit code for no subcommand
  }
}
