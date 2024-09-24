package io.moderne.wrapper.cli;

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

import io.moderne.wrapper.cli.command.ModAvailable;
import io.moderne.wrapper.cli.command.ModClean;
import io.moderne.wrapper.cli.command.ModDownload;
import io.moderne.wrapper.cli.command.ModInstall;
import io.moderne.wrapper.cli.command.ModRun;

public class ModWrapper {

	public static void main(String[] args) {
		final ModConfiguration configuration = new ModConfigurationProperties();

		final Options options = new Options();
		final Option helpOption = Option.builder("help").desc("display the Wrapper usages").build();
		final Option generateOption = Option.builder("generate").desc("generate Moderne cli wrapper configuration")
				.build();

		final Option downloadOption = Option.builder("download").desc("download Moderne cli locally").build();
		final Option installOption = Option.builder("install").desc("install Moderne cli").build();
		final Option availableOption = Option.builder("available").desc("display the available Moderne cli versions")
				.build();
		final Option cleanOption = Option.builder("clean").desc("delete the local Moderne cli versions").build();

		final Option versionOption = Option.builder("v").desc("Moderne cli version").argName("version").build();
		final Option fileOption = Option.builder("f").desc("Moderne cli file").argName("file").build();

		options.addOption(availableOption);
		options.addOption(generateOption);
		options.addOption(downloadOption);
		options.addOption(installOption);
		options.addOption(cleanOption);
		options.addOption(helpOption);
		options.addOption(versionOption);
		options.addOption(fileOption);

		int exitCode = ExitCode.OK.value();

		try {
			final CommandLineParser parser = new DefaultParser();
			final CommandLine cmd = parser.parse(options, args);

			if (cmd.hasOption(helpOption)) {
				HelpFormatter.builder().get().printHelp("Command line syntax:", options);
			} else if (cmd.hasOption(generateOption)) {
				// Generate configuration
				configuration.generate();
				System.out.printf("Generate default configuration file");
			} else if (cmd.hasOption(availableOption)) {
				// Output available version
				exitCode = new ModAvailable(configuration).execute();
			} else if (cmd.hasOption(installOption)) {
				// Install new version to local repository
				if (!cmd.hasOption(versionOption) || !cmd.hasOption(fileOption)) {
					// Missing options
					System.out.printf("Missing command option -v and/or -f");
				} else {
					final String version = cmd.getParsedOptionValue(versionOption);
					final File artifact = Paths.get(cmd.getParsedOptionValue(fileOption)).toFile();
					exitCode = new ModInstall(configuration, artifact, version).execute();
				}
			} else if (cmd.hasOption(downloadOption)) {
				// Download version to local repository
				exitCode = new ModDownload(configuration,
						cmd.hasOption(versionOption) ? Optional.of(cmd.getParsedOptionValue(versionOption))
								: Optional.empty())
						.execute();
			} else if (cmd.hasOption(cleanOption)) {
				// Download version to local repository
				exitCode = new ModClean(configuration).execute();
			} else {
				// Run (and download to local repository if necessary) the cli
				Optional<String> version = Optional.empty();
				if (cmd.hasOption(versionOption)) {
					version = Optional.of(cmd.getParsedOptionValue(versionOption));
				}
				exitCode = new ModRun(configuration, version).execute(cmd.getArgs());
			}

			System.exit(exitCode);
		} catch (ParseException e) {
			System.exit(ExitCode.MISUSE_OF_SHELL_BUILT_IN.value());
		}
	}
}
