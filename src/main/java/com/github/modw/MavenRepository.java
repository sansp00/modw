package com.github.modw;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
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
import org.eclipse.aether.repository.LocalMetadataRequest;
import org.eclipse.aether.repository.LocalMetadataResult;
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
	final Path repositoryPath;

	public MavenRepository(final Path repositoryPath) {
		this(repositoryPath, false);
	}

	public MavenRepository(final Path repositoryPath, final boolean offline) {
		this.repositoryPath = repositoryPath;
		this.repositorySystem = new RepositorySystemSupplier().get();
		this.repositorySystemSession = repositorySystemSessionSupplier(repositorySystem, repositoryPath, offline).get();
	}

	Supplier<RepositorySystemSession> repositorySystemSessionSupplier(final RepositorySystem repositorySystem,
			final Path repositoryPath, final boolean offline) {
		return () -> {
			final DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();
			session.setLocalRepositoryManager(repositorySystem.newLocalRepositoryManager(session,
					new LocalRepository(repositoryPath.toString())));
			session.setOffline(offline);
			session.setTransferListener(new LoggingTransferListener());
			return (RepositorySystemSession) session;
		};
	}

	/**
	 * @param artifact
	 * @param repositories
	 * @return
	 */
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

	Optional<Versioning> versioning(final File metadataFile) {
		if (Objects.nonNull(metadataFile) && metadataFile.exists()) {
			try (InputStream in = new FileInputStream(metadataFile)) {
				final Versioning versioning = new MetadataXpp3Reader().read(in, false).getVersioning();
				return Optional.of(versioning);
			} catch (FileNotFoundException e) {
			} catch (IOException e) {
			} catch (XmlPullParserException e) {
			}
		}
		return Optional.empty();
	}

	Boolean artifactExists(final Artifact artifact) {
		final String artifactPath = repositorySystemSession.getLocalRepositoryManager()
				.getPathForLocalArtifact(artifact);
		final File filePath = Path.of(repositoryPath.toFile().getAbsolutePath(), artifactPath).toFile();
		return filePath.exists();
	}

	/**
	 * @param artifact
	 * @param repositories
	 * @return
	 */
	public Set<String> clean(final Artifact artifact, final List<RemoteRepository> repositories) {
		final Set<String> versions = new HashSet<>();
		final DefaultMetadata mavenMetaDataXml = new DefaultMetadata(artifact.getGroupId(), artifact.getArtifactId(),
				MAVEN_METADATA_XML, Metadata.Nature.RELEASE);

		final List<MetadataResult> metadataResults = new ArrayList<>();

		final Function<String, Artifact> versionnedArtifact = v -> {
			return getArtifact(artifact.getGroupId(), artifact.getArtifactId(), v);
		};

		final Consumer<Artifact> deleteArtifact = a -> {
			final String artifactPath = repositorySystemSession.getLocalRepositoryManager().getPathForLocalArtifact(a);
			FileUtils.deleteQuietly(Path.of(artifactPath).getParent().toFile());
		};

		final Consumer<Artifact> collect = a -> {
			versions.add(a.getVersion());
		};

		final Predicate<Artifact> artifactExists = a -> artifactExists(a);

		repositories.forEach(repository -> {
			metadataResults.addAll(repositorySystem.resolveMetadata(repositorySystemSession,
					Collections.singletonList(new MetadataRequest(mavenMetaDataXml, repository, ""))));
		});

		metadataResults.stream().map(MetadataResult::getMetadata).forEach(metadata -> {
			versioning(metadata.getFile()).ifPresent(versioning -> {
				versioning.getVersions().stream().map(versionnedArtifact).filter(artifactExists)
						.filter(a -> !StringUtils.equals(a.getVersion(), versioning.getRelease()))
						.forEach(deleteArtifact.andThen(collect));

			});

		});
		final LocalMetadataResult localMetadataResult = repositorySystemSession.getLocalRepositoryManager()
				.find(repositorySystemSession, new LocalMetadataRequest(mavenMetaDataXml, null, ""));

		versioning(localMetadataResult.getFile()).ifPresent(versioning -> {
			versioning.getVersions().stream().map(versionnedArtifact).filter(artifactExists)
					.filter(a -> !StringUtils.equals(a.getVersion(), versioning.getRelease()))
					.forEach(deleteArtifact.andThen(collect));

		});
		return versions;
	}

	/**
	 * @param artifact
	 * @param repositories
	 * @return
	 */
	public Set<String> installed(final Artifact artifact, final List<RemoteRepository> repositories) {
		final Set<String> versions = new HashSet<>();
		final DefaultMetadata mavenMetaDataXml = new DefaultMetadata(artifact.getGroupId(), artifact.getArtifactId(),
				MAVEN_METADATA_XML, Metadata.Nature.RELEASE);
		final List<MetadataResult> metadataResults = new ArrayList<>();

		final Function<String, Artifact> versionnedArtifact = v -> {
			return getArtifact(artifact.getGroupId(), artifact.getArtifactId(), v);
		};

		final Consumer<Artifact> collect = a -> {
			versions.add(a.getVersion());
		};

		final Predicate<Artifact> artifactExists = a -> artifactExists(a);

		repositories.forEach(repository -> {
			metadataResults.addAll(repositorySystem.resolveMetadata(repositorySystemSession,
					Collections.singletonList(new MetadataRequest(mavenMetaDataXml, repository, ""))));
		});

		metadataResults.stream().map(MetadataResult::getMetadata).forEach(metadata -> {
			versioning(metadata.getFile()).ifPresent(versioning -> {
				versioning.getVersions().stream().map(versionnedArtifact).filter(artifactExists).forEach(collect);

			});

		});
		final LocalMetadataResult localMetadataResult = repositorySystemSession.getLocalRepositoryManager()
				.find(repositorySystemSession, new LocalMetadataRequest(mavenMetaDataXml, null, ""));

		versioning(localMetadataResult.getFile()).ifPresent(versioning -> {
			versioning.getVersions().stream().map(versionnedArtifact).filter(artifactExists).forEach(collect);

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

}
