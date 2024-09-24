package io.moderne.wrapper.cli.command;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactResolutionException;

import io.moderne.wrapper.cli.ExitCode;
import io.moderne.wrapper.cli.MavenRepository;
import io.moderne.wrapper.cli.ModConfiguration;

public class ModRun {

	final ModConfiguration configuration;
	final Optional<String> cliVersion;

	public ModRun(final ModConfiguration configuration, final Optional<String> cliVersion) {
		this.configuration = configuration;
		this.cliVersion = cliVersion;
	}

	// java -jar /path/to/mod.jar "$@"

	public int execute(final String... args) {
		final Artifact cliArtifact = MavenRepository.getArtifact(configuration.getCliGroupId(),
				configuration.getCliArtifactId(), cliVersion.orElse(configuration.getCliVersion()));
		final RemoteRepository cliRepository = MavenRepository.getRepository(configuration.getRepositoryId(),
				configuration.getRepositoryType(), configuration.getRepositoryUrl());
		final Path javaExec = Path.of(configuration.javaHome(), "/bin/java");

		try {
			final Artifact resolved = new MavenRepository(configuration.repoPath()).resolveArtifact(cliArtifact,
					Arrays.asList(cliRepository));

			final List<String> commandLine = new ArrayList<>();
			commandLine.add(javaExec.toString());
			commandLine.add("-jar");
			commandLine.add(resolved.getFile().getAbsolutePath());
			commandLine.addAll(Arrays.asList(args));

			final ProcessBuilder processBuilder = new ProcessBuilder(commandLine);// .inheritIO();

			final Process process = processBuilder.start();
			System.out.println(new String(process.getInputStream().readAllBytes()));
			System.err.println(new String(process.getErrorStream().readAllBytes()));
			return process.waitFor();
		} catch (ArtifactResolutionException e) {
			return ExitCode.GENERAL_ERROR.value();
		} catch (InterruptedException e) {
			return ExitCode.FATAL_ERROR_SIGNAL.value();
		} catch (IOException e) {
			return ExitCode.COMMAND_NOT_FOUND.value();
		}

	}

}
