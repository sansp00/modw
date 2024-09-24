package io.moderne.wrapper.cli;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Properties;

import org.apache.commons.io.file.PathUtils;

public class ModConfigurationProperties implements ModConfiguration {
	final Properties properties;

	public ModConfigurationProperties() {
		properties = new Properties();
		try {
			init();
		} catch (IOException e) {
		}
		try (BufferedReader reader = Files.newBufferedReader(propertiesFile().toFile().toPath())) {
			properties.load(reader);
		} catch (IOException e) {

		}

	}

	void init() throws IOException {
		final File root = modwPath().toFile();
		final File properties = propertiesFile().toFile();
		final File repo = repoPath().toFile();

		if (!root.exists()) {
			PathUtils.createParentDirectories(modwPath());

		}
		if (!repo.exists()) {
			PathUtils.createParentDirectories(repoPath());
		}
		if (!properties.exists()) {
			properties.createNewFile();
		}
	}

	public ModConfigurationProperties generate() {
		properties.setProperty(CLI_GROUPID_KEY, CLI_GROUPID_DEFAULT);
		properties.setProperty(CLI_ARTIFACTID_KEY, CLI_ARTIFACTID_DEFAULT);
		properties.setProperty(CLI_VERSION_KEY, CLI_VERSION_DEFAULT);
		properties.setProperty(REPOSITORY_ID_KEY, REPOSITORY_ID_DEFAULT);
		properties.setProperty(REPOSITORY_TYPE_KEY, REPOSITORY_TYPE_DEFAULT);
		properties.setProperty(REPOSITORY_URL_KEY, REPOSITORY_URL_DEFAULT);

		try (BufferedWriter writer = Files.newBufferedWriter(propertiesFile().toFile().toPath(),
				StandardOpenOption.TRUNCATE_EXISTING)) {
			properties.store(writer, "#");
		} catch (IOException e) {
		}

		return this;
	}

	public String getCliGroupId() {
		return properties.getProperty(CLI_GROUPID_KEY, CLI_GROUPID_DEFAULT);
	}

	public String getCliArtifactId() {
		return properties.getProperty(CLI_ARTIFACTID_KEY, CLI_ARTIFACTID_DEFAULT);
	}

	public String getCliVersion() {
		return properties.getProperty(CLI_VERSION_KEY, CLI_VERSION_DEFAULT);
	}

	public String getRepositoryId() {
		return properties.getProperty(REPOSITORY_ID_KEY, REPOSITORY_ID_DEFAULT);
	}

	public String getRepositoryType() {
		return properties.getProperty(REPOSITORY_TYPE_KEY, REPOSITORY_TYPE_DEFAULT);
	}

	public String getRepositoryUrl() {
		return properties.getProperty(REPOSITORY_URL_KEY, REPOSITORY_URL_DEFAULT);
	}

}
