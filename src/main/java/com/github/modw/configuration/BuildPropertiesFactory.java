package com.github.modw.configuration;

import com.github.modw.Constants;
import com.github.modw.ModWrapperException;
import org.github.gestalt.config.Gestalt;
import org.github.gestalt.config.builder.GestaltBuilder;
import org.github.gestalt.config.exceptions.GestaltException;
import org.github.gestalt.config.source.ClassPathConfigSourceBuilder;

public class BuildPropertiesFactory {
  final Gestalt gestalt;

  public BuildPropertiesFactory() {
    try {
      this.gestalt =
          new GestaltBuilder()
              .addSource(
                  ClassPathConfigSourceBuilder.builder()
                      .setResource(Constants.GIT_PROPERTIES_PATH.toString())
                      .build())
              .build();
      gestalt.loadConfigs();
    } catch (GestaltException e) {
      throw new ModWrapperException(e);
    }
  }

  public BuildProperties create() {
    try {
      return gestalt.getConfig("", BuildProperties.class);
    } catch (GestaltException e) {
      throw new ModWrapperException(e);
    }
  }
}
