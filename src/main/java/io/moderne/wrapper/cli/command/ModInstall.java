package io.moderne.wrapper.cli.command;

import java.io.File;
import java.util.Arrays;

import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.installation.InstallationException;
import org.eclipse.aether.repository.RemoteRepository;

import io.moderne.wrapper.cli.ExitCode;
import io.moderne.wrapper.cli.MavenRepository;
import io.moderne.wrapper.cli.ModConfiguration;

public class ModInstall {
	final ModConfiguration configuration;
	final File file;
	final String version;

	public ModInstall(final ModConfiguration configuration, final File file, final String version) {
		this.configuration = configuration;
		this.file = file;
		this.version = version;
	}

	public int execute(final String... args) {
		final RemoteRepository cliRepository = MavenRepository.getRepository(configuration.getRepositoryId(),
				configuration.getRepositoryType(), configuration.getRepositoryUrl());

		final Artifact cliArtifact = MavenRepository
				.getArtifact(configuration.getCliGroupId(), configuration.getCliArtifactId(), version).setFile(file);

		try {
			new MavenRepository(configuration.repoPath()).installArtifact(cliArtifact, Arrays.asList(cliRepository));
		} catch (InstallationException e) {
			return ExitCode.GENERAL_ERROR.value();
		}
		return ExitCode.OK.value();
	}
}
