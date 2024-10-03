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
        final Option informationOption = Option.builder("info").desc("Wrapper information").build();

        options.addOption(availableOption);
        options.addOption(generateOption);
        options.addOption(downloadOption);
        options.addOption(installOption);
        options.addOption(cleanOption);
        options.addOption(informationOption);
        options.addOption(helpOption);

        final Option versionArg =
                Option.builder("v").desc("Moderne CLI version (works with -download and run)").argName("version").hasArg().build();
        final Option fileArg = Option.builder("f").desc("Moderne CLI file (works with -install)").argName("file").hasArg().build();
        options.addOption(versionArg);
        options.addOption(fileArg);

        int exitCode = ExitCode.OK.value();

        try {
            final CommandLineParser parser = new DefaultParser();
            final CommandLine cmd = parser.parse(options, args, true);

            if (cmd.hasOption(helpOption)) {
                HelpFormatter.builder().get().printHelp("Usage: modw [MODW OPTIONS]... [MOD OPTIONS]...", options);
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
                    final String version = cmd.getOptionValue(versionArg);
                    final File artifact = Paths.get(cmd.getOptionValue(fileArg)).toFile();
                    exitCode = new Install(configuration, artifact, version).execute();
                }
            } else if (cmd.hasOption(downloadOption)) {
                // Download version to local repository
                final Optional<String> versionOpt = cmd.hasOption(versionArg)
                        ? Optional.of(cmd.getOptionValue(versionArg))
                        : Optional.empty();
                exitCode =
                        new Download(
                                configuration,
                                versionOpt)
                                .execute();
            } else if (cmd.hasOption(cleanOption)) {
                // Download version to local repository
                exitCode = new Clean(configuration).execute();
            } else if (cmd.hasOption(informationOption)) {
                System.out.println("modw information");
                System.out.printf(
                        "-version: %s%n-built: %s%n",
                        buildProperties.getVersion(), buildProperties.getBuildTime());
            } else {
                // Run (and download to local repository if necessary) the cli
                final Optional<String> versionOpt = cmd.hasOption(versionArg)
                        ? Optional.of(cmd.getOptionValue(versionArg))
                        : Optional.empty();
                exitCode = new Run(configuration, versionOpt).execute(cmd.getArgs());
            }

            System.exit(exitCode);
        } catch (ParseException e) {
            System.exit(ExitCode.MISUSE_OF_SHELL_BUILT_IN.value());
        }
    }
}
