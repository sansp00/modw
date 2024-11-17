package com.github.modw.github;

import feign.Feign;
import feign.Response;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;

public class GithubRepository {

  final GitHub github;

  public GithubRepository() {
    this.github =
        Feign.builder()
            .encoder(new JacksonEncoder())
            .decoder(new JacksonDecoder())
            .target(GitHub.class, "https://api.github.com");
  }

  public GitHub.Release latest(final String owner, final String repo) throws IOException {

    //GitHub.Release latest = github.latest("sansp00", "modw");
    GitHub.Release latest = github.latest(owner, repo);
    return latest;
  }

  public List<String> releases(final String owner, final String repo) throws IOException {

    List<GitHub.Release> releases = github.releases(owner, repo);
    return releases.stream().map(GitHub.Release::getName).collect(Collectors.toList());
  }

  public void download(final String owner, final String repo, final String name, final String asset, final Path destination)
      throws IOException {

    try (final Response response = github.download(owner, repo, name, asset)) {
      if (response.status() == 200) {
        FileUtils.copyInputStreamToFile(
            response.body().asInputStream(), Paths.get(destination.toString(), asset).toFile());
      }
    }

    // final Response.Body body = response.body();
    // final InputStream inputStream = body.asInputStream();

    //
    //    FileAPI fileAPI = Feign.builder()
    //            .logger(new Slf4jLogger())
    //            .logLevel(Logger.Level.FULL)
    //            .target(FileAPI.class, "http://localhost:8080");
    //    String fileName = "test.jpg";
    //    Map<String, Object> queryMap = new HashMap<>();
    //    queryMap.put("fileName", fileName);
    //    Response response = fileAPI.download(queryMap);
    //    if (response.status() == 200) {
    //      File downloadFile = new File("D:\\Downloads\\", fileName);
    //      FileUtils.copyInputStreamToFile(response.body().asInputStream(), downloadFile);
    //    }
  }
}
