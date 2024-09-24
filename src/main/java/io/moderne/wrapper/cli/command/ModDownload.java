package io.moderne.wrapper.cli.command;

import java.util.Arrays;
import java.util.Optional;

import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactResolutionException;

import io.moderne.wrapper.cli.ExitCode;
import io.moderne.wrapper.cli.MavenRepository;
import io.moderne.wrapper.cli.ModConfiguration;

public class ModDownload {

	final ModConfiguration configuration;
	final Optional<String> cliVersion;

	public ModDownload(final ModConfiguration configuration, final Optional<String> cliVersion) {
		this.configuration = configuration;
		this.cliVersion = cliVersion;

	}

	public int execute(final String... args) {

		try {
			final Artifact cliArtifact = MavenRepository.getArtifact(configuration.getCliGroupId(),
					configuration.getCliArtifactId(), cliVersion.orElse(configuration.getCliVersion()));

			final RemoteRepository cliRepository = MavenRepository.getRepository(configuration.getRepositoryId(),
					configuration.getRepositoryType(), configuration.getRepositoryUrl());

			final Artifact resolved = new MavenRepository(configuration.repoPath()).resolveArtifact(cliArtifact,
					Arrays.asList(cliRepository));

			System.out.printf("Artifact resolved (GAV => file): %s:%s:%s => %s", resolved.getGroupId(),
					resolved.getArtifactId(), resolved.getVersion(), resolved.getFile());

		} catch (ArtifactResolutionException e) {
			System.out.printf("Artifact unresolved");
			System.out.printf("Error: %s", e.toString());
			return ExitCode.GENERAL_ERROR.value();
		}
		return ExitCode.OK.value();
	}
}
