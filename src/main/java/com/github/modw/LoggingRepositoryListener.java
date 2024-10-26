package com.github.modw;

import org.eclipse.aether.RepositoryEvent;
import org.eclipse.aether.RepositoryListener;

public class LoggingRepositoryListener implements RepositoryListener {

	@Override
	public void artifactDescriptorInvalid(RepositoryEvent event) {
	}

	@Override
	public void artifactDescriptorMissing(RepositoryEvent event) {
	}

	@Override
	public void metadataInvalid(RepositoryEvent event) {
	}

	@Override
	public void artifactResolving(RepositoryEvent event) {
		System.out.printf("---> resolving artifact %s%n", event.getArtifact());
	}

	@Override
	public void artifactResolved(RepositoryEvent event) {
		System.out.printf("---> resolved artifact %s%n", event.getArtifact());
	}

	@Override
	public void metadataResolving(RepositoryEvent event) {
	}

	@Override
	public void metadataResolved(RepositoryEvent event) {
	}

	@Override
	public void artifactDownloading(RepositoryEvent event) {
		System.out.printf("---> downloading artifact %s%n", event.getArtifact());
	}

	@Override
	public void artifactDownloaded(RepositoryEvent event) {
		System.out.printf("---> downloaded artifact %s%n", event.getArtifact());
	}

	@Override
	public void metadataDownloading(RepositoryEvent event) {
	}

	@Override
	public void metadataDownloaded(RepositoryEvent event) {
	}

	@Override
	public void artifactInstalling(RepositoryEvent event) {
		System.out.printf("---> installing artifact %s%n", event.getArtifact());
	}

	@Override
	public void artifactInstalled(RepositoryEvent event) {
		System.out.printf("---> installed artifact %s%n", event.getArtifact());
	}

	@Override
	public void metadataInstalling(RepositoryEvent event) {
	}

	@Override
	public void metadataInstalled(RepositoryEvent event) {
	}

	@Override
	public void artifactDeploying(RepositoryEvent event) {
	}

	@Override
	public void artifactDeployed(RepositoryEvent event) {
	}

	@Override
	public void metadataDeploying(RepositoryEvent event) {
	}

	@Override
	public void metadataDeployed(RepositoryEvent event) {
	}

}
