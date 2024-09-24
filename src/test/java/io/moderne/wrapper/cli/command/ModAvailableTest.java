package io.moderne.wrapper.cli.command;

import static com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemOut;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.github.tomakehurst.wiremock.WireMockServer;

import io.moderne.wrapper.cli.ModConfiguration;

class ModAvailableTest implements TestModWFixture {

	ModConfiguration modConfiguration;

	@Test
	void execute() throws Exception {
		final ModAvailable command = new ModAvailable(modConfiguration);
		final String out = tapSystemOut(() -> {
			command.execute();
		});

		command.execute();
		assertThat(out).contains("1.18.34");
	}

	@Override
	public void offer(ModConfiguration modConfiguration) {
		this.modConfiguration = modConfiguration;
	}

	@Override
	public void offer(WireMockServer wireMockServer) {
		// TODO Auto-generated method stub

	}

}
