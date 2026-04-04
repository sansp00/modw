package com.github.modw.maven;

import com.github.modw.ModWrapperException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.Strings;
import org.apache.maven.artifact.repository.metadata.Versioning;
import org.apache.maven.artifact.repository.metadata.io.xpp3.MetadataXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
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
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.resolution.MetadataRequest;
import org.eclipse.aether.resolution.MetadataResult;
import org.eclipse.aether.supplier.RepositorySystemSupplier;

public class MavenRepository {
  private static final String MAVEN_METADATA_XML_FILENAME = "maven-metadata.xml";

  final RepositorySystem repositorySystem;
  final RepositorySystemSession repositorySystemSession;
  final Path repositoryPath;

  public MavenRepository(final Path repositoryPath) {
    this(repositoryPath, false);
  }

  public MavenRepository(final Path repositoryPath, final boolean offline) {
    this.repositoryPath = repositoryPath;
    this.repositorySystem = new RepositorySystemSupplier().get();
    this.repositorySystemSession =
        new RepositorySystemSessionSupplier(repositorySystem, repositoryPath, offline).get();
  }

  /**
   * Create an artifact instance from coordinates
   * @param groupId group identifier
   * @param artifactId artifact identifier
   * @param version artifact identifier
   * @return Artifact representing the coordinates
   */
  public static Artifact artifactFor(
      final String groupId, final String artifactId, final String version) {
    return new DefaultArtifact(String.format("%s:%s:%s", groupId, artifactId, version));
  }

  /**
   * Create an artifact instance from coordinates
   * @param groupId group identifier
   * @param artifactId artifact identifier
   * @param version version
   * @param extension extension
   * @param qualifier qualifier
   * @return Artifact representing the coordinates
   */
  public static Artifact artifactFor(
      final String groupId,
      final String artifactId,
      final String version,
      String extension,
      String qualifier) {
    return new DefaultArtifact(
        String.format("%s:%s:%s:%s:%s", groupId, artifactId, extension, qualifier, version));
  }

  /**
   * Reads the versionning information of the Maven metadata file.
   * @param metadataFile Maven metadata
   * @return Versionning contained in the metadata
   */
  Optional<Versioning> versioning(final File metadataFile) {
    if (Objects.nonNull(metadataFile) && metadataFile.exists()) {
      try (InputStream in = new FileInputStream(metadataFile)) {
        final Versioning versioning = new MetadataXpp3Reader().read(in, false).getVersioning();
        return Optional.of(versioning);
      } catch (IOException | XmlPullParserException e) {
        throw new ModWrapperException(e);
      }
    }
    return Optional.empty();
  }

  /**
   * Checks if a Maven artifact exists in the local repository
   * @param artifact Artifact to check
   * @return boolean indicating if artifact exists or not
   */
  Boolean exists(final Artifact artifact) {
    final String artifactPath =
        repositorySystemSession.getLocalRepositoryManager().getPathForLocalArtifact(artifact);

    final File filePath = Paths.get(repositoryPath.toString(), artifactPath).toFile();
    return filePath.exists();
  }

  /**
   * Deletes a Maven artifact from the local repository
   * @param artifact Artifact to delete
   */
  void delete(final Artifact artifact) {
    final String artifactPath =
        repositorySystemSession.getLocalRepositoryManager().getPathForLocalArtifact(artifact);
    FileUtils.deleteQuietly(Paths.get(artifactPath).getParent().toFile());
  }

  /**
   * Deletes all versions of a Maven artifact
   * @param artifact Artifact for the versions will be deleted
   * @param repositories
   * @return Versions that were deleted
   */
  public Set<String> deleteVersions(
      final Artifact artifact, final List<RemoteRepository> repositories) {
    final Set<String> versions = new HashSet<>();
    final DefaultMetadata mavenMetaDataXml =
        new DefaultMetadata(
            artifact.getGroupId(),
            artifact.getArtifactId(),
            MAVEN_METADATA_XML_FILENAME,
            Metadata.Nature.RELEASE);

    final List<MetadataResult> metadataResults = new ArrayList<>();

    final Function<String, Artifact> versionedArtifact =
        v -> artifactFor(artifact.getGroupId(), artifact.getArtifactId(), v);
    final Consumer<Artifact> deleteArtifact = this::delete;
    final Consumer<Artifact> collect = a -> versions.add(a.getVersion());
    final Predicate<Artifact> artifactExists = this::exists;

    repositories.forEach(
        repository ->
            metadataResults.addAll(
                repositorySystem.resolveMetadata(
                    repositorySystemSession,
                    Collections.singletonList(
                        new MetadataRequest(mavenMetaDataXml, repository, "")))));

    metadataResults.stream()
        .map(MetadataResult::getMetadata)
        .forEach(
            metadata ->
                versioning(metadata.getFile())
                    .ifPresent(
                        versioning ->
                            versioning.getVersions().stream()
                                .map(versionedArtifact)
                                .filter(artifactExists)
                                .filter(
                                    a ->
                                        !Strings.CI.equals(a.getVersion(), versioning.getRelease()))
                                .forEach(deleteArtifact.andThen(collect))));
    final LocalMetadataResult localMetadataResult =
        repositorySystemSession
            .getLocalRepositoryManager()
            .find(repositorySystemSession, new LocalMetadataRequest(mavenMetaDataXml, null, ""));

    versioning(localMetadataResult.getFile())
        .ifPresent(
            versioning ->
                versioning.getVersions().stream()
                    .map(versionedArtifact)
                    .filter(artifactExists)
                    .filter(a -> !Strings.CI.equals(a.getVersion(), versioning.getRelease()))
                    .forEach(deleteArtifact.andThen(collect)));
    return versions;
  }

  /**
   * Lists available versions of a Maven artifact
   * @param artifact Artifact for the versions to fetch
   * @param repositories
   * @return Versions available
   */
  public Set<String> availableVersions(
      final Artifact artifact, final List<RemoteRepository> repositories) {
    final List<MetadataResult> metadataResults = new ArrayList<>();
    final Set<String> versions = new HashSet<>();
    final DefaultMetadata mavenMetaDataXml =
        new DefaultMetadata(
            artifact.getGroupId(),
            artifact.getArtifactId(),
            MAVEN_METADATA_XML_FILENAME,
            Metadata.Nature.RELEASE);
    repositories.forEach(
        repository ->
            metadataResults.addAll(
                repositorySystem.resolveMetadata(
                    repositorySystemSession,
                    Collections.singletonList(
                        new MetadataRequest(mavenMetaDataXml, repository, "")))));

    metadataResults.forEach(
        metadataResult -> {
          final Metadata metadata = metadataResult.getMetadata();
          if (metadata.getFile() != null && metadata.getFile().exists()) {
            try (InputStream in = new FileInputStream(metadata.getFile())) {
              final Versioning versioning =
                  new MetadataXpp3Reader().read(in, false).getVersioning();
              versions.addAll(versioning.getVersions());
            } catch (IOException | XmlPullParserException e) {
              throw new ModWrapperException(e);
            }
          }
        });

    final Predicate<Artifact> resolvable =
        a -> {
          try {
            return Objects.nonNull(resolve(a, repositories));
          } catch (ArtifactResolutionException _) {
            return false;
          }
        };

    //JFrog curation may withhold a release version listed in the metadata, so we try to reolve
    return versions.stream()
        .map(v -> artifactFor(artifact.getGroupId(), artifact.getArtifactId(), v))
        .filter(resolvable)
        .map(Artifact::getVersion)
        .collect(Collectors.toSet());
  }

  /**
   * Lists installed versions of a Maven artifact
   * @param artifact Artifact for the versions to check
   * @param repositories
   * @return Versions installed
   */
  public Set<String> installedVersions(
      final Artifact artifact, final List<RemoteRepository> repositories) {
    final Set<String> versions = new HashSet<>();
    final DefaultMetadata mavenMetaDataXml =
        new DefaultMetadata(
            artifact.getGroupId(),
            artifact.getArtifactId(),
            MAVEN_METADATA_XML_FILENAME,
            Metadata.Nature.RELEASE);
    final List<MetadataResult> metadataResults = new ArrayList<>();

    final Function<String, Artifact> versionedArtifact =
        v -> artifactFor(artifact.getGroupId(), artifact.getArtifactId(), v);
    final Predicate<Artifact> artifactExists = this::exists;

    repositories.forEach(
        repository ->
            metadataResults.addAll(
                repositorySystem.resolveMetadata(
                    repositorySystemSession,
                    Collections.singletonList(
                        new MetadataRequest(mavenMetaDataXml, repository, "")))));

    metadataResults.stream()
        .map(MetadataResult::getMetadata)
        .forEach(
            metadata ->
                versioning(metadata.getFile())
                    .ifPresent(
                        versioning ->
                            versioning.getVersions().stream()
                                .map(versionedArtifact)
                                .filter(artifactExists)
                                .map(Artifact::getVersion)
                                .forEach(versions::add)));
    final LocalMetadataResult localMetadataResult =
        repositorySystemSession
            .getLocalRepositoryManager()
            .find(repositorySystemSession, new LocalMetadataRequest(mavenMetaDataXml, null, ""));

    versioning(localMetadataResult.getFile())
        .ifPresent(
            versioning ->
                versioning.getVersions().stream()
                    .map(versionedArtifact)
                    .filter(artifactExists)
                    .map(Artifact::getVersion)
                    .forEach(versions::add));

    return versions;
  }

  /**
   * Install a Maven artifact to local repository
   * @param artifact Artifact to install
   * @throws InstallationException
   */
  public void install(final Artifact artifact) throws InstallationException {
    repositorySystem.install(
        repositorySystemSession,
        new InstallRequest().setArtifacts(Collections.singletonList(artifact)));
  }

  /**
   * Resolves a Maven artifact with repositories
   * @param artifact Artifact to resolve
   * @param repositories
   * @return Resolved artifact
   * @throws ArtifactResolutionException
   */
  public Artifact resolve(final Artifact artifact, final List<RemoteRepository> repositories)
      throws ArtifactResolutionException {
    final ArtifactResult artifactResult =
        repositorySystem.resolveArtifact(
            repositorySystemSession,
            new ArtifactRequest().setArtifact(artifact).setRepositories(repositories));
    return artifactResult.getArtifact();
  }
}
