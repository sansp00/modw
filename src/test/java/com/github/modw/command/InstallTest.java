package com.github.modw.command;

import static com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemOut;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.modw.CommandTestFixture;
import com.github.modw.Configuration;
import com.github.tomakehurst.wiremock.WireMockServer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class InstallTest implements CommandTestFixture {

	Configuration modConfiguration;

	@Test
	void execute(@TempDir Path artifactPath) throws Exception {
		final Path artifact = Paths.get(artifactPath.toString(), "artifact.jar");
		Files.createFile(artifact);

		final Install command = new Install(modConfiguration, artifact.toFile(), "TEST");

		final String out = tapSystemOut(() -> {
			command.execute();
		});

		System.out.println(out);
		assertThat(modConfiguration.repoPath()).isDirectoryRecursivelyContaining("glob:**lombok-TEST.jar");
	}

	@Override
	public void offer(Configuration modConfiguration) {
		this.modConfiguration = modConfiguration;
	}

	@Override
	public void offer(WireMockServer wireMockServer) {
		// NoOp
	}

}
