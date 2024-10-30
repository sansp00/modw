package com.github.modw.command;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactResolutionException;

import com.github.modw.Command;
import com.github.modw.Configuration;
import com.github.modw.ExitCode;
import com.github.modw.maven.MavenRepository;
import com.github.modw.maven.RemoteRepositoryFactory;

public class Run implements Command {

	final Configuration configuration;
	final RemoteRepositoryFactory remoteRepositorySupplier;
	final Optional<String> cliVersion;

	public Run(final Configuration configuration, final Optional<String> cliVersion) {
		this.configuration = configuration;
		this.remoteRepositorySupplier = new RemoteRepositoryFactory(configuration);
		this.cliVersion = cliVersion;
	}

	// java -jar /path/to/mod.jar "$@"

	public int execute(final String... args) {
		System.out.println("Executing command [run]");
		final Artifact cliArtifact = MavenRepository.getArtifact(configuration.getCliGroupId(),
				configuration.getCliArtifactId(), cliVersion.orElse(configuration.getCliVersion()));
		final RemoteRepository cliRepository = remoteRepositorySupplier.get();
		final Path javaExec = Paths.get(configuration.javaHome(), "/bin/java");

		try {
			final Artifact resolved = new MavenRepository(configuration.repoPath()).resolveArtifact(cliArtifact,
					Collections.singletonList(cliRepository));

			final List<String> commandLine = new ArrayList<>();
			commandLine.add(javaExec.toString());
			commandLine.add("-jar");
			commandLine.add(resolved.getFile().getAbsolutePath());
			commandLine.addAll(Arrays.asList(args));

			System.out.printf("Running '%s'%n%n", commandLine.stream().collect(Collectors.joining(" ")));
			final Process process = new ProcessBuilder(commandLine).inheritIO().start();
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
