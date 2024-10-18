package com.github.modw.command;

import static com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemOut;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.installation.InstallationException;
import org.eclipse.aether.repository.RemoteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.github.modw.CommandTestFixture;
import com.github.modw.Configuration;
import com.github.modw.MavenRepository;
import com.github.modw.RemoteRepositoryFactory;
import com.github.tomakehurst.wiremock.WireMockServer;

class CleanTest implements CommandTestFixture {

	Configuration modConfiguration;

	@Test
	void execute(@TempDir Path artifactPath) throws Exception {
		final Clean command = new Clean(modConfiguration);

		final Path artifact = Paths.get(artifactPath.toString(), "artifact.jar");
		Files.createFile(artifact);

		installArtifact(artifact.toFile(), "1.0.0");
		installArtifact(artifact.toFile(), "2.0.0");

		final String out = tapSystemOut(() -> {
			command.execute();
		});

		assertThat(modConfiguration.repoPath()).isDirectoryRecursivelyContaining("glob:**lombok-2.0.0.jar");
		assertThat(modConfiguration.repoPath()).isDirectoryNotContaining("glob:**lombok-1.0.0.jar");
		assertThat(out).contains("1.0.0");
	}

	@Override
	public void offer(Configuration modConfiguration) {
		this.modConfiguration = modConfiguration;
	}

	@Override
	public void offer(WireMockServer wireMockServer) {
		// TODO Auto-generated method stub

	}

	void installArtifact(final File file, final String version) throws InstallationException {
		final RemoteRepository remoteRepository = new RemoteRepositoryFactory(modConfiguration).get();
		final Artifact cliArtifact = MavenRepository
				.getArtifact(modConfiguration.getCliGroupId(), modConfiguration.getCliArtifactId(), version)
				.setFile(file);

		new MavenRepository(modConfiguration.repoPath()).installArtifact(cliArtifact,
				Collections.singletonList(remoteRepository));
	}

}
