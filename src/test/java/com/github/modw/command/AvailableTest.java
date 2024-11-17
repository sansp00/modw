package com.github.modw.command;

import static com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemOut;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.modw.CommandTestFixture;
import org.junit.jupiter.api.Test;

import com.github.modw.Configuration;
import com.github.tomakehurst.wiremock.WireMockServer;

class AvailableTest implements CommandTestFixture {

  Configuration modConfiguration;

  @Test
  void execute() throws Exception {
    final Available command = new Available(modConfiguration);
    final String out =
        tapSystemOut(
            () -> {
              command.execute();
            });

    command.execute();
    System.out.println(out);
    assertThat(out).contains("1.18.34");
  }

  @Override
  public void offer(Configuration modConfiguration) {
    this.modConfiguration = modConfiguration;
  }

  @Override
  public void offer(WireMockServer wireMockServer) {
    // TODO Auto-generated method stub

  }
}
