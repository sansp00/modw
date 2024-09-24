package com.github.modw;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * modw configuration properties 
 */
public interface Configuration {
	static final String MODW_PATH = ".modw";
	static final String REPO_PATH = "repo";

	static final String PROPERTIES_FILENAME = "modw.properties";


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

	public String generate();

	public String getCliGroupId();

	public String getCliArtifactId();

	public String getCliVersion();

	public String getRepositoryId();

	public String getRepositoryType();

	public String getRepositoryUrl();

	public String getRepositoryUsername();

	public String getRepositoryPassword();

	public String getProxyType();

	public String getProxyHost();

	public int getProxyPort();

	public String getProxyUsername();

	public String getProxyPassword();

}
