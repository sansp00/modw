package com.github.modw.command;

import static com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemOut;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.github.modw.CommandFixture;
import com.github.modw.CommandHarness;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class InstalledTest implements CommandFixture {

  @Test
  void call(@TempDir Path localRepository) throws Exception {
    final CommandHarness harness = harness(localRepository);

    final Installed command =
        new Installed(harness.properties(), harness.remoteRepository(), harness.localRepository());
    command.filter = "NONE";
    final String out = tapSystemOut(command::call);
    assertThat(out).contains("∅");
  }
}
