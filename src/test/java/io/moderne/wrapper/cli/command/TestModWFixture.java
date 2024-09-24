package io.moderne.wrapper.cli.command;

import java.io.IOException;
import java.nio.file.Path;

import org.apache.commons.io.file.PathUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.io.TempDir;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;

import io.moderne.wrapper.cli.ModConfiguration;

@TestInstance(Lifecycle.PER_CLASS)
interface TestModWFixture {

	void offer(final ModConfiguration modConfiguration);

	void offer(final WireMockServer wireMockServer);

	static final WireMockServer wireMockServer = new WireMockServer(
			WireMockConfiguration.options().usingFilesUnderDirectory("src/test/resources"));

	@BeforeAll
	default void setup(@TempDir Path modwPath) throws IOException {
		wireMockServer.start();

		final ModConfiguration configuration = new ModConfiguration() {

			public Path modwPath() {
				return modwPath;
			}

			@Override
			public ModConfiguration generate() {
				return this;
			}

			@Override
			public String getCliGroupId() {
				return "org.projectlombok";
			}

			@Override
			public String getCliArtifactId() {
				return "lombok";
			}

			@Override
			public String getCliVersion() {
				return ModConfiguration.CLI_VERSION_DEFAULT;
			}

			@Override
			public String getRepositoryId() {
				return ModConfiguration.REPOSITORY_ID_DEFAULT;
			}

			@Override
			public String getRepositoryType() {
				return ModConfiguration.REPOSITORY_TYPE_DEFAULT;
			}

			@Override
			public String getRepositoryUrl() {
				return wireMockServer.baseUrl();
			}

		};

		if (!configuration.modwPath().toFile().exists()) {
			PathUtils.createParentDirectories(configuration.modwPath());

		}
		if (!configuration.repoPath().toFile().exists()) {
			PathUtils.createParentDirectories(configuration.repoPath());
		}

		offer(configuration);
		offer(wireMockServer);

	}

	@AfterAll
	default void teardown() {
		wireMockServer.stop();
	}

}