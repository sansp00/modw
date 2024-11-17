package com.github.modw.command;

import com.github.modw.*;
import com.github.modw.maven.MavenRepository;
import com.github.modw.maven.RemoteRepositoryFactory;
import java.io.IOException;
import java.util.Collections;
import java.util.Optional;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactResolutionException;

public class Update implements Command {

	final Configuration configuration;
	final RemoteRepositoryFactory remoteRepositorySupplier;
	final Optional<String> wrapperVersion;

	public Update(final Configuration configuration, final Optional<String> wrapperVersion) {
		this.configuration = configuration;
		this.remoteRepositorySupplier = new RemoteRepositoryFactory(configuration);
		this.wrapperVersion = wrapperVersion;
	}

	public int execute(final String... args) {
		System.out.println("Executing command [update]");
		try {
			final Artifact wrapperArtifact = MavenRepository.getArtifact(configuration.getWrapperGroupId(),
					configuration.getWrapperArtifactId(), "", configuration.getWrapperQualifier(),
					wrapperVersion.orElse("RELEASE"));

			final RemoteRepository repository = remoteRepositorySupplier.get();

			final Artifact resolved = new MavenRepository(configuration.repoPath()).resolveArtifact(wrapperArtifact,
					Collections.singletonList(repository));

			System.out.printf("Artifact resolved/downloaded %s:%s:%s => %s%n", resolved.getGroupId(),
					resolved.getArtifactId(), resolved.getVersion(), resolved.getFile());

			configuration.updateWrapperVersion(resolved.getVersion());
			configuration.save();

		} catch (ArtifactResolutionException e) {
			System.out.println("Artifact unresolved");
			System.out.printf("Error: %s%n", e);
			return ExitCode.GENERAL_ERROR.value();
		} catch (IOException e) {
			System.out.println("Unable to update modw.properties");
			System.out.printf("Error: %s%n", e);
			return ExitCode.GENERAL_ERROR.value();
		}
		return ExitCode.OK.value();
	}
}
