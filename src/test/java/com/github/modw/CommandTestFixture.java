package com.github.modw;

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

import static com.github.modw.ConfigurationProperties.*;

@TestInstance(Lifecycle.PER_CLASS)
public interface CommandTestFixture {

	void offer(final Configuration modConfiguration);

	void offer(final WireMockServer wireMockServer);

	static final WireMockServer wireMockServer = new WireMockServer(
			WireMockConfiguration.options().usingFilesUnderDirectory("src/test/resources"));

	@BeforeAll
	default void setup(@TempDir Path modwPath) throws IOException {
		wireMockServer.start();

		final Configuration configuration = new Configuration() {

			public Path modwPath() {
				return modwPath;
			}

			@Override
			public String generate() {
				return "";
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
				return CLI_VERSION_DEFAULT;
			}

			@Override
			public String getRepositoryId() {
				return REPOSITORY_ID_DEFAULT;
			}

			@Override
			public String getRepositoryType() {
				return REPOSITORY_TYPE_DEFAULT;
			}

			@Override
			public String getRepositoryUrl() {
				return wireMockServer.baseUrl();
			}

			@Override
			public String getRepositoryUsername() {
				return null;
			}

			@Override
			public String getRepositoryPassword() {
				return null;
			}

			@Override
			public String getProxyType() {
				return null;
			}

			@Override
			public String getProxyHost() {
				return null;
			}

			@Override
			public int getProxyPort() {
				return PROXY_PORT_DEFAULT;
			}

			@Override
			public String getProxyUsername() {
				return null;
			}

			@Override
			public String getProxyPassword() {
				return null;
			}

			@Override
			public String getWrapperGroupId() {
				return "com.github.modw";
			}

			@Override
			public String getWrapperArtifactId() {
				return "modw";
			}

			@Override
			public String getWrapperVersion() {

				return "RELEASE";
			}

			@Override
			public String getWrapperQualifier() {

				return "pg";
			}

			@Override
			public String updateWrapperVersion(String version) {
				return version;
			}
			@Override
			public void save() {
				
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
