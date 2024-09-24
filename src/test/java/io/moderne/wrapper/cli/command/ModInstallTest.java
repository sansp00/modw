package io.moderne.wrapper.cli.command;

import static com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemOut;
import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.github.tomakehurst.wiremock.WireMockServer;

import io.moderne.wrapper.cli.ModConfiguration;

class ModInstallTest implements TestModWFixture {

	ModConfiguration modConfiguration;

	@Test
	void execute(@TempDir Path artifactPath) throws Exception {
		final Path artifact = Paths.get(artifactPath.toString(), "artifact.jar");
		Files.createFile(artifact);

		final ModInstall command = new ModInstall(modConfiguration, artifact.toFile(), "TEST");

		final String out = tapSystemOut(() -> {
			command.execute();
		});

		assertThat(modConfiguration.repoPath()).isDirectoryRecursivelyContaining("glob:**lombok-TEST.jar");
	}

	@Override
	public void offer(ModConfiguration modConfiguration) {
		this.modConfiguration = modConfiguration;
	}

	@Override
	public void offer(WireMockServer wireMockServer) {
		// NoOp
	}

}
