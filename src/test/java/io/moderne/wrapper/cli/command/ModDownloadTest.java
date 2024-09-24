package io.moderne.wrapper.cli.command;

import static com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemOut;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.github.tomakehurst.wiremock.WireMockServer;

import io.moderne.wrapper.cli.ModConfiguration;

class ModDownloadTest implements TestModWFixture {

	ModConfiguration modConfiguration;

	@Test
	void execute() throws Exception {
		final ModDownload command = new ModDownload(modConfiguration, Optional.empty());

		final String out = tapSystemOut(() -> {
			command.execute();
		});

		assertThat(modConfiguration.repoPath()).isDirectoryRecursivelyContaining("glob:**lombok-1.18.34.jar");
	}

	@Override
	public void offer(final ModConfiguration modConfiguration) {
		this.modConfiguration = modConfiguration;
	}

	@Override
	public void offer(WireMockServer wireMockServer) {
		// NoOp
	}
}
