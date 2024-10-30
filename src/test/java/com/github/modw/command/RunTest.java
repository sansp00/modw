package com.github.modw.command;

import static com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemErrAndOut;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.github.modw.CommandTestFixture;
import com.github.modw.Configuration;
import com.github.modw.ExitCode;
import com.github.tomakehurst.wiremock.WireMockServer;

class RunTest implements CommandTestFixture {

	Configuration modConfiguration;

	@Test
	void execute() throws Exception {

		final Run command = new Run(modConfiguration, Optional.empty());

		final String out = tapSystemErrAndOut(() -> {
			assertThat(command.execute("-version")).isEqualTo(ExitCode.OK.value());
			command.execute("-version");
		});
	}

	@Override
	public void offer(final Configuration modConfiguration) {
		this.modConfiguration = modConfiguration;
	}

	@Override
	public void offer(WireMockServer wireMockServer) {
		// NoOp
	}

}