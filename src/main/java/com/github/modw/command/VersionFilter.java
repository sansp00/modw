package com.github.modw.command;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.Getter;
import org.semver4j.Semver;

public enum VersionFilter {
  LATEST_MAJORS(Semver::getMajor),
  LATEST_MINORS(Semver::getMinor),
  LATEST_PATCHES(Semver::getMinor),
  NONE(v -> v.getVersion().hashCode());

  @Getter final Function<Semver, Integer> criteria;

  VersionFilter(final Function<Semver, Integer> criteria) {
    this.criteria = criteria;
  }

  public static List<String> latest(final List<String> versions, final VersionFilter filter) {
    return versions.stream()
        .map(Semver::coerce)
        .collect(Collectors.groupingBy(filter.getCriteria()))
        .values()
        .stream()
        .map(versionsGroupBy -> versionsGroupBy.stream().max(Semver::compareTo).orElse(Semver.ZERO))
        .sorted()
        .map(Semver::toString)
        .toList();
  }


  public static List<String> filter(final List<String> versions, final Predicate<Semver> predicate) {
    return versions.stream()
            .map(Semver::coerce)
            .filter(predicate)
            .sorted()
            .map(Semver::toString)
            .toList();
  }
}
