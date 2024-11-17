# MODW
---

modw is [Moderne CLI](https://docs.moderne.io/user-documentation/moderne-cli) wrapper, similar to Maven wrapper or
Gradle wrapper, to simplify the management of the Moderne CLI versions.
This was developed in my spare time to ease the burden of managing my
local [Moderne CLI](https://docs.moderne.io/user-documentation/moderne-cli) version.

It is still a work in progress ...

## Todo

---
Currently, at the top of my list:

- Proper packaging and release management (with the use of semantic versionning)
- Proper PR management
- Better and more tests
- Launcher script enhancement
- An official release :)

## Features

---

Compatible with JDK 1.8 and up.
Simple Windows and Linux scripts are provided (experimental).

Ability to ...

- auto update to the latest version
- download and switch to any version available
- switch version on the fly
- support for proxy (experimental)
- support for authentication (experimental)
- support for environment variable in configuration (experimental)

## Installation & Configuration

---
Until the project provides a proper packaging ...

- Under [MODW_FOLDER], put the modw-[VERSION]-pg.jar and the appropriate modw launch script.
- Under [MODW_FOLDER], put the appropriate modw launch script (Windows or Linux)
- Add [MODW_FOLDER] to your path
- Execute `modw -generate` or  `modw.cmd -generate` depending on your OS to generate the template configuration file
    - Copy and edit the output using the editor of your choice and save under the folder `.modw` in a file called
      `modw.properties`
    - the template configuration should look like this:
  > `#` Moderne CLI GAV <br>
  cli.groupId=io.moderne <br>
  cli.artifactId=moderne-cli <br>
  cli.version=RELEASE <br>
  `#` Repository configuration <br>
  repository.id=central <br>
  repository.key=default <br>
  repository.url=https://repo1.maven.org/maven2/ <br>
  `#` Repository authentication configuration (Optional) <br>
  repository.username=username <br>
  repository.password=password <br>
  `#` Repository proxy configuration (Optional) <br>
  proxy.type=https <br>
  proxy.host=localhost <br>
  proxy.port=-1 <br>
  `#` Repository proxy user configuration (Optional) <br>
  proxy.username=username <br>
  proxy.password=password <br>
- Once the configuration settled, you can use `modw` as a substitute for the usual [
  `mod` commands](https://docs.moderne.io/user-documentation/moderne-cli/cli-reference)

## Usage

---
`modw -help`
> usage: Command line syntax: <br>
> -available display the available Moderne CLI versions<br>
> -clean delete the local Moderne CLI versions<br>
> -download download Moderne CLI locally<br>
> -f <file>      Moderne CLI file (works with -install)<br>
> -generate generate an example Moderne CLI wrapper configuration<br>
> -help display the Wrapper usages<br>
> -install install Moderne CLI<br>
> -installed display the installed Moderne CLI versions<br>
> -v <version>   Moderne CLI version (works with -download and run)<br>
> -version Wrapper version information<br>

## Build Instructions

---
After checking out the repository, run `mvnw clean package`. The target output should contain:

- modw-[VERSION]-pg.jar &rarr; ProGuard slimmed down JAR with included dependencies
- modw-[VERSION]-jar-with-dependencies.jar &rarr; Regular JAR with included dependencies
- modw-[VERSION].jar &rarr; Regular JAR
- modw &rarr; Linux launch script
- modw.cmd &rarr; Windows launch script
