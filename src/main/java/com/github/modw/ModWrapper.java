package com.github.modw;

import java.io.File;
import java.nio.file.Paths;
import java.util.Optional;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.github.modw.command.Available;
import com.github.modw.command.Clean;
import com.github.modw.command.Download;
import com.github.modw.command.Install;
import com.github.modw.command.Run;

public class ModWrapper {

  public static void main(String[] args) {
    final Configuration configuration = new ConfigurationProperties();
    final BuildProperties buildProperties = new BuildProperties();

    final Options options = new Options();
    final Option helpOption = Option.builder("help").desc("display the Wrapper usages").build();
    final Option generateOption =
        Option.builder("generate")
            .desc("generate an example Moderne CLI wrapper configuration")
            .build();

    final Option downloadOption =
        Option.builder("download").desc("download Moderne CLI locally").build();
    final Option installOption = Option.builder("install").desc("install Moderne CLI").build();
    final Option availableOption =
        Option.builder("available").desc("display the available Moderne CLI versions").build();
    final Option cleanOption =
        Option.builder("clean").desc("delete the local Moderne CLI versions").build();
    final Option buildVersionOption = Option.builder("version").desc("Wrapper version").build();

    final Option versionArg =
        Option.builder("v").desc("Moderne CLI version (works with -download and run)").argName("version").build();
    final Option fileArg = Option.builder("f").desc("Moderne CLI file (works with -install)").argName("file").build();

    options.addOption(availableOption);
    options.addOption(generateOption);
    options.addOption(downloadOption);
    options.addOption(installOption);
    options.addOption(cleanOption);
    options.addOption(buildVersionOption);
    options.addOption(helpOption);
    options.addOption(versionArg);
    options.addOption(fileArg);

    int exitCode = ExitCode.OK.value();

    try {
      final CommandLineParser parser = new DefaultParser();
      final CommandLine cmd = parser.parse(options, args);

      if (cmd.hasOption(helpOption)) {
        HelpFormatter.builder().get().printHelp("Command line syntax:", options);
      } else if (cmd.hasOption(generateOption)) {
        // Generate configuration
        System.out.printf("Example configuration file :%n%s", configuration.generate());
      } else if (cmd.hasOption(availableOption)) {
        // Output available version
        exitCode = new Available(configuration).execute();
      } else if (cmd.hasOption(installOption)) {
        // Install new version to local repository
        if (!cmd.hasOption(versionArg) || !cmd.hasOption(fileArg)) {
          // Missing options
          System.out.println("Missing command option -v and/or -f");
        } else {
          final String version = cmd.getParsedOptionValue(versionArg);
          final File artifact = Paths.get(cmd.getParsedOptionValue(fileArg)).toFile();
          exitCode = new Install(configuration, artifact, version).execute();
        }
      } else if (cmd.hasOption(downloadOption)) {
        // Download version to local repository
        exitCode =
            new Download(
                    configuration,
                    cmd.hasOption(versionArg)
                        ? Optional.of(cmd.getParsedOptionValue(versionArg))
                        : Optional.empty())
                .execute();
      } else if (cmd.hasOption(cleanOption)) {
        // Download version to local repository
        exitCode = new Clean(configuration).execute();
      } else if (cmd.hasOption(buildVersionOption)) {
        System.out.printf(
            "modw%n-version: %s%n-built: %s%n",
            buildProperties.getVersion(), buildProperties.getBuildTime());
      } else {
        // Run (and download to local repository if necessary) the cli
        Optional<String> version = Optional.empty();
        if (cmd.hasOption(versionArg)) {
          version = Optional.of(cmd.getParsedOptionValue(versionArg));
        }
        exitCode = new Run(configuration, version).execute(cmd.getArgs());
      }

      System.exit(exitCode);
    } catch (ParseException e) {
      System.exit(ExitCode.MISUSE_OF_SHELL_BUILT_IN.value());
    }
  }
}
