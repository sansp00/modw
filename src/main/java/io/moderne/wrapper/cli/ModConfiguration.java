package io.moderne.wrapper.cli;

import java.nio.file.Path;
import java.nio.file.Paths;

public interface ModConfiguration {
	static final String MODW_PATH = ".modw";
	static final String REPO_PATH = "repo";

	static final String PROPERTIES_FILENAME = "modw.properties";

	static final String REPOSITORY_ID_KEY = "repository.id";
	static final String REPOSITORY_ID_DEFAULT = "central";

	static final String REPOSITORY_TYPE_KEY = "repository.key";
	static final String REPOSITORY_TYPE_DEFAULT = "default";

	static final String REPOSITORY_URL_KEY = "repository.url";
	static final String REPOSITORY_URL_DEFAULT = "https://repo1.maven.org/maven2/";

	static final String CLI_GROUPID_KEY = "cli.groupId";
	static final String CLI_GROUPID_DEFAULT = "io.moderne";

	static final String CLI_ARTIFACTID_KEY = "cli.artifactId";
	static final String CLI_ARTIFACTID_DEFAULT = "moderne-cli";

	static final String CLI_VERSION_KEY = "cli.version";
	static final String CLI_VERSION_DEFAULT = "RELEASE";

	default Path modwPath() {
		return Paths.get(System.getProperty("user.home"), MODW_PATH);
	}

	default Path propertiesFile() {
		return Paths.get(modwPath().toString(), PROPERTIES_FILENAME);
	}

	default Path repoPath() {
		return Paths.get(modwPath().toString(), REPO_PATH);
	}

	default String javaHome() {
		return System.getProperty("java.home");
	}
	
	public ModConfiguration generate();

	public String getCliGroupId();

	public String getCliArtifactId();

	public String getCliVersion();

	public String getRepositoryId();

	public String getRepositoryType();

	public String getRepositoryUrl();

}
