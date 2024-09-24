package com.github.modw;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.*;

import org.apache.maven.artifact.repository.metadata.Versioning;
import org.apache.maven.artifact.repository.metadata.io.xpp3.MetadataXpp3Reader;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.installation.InstallRequest;
import org.eclipse.aether.installation.InstallationException;
import org.eclipse.aether.metadata.DefaultMetadata;
import org.eclipse.aether.metadata.Metadata;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.resolution.MetadataRequest;
import org.eclipse.aether.resolution.MetadataResult;
import org.eclipse.aether.supplier.RepositorySystemSupplier;

public class MavenRepository {
	private static final String MAVEN_METADATA_XML = "maven-metadata.xml";
	final RepositorySystem repositorySystem;
	final RepositorySystemSession repositorySystemSession;

	public MavenRepository(final Path repositoryPath) {
		repositorySystem = new RepositorySystemSupplier().get();
		repositorySystemSession = MavenRepositorySystemUtils.newSession();

		((DefaultRepositorySystemSession) repositorySystemSession).setLocalRepositoryManager(repositorySystem
				.newLocalRepositoryManager(repositorySystemSession, new LocalRepository(repositoryPath.toString())));
	}

	public Set<String> resolveVersion(final Artifact artifact, final List<RemoteRepository> repositories) {
		final List<MetadataResult> metadataResults = new ArrayList<>();
		final Set<String> versions = new HashSet<>();
		final DefaultMetadata mavenMetaDataXml = new DefaultMetadata(artifact.getGroupId(), artifact.getArtifactId(),
				MAVEN_METADATA_XML, Metadata.Nature.RELEASE);

		repositories.forEach(repository -> {
			metadataResults.addAll(repositorySystem.resolveMetadata(repositorySystemSession,
                    Collections.singletonList(new MetadataRequest(mavenMetaDataXml, repository, ""))));
		});

		metadataResults.forEach(metadataResult -> {
			final Metadata metadata = metadataResult.getMetadata();
			if (metadata.getFile() != null && metadata.getFile().exists()) {
				try (InputStream in = new FileInputStream(metadata.getFile())) {
					final Versioning versioning = new MetadataXpp3Reader().read(in, false).getVersioning();
					versions.addAll(versioning.getVersions());
				} catch (FileNotFoundException e) {
				} catch (IOException e) {
				} catch (XmlPullParserException e) {
				}
			}
		});

		return versions;
	}

	public Set<String> clean(final Artifact artifact, final List<RemoteRepository> repositories) {
		final List<MetadataResult> metadataResults = new ArrayList<>();
		final Set<String> versions = new HashSet<>();
		final DefaultMetadata mavenMetaDataXml = new DefaultMetadata(artifact.getGroupId(), artifact.getArtifactId(),
				MAVEN_METADATA_XML, Metadata.Nature.RELEASE);

		repositories.forEach(repository -> {
			metadataResults.addAll(repositorySystem.resolveMetadata(repositorySystemSession,
                    Collections.singletonList(new MetadataRequest(mavenMetaDataXml, repository, ""))));
		});

		metadataResults.forEach(metadataResult -> {
			final Metadata metadata = metadataResult.getMetadata();

			versions.add(metadata.getVersion());
			metadata.getFile().getParentFile().delete();

		});
		return versions;
	}

	public void installArtifact(final Artifact artifact, final List<RemoteRepository> repositories)
			throws InstallationException {
		repositorySystem.install(repositorySystemSession, new InstallRequest().setArtifacts(Arrays.asList(artifact)));
	}

	public Artifact resolveArtifact(final Artifact artifact, final List<RemoteRepository> repositories)
			throws ArtifactResolutionException {
		final ArtifactResult artifactResult = repositorySystem.resolveArtifact(repositorySystemSession,
				new ArtifactRequest().setArtifact(artifact).setRepositories(repositories));
		return artifactResult.getArtifact();
	}

	public static Artifact getArtifact(final String groupId, final String artifactId, final String version) {
		return new DefaultArtifact(String.format("%s:%s:%s", groupId, artifactId, version));
	}

//	public static RemoteRepository getRepository(final String id, final String type, final String url) {
//		return new RemoteRepository.Builder(id, type, url).build();
//	}
//
//	public static RemoteRepository getCentralMavenRepository() {
//		return new RemoteRepository.Builder("central", "default", "https://repo1.maven.org/maven2/").build();
//	}
}
