package com.github.modw.command;

import static com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemOut;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import com.github.modw.CommandTestFixture;
import org.junit.jupiter.api.Test;

import com.github.modw.Configuration;
import com.github.tomakehurst.wiremock.WireMockServer;

class DownloadTest implements CommandTestFixture {

  Configuration modConfiguration;

  @Test
  void execute() throws Exception {
    final Download command = new Download(modConfiguration, Optional.empty());

    final String out =
        tapSystemOut(
            () -> {
              command.execute();
            });

    System.out.println(out);
    assertThat(modConfiguration.repoPath())
        .isDirectoryRecursivelyContaining("glob:**lombok-1.18.34.jar");
  }

  @Override
  public void offer(final Configuration modConfiguration) {
    this.modConfiguration = modConfiguration;
  }

  @Override
  public void offer(WireMockServer wireMockServer) {
    // NoOp
  }
}
