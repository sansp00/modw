package com.github.modw.github;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

public class GithubRepositoryTest {

  @Test
  void latest() throws IOException {
    GithubRepository repo = new GithubRepository();
    GitHub.Release aaa = repo.latest("OpenFeign", "feign");
    assertThat(aaa).isNotNull();
  }

  @Test
  void releases() throws IOException {
    GithubRepository repo = new GithubRepository();
    List<String> aaa = repo.releases("OpenFeign", "feign");
    assertThat(aaa).isNotEmpty();
  }

//  @Test
//  void titi() throws IOException {
//    ObjectMapper objectMapper = new ObjectMapper();
//    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//    GitHub.Release toto =
//        objectMapper.readValue(
//            new File("src/test/resources/github-release-latest.json"), GitHub.Release.class);
//    assertThat(toto).isNotNull();
//  }
}
