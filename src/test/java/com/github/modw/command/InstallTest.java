package com.github.modw.command;

import static com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemOut;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.github.modw.CommandFixture;
import com.github.modw.CommandHarness;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class InstallTest implements CommandFixture {

  @Test
  void call(@TempDir Path localRepository, @TempDir Path jarPath) throws Exception {
    final CommandHarness harness = harness(localRepository);
    final Path jarFile = jarPath.resolve("dummy.jar");
    Files.write(jarFile, List.of(""));

    final Install command =
        new Install(harness.properties(), harness.remoteRepository(), harness.localRepository());
    command.version = "0.0.1";
    command.file = jarFile.toFile();
    final String out = tapSystemOut(command::call);
    assertThat(out).contains("0.0.1");
  }
}
