package io.moderne.wrapper.cli.command;

import static com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemErrAndOut;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.github.tomakehurst.wiremock.WireMockServer;

import io.moderne.wrapper.cli.ExitCode;
import io.moderne.wrapper.cli.ModConfiguration;

class ModRunTest implements TestModWFixture {

	ModConfiguration modConfiguration;

	@Test
	void execute() throws Exception {

		final ModRun command = new ModRun(modConfiguration, Optional.empty());

		final String out = tapSystemErrAndOut(() -> {
			assertThat(command.execute("-version")).isEqualTo(ExitCode.OK.value());
		});
		assertThat(out).contains("v1.18.34 \"Envious Ferret\"\n");
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