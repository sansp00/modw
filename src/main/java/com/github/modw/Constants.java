package com.github.modw;

import lombok.experimental.UtilityClass;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

@UtilityClass
public class Constants {
    static final String MODW_LOGO = """
   _____  _____  ____  
  |     ||     ||    \\
  | | | ||  |  ||  |  |
  |_|_|_||_____||____/ 
  | | | |              
  | | | |              
  |_____|              
""";

    public static final String JAVA_COMMAND = "/bin/java";
    public static final Path JAVA_HOME = Paths.get(System.getProperty("java.home"));
    public static final Path GIT_PROPERTIES_PATH =
            Paths.get( "git.properties");
    public static final Path MODW_PATH = Paths.get(System.getProperty("user.home"), ".modw");
    public static final Path MODW_REPO_PATH =
            Paths.get(System.getProperty("user.home"), ".modw", "repo");
    public static final Path MODW_PROPERTIES_PATH =
            Paths.get(System.getProperty("user.home"), ".modw", "modw.properties");

    public static final Pattern MODW_JAR_NAME_PATTERN =
            Pattern.compile("modw-\\d+\\.\\d+\\.\\d+-pg.jar");
    public static final Pattern MODW_ASSET_NAME_PATTERN =
            Pattern.compile("modw-\\d+\\.\\d+\\.\\d+-distribution.zip");
    public static final IOFileFilter MODW_ASSET_DIR_FILTER =
            new WildcardFileFilter.Builder().setWildcards("modw-*-distribution").get();
    public static final IOFileFilter MODW_ASSET_NAME_FILTER =
            new WildcardFileFilter.Builder().setWildcards("modw-*-distribution.zip").get();
    public static final IOFileFilter MODW_JAR_NAME_FILTER =
            new WildcardFileFilter.Builder().setWildcards("modw-*-pg.jar").get();
}
