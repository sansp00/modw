package com.github.modw.command;

import java.util.Collections;
import java.util.Set;

import com.github.modw.*;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.repository.RemoteRepository;

public class Installed implements Command {
	final Configuration configuration;
	final RemoteRepositoryFactory remoteRepositorySupplier;

	public Installed(final Configuration configuration) {
		this.configuration = configuration;
		this.remoteRepositorySupplier = new RemoteRepositoryFactory(configuration);
	}

	@Override
	public int execute(final String... args) {
		System.out.println("-> Executing command [installed]");
		final RemoteRepository cliRepository = remoteRepositorySupplier.get();

		final Artifact cliArtifact = MavenRepository.getArtifact(configuration.getCliGroupId(),
				configuration.getCliArtifactId(), configuration.getCliVersion());

		final Set<String> versions = new MavenRepository(configuration.repoPath()).installed(cliArtifact,
				Collections.singletonList(cliRepository));
		System.out.println("Installed versions:");
		versions.stream().sorted().forEach(version -> {
			System.out.printf("- %s%n", version);
		});
		return ExitCode.OK.value();
	}
}
