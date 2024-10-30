package com.github.modw.command;

import java.util.Collections;
import java.util.Optional;

import com.github.modw.*;
import com.github.modw.maven.MavenRepository;
import com.github.modw.maven.RemoteRepositoryFactory;

import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactResolutionException;

public class Download implements Command {

	final Configuration configuration;
	final RemoteRepositoryFactory remoteRepositorySupplier;
	final Optional<String> cliVersion;

	public Download(final Configuration configuration, final Optional<String> cliVersion) {
		this.configuration = configuration;
		this.remoteRepositorySupplier = new RemoteRepositoryFactory(configuration);
		this.cliVersion = cliVersion;
	}

	public int execute(final String... args) {
		System.out.println("Executing command [download]");
		try {
			final Artifact cliArtifact = MavenRepository.getArtifact(configuration.getCliGroupId(),
					configuration.getCliArtifactId(), cliVersion.orElse(configuration.getCliVersion()));

			final RemoteRepository cliRepository = remoteRepositorySupplier.get();

			final Artifact resolved = new MavenRepository(configuration.repoPath()).resolveArtifact(cliArtifact,
					Collections.singletonList(cliRepository));

			System.out.printf("Artifact resolved/downloaded %s:%s:%s => %s%n", resolved.getGroupId(),
					resolved.getArtifactId(), resolved.getVersion(), resolved.getFile());

		} catch (ArtifactResolutionException e) {
			System.out.println("Artifact unresolved");
			System.out.printf("Error: %s%n", e);
			return ExitCode.GENERAL_ERROR.value();
		}
		return ExitCode.OK.value();
	}
}
