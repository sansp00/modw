package io.moderne.wrapper.cli.command;

import java.util.Arrays;
import java.util.Set;

import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.repository.RemoteRepository;

import io.moderne.wrapper.cli.ExitCode;
import io.moderne.wrapper.cli.MavenRepository;
import io.moderne.wrapper.cli.ModConfiguration;

public class ModClean {
	final ModConfiguration configuration;

	public ModClean(final ModConfiguration configuration) {
		this.configuration = configuration;
	}

	public int execute(final String... args) {
		final RemoteRepository cliRepository = MavenRepository.getRepository(configuration.getRepositoryId(),
				configuration.getRepositoryType(), configuration.getRepositoryUrl());

		final Artifact cliArtifact = MavenRepository.getArtifact(configuration.getCliGroupId(),
				configuration.getCliArtifactId(), configuration.getCliVersion());

		final Set<String> versions = new MavenRepository(configuration.repoPath()).clean(cliArtifact,
				Arrays.asList(cliRepository));

		System.out.printf("Cleaned versions:");
		versions.stream().forEach(version -> {
			System.out.printf("- %s", version);
		});
		return ExitCode.OK.value();
	}
}
