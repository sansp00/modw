package com.github.modw.console;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import lombok.experimental.UtilityClass;
import org.fusesource.jansi.AnsiConsole;
import picocli.CommandLine;

@UtilityClass
public class Console {

  public static PrintStream stream = null;

  public static void setup() {
    AnsiConsole.systemInstall();
    stream = AnsiConsole.out();
  }

  public static void teardown() {
    AnsiConsole.systemUninstall();
  }

  public static final Function<List<CommandLine.Help.Ansi.Style>, String> styleOn =
      styles -> CommandLine.Help.Ansi.Style.on(styles.toArray(CommandLine.Help.Ansi.Style[]::new));
  public static final Function<List<CommandLine.Help.Ansi.Style>, String> styleOff =
      styles -> CommandLine.Help.Ansi.Style.off(styles.toArray(CommandLine.Help.Ansi.Style[]::new));
  public static final BiFunction<String, List<CommandLine.Help.Ansi.Style>, String> style =
      (text, styles) ->
          "%s%s%s".formatted(styleOn.apply(styles), text, CommandLine.Help.Ansi.Style.reset.on());

  @UtilityClass
  public static class Display {
    public static void logo(final String logo) {
      println(
          style.apply(
              logo,
              List.of(CommandLine.Help.Ansi.Style.bold, CommandLine.Help.Ansi.Style.fg_green)));
    }

    public static void command(final String command) {
      print(style.apply("Executing command", List.of(CommandLine.Help.Ansi.Style.fg_white)));
      print(" ");
      print(
          style.apply(
              command,
              List.of(
                  CommandLine.Help.Ansi.Style.bold,
                  CommandLine.Help.Ansi.Style.underline,
                  CommandLine.Help.Ansi.Style.fg_green)));
      println(" \uD83D\uDE80");
    }

    public static void list(final String header, final List<String> items) {
      print(style.apply("┍ ", List.of(CommandLine.Help.Ansi.Style.fg_green)));
      print(
          style.apply(
              header,
              List.of(CommandLine.Help.Ansi.Style.fg_white, CommandLine.Help.Ansi.Style.bold)));
      print(" ");
      print("\uD83D\uDCDC");
      endline();
      if (items.isEmpty()) {
        println(style.apply("┕ ∅", List.of(CommandLine.Help.Ansi.Style.fg_green)));
      } else {
        for (int i = 0; i < items.size(); i++) {
          final String prefix = (i < items.size() - 1) ? "┝ " : "┕ ";
          println(
              style.apply(prefix + items.get(i), List.of(CommandLine.Help.Ansi.Style.fg_green)));
        }
      }
    }

    public static void result(final String description, final String result) {
      print(style.apply(description, List.of(CommandLine.Help.Ansi.Style.fg_white)));
      print(" ");
      print(style.apply(result, List.of(CommandLine.Help.Ansi.Style.fg_green)));
      endline();
    }

    public static void spinnerBegin(final Spinner spinner) {
      print(spinner.display());
    }

    public static void spinnerProgress(final Spinner spinner) {
      print(spinner.spin());
    }

    public static void spinnerEnd(final Spinner spinner) {
      print(spinner.erase());
    }

    public static void message(final String message) {
      println(style.apply(message, List.of(CommandLine.Help.Ansi.Style.fg_white)));
    }

    public static void success(final String message) {
      print(
          style.apply(
              message,
              List.of(CommandLine.Help.Ansi.Style.fg_green, CommandLine.Help.Ansi.Style.bold)));
      println(" ✅");
    }

    public static void error(final String message) {
      print(
          style.apply(
              message,
              List.of(CommandLine.Help.Ansi.Style.fg_red, CommandLine.Help.Ansi.Style.bold)));
      println(" ❌");
    }

    public static void warning(final String message) {
      print(
          style.apply(
              message,
              List.of(CommandLine.Help.Ansi.Style.fg_yellow, CommandLine.Help.Ansi.Style.bold)));
      println(" ⚠");
    }

    public static void exception(final Exception e) {
      println(style.apply(e.toString(), List.of(CommandLine.Help.Ansi.Style.fg_red)));
    }
  }

  public static void endline() {
    println("");
  }

  public static void println(final String data) {
    out().println(CommandLine.Help.Ansi.AUTO.string(data));
  }

  public static void print(final String data) {
    out().print(CommandLine.Help.Ansi.AUTO.string(data));
  }

  public static void setOut(final PrintStream printStream) {
    stream = printStream;
  }

  public static PrintStream getOut() {
    return stream;
  }

  public static PrintStream out() {
    return Optional.ofNullable(stream).orElse(System.out);
  }

  public static CommandLine.Help.ColorScheme colorScheme() {
    return CommandLine.Help.defaultColorScheme(CommandLine.Help.Ansi.AUTO);
    //    CommandLine.Help.defaultColorScheme();
    //    return new CommandLine.Help.ColorScheme.Builder()
    //        .commands(
    //            CommandLine.Help.Ansi.Style.bold,
    //            CommandLine.Help.Ansi.Style.underline) // combine multiple styles
    //        .options(CommandLine.Help.Ansi.Style.fg_yellow) // yellow foreground color
    //        .parameters(CommandLine.Help.Ansi.Style.fg_yellow)
    //        .optionParams(CommandLine.Help.Ansi.Style.italic)
    //        .errors(CommandLine.Help.Ansi.Style.fg_red, CommandLine.Help.Ansi.Style.bold)
    //        .stackTraces(CommandLine.Help.Ansi.Style.italic)
    //        .applySystemProperties() // optional: allow end users to customize
    //        .build();
  }
}
