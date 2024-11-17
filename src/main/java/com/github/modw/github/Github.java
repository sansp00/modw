package com.github.modw.github;

import feign.Param;
import feign.RequestLine;
import feign.Response;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

interface GitHub {
  @RequestLine("GET /repos/{owner}/{repo}/releases/latest")
  Release latest(@Param("owner") String owner, @Param("repo") String repo);

  @RequestLine("GET /repos/{owner}/{repo}/releases")
  List<Release> releases(@Param("owner") String owner, @Param("repo") String repo);

  // https://github.com/octocat/Hello-World/releases/download/v1.0.0/example.zip
  @RequestLine("GET /repos/{owner}/{repo}/releases/{name}/{asset}")
  Response download(
      @Param("owner") String owner,
      @Param("repo") String repo,
      @Param("name") String name,
      @Param("asset") String asset);

  @Data
  public static class Release {
    String tag_name;
    String name;
    String created_at;
    String published_at;
    List<Asset> assets = new ArrayList<>();
  }

  @Data
  public static class Asset {
    String url;
    String browser_download_url;
    String name;
  }
}
