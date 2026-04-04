package com.github.modw.console;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Spinner {

  final List<String> characters;
  final AtomicInteger index;

  public Spinner() {
    this.index = new AtomicInteger(0);
    //this.characters =  Arrays.asList("-", "\\", "|", "/");
    this.characters =  Arrays.asList("⠋", "⠙", "⠹", "⠸", "⠼", "⠴", "⠦", "⠧", "⠇", "⠏");
  }

  public String display() {
    return " ";
  }

  public String spin() {
    return String.format("\b%s", characters.get(index.getAndIncrement() % characters.size()));
  }

  public String erase() {
    return "\b";
  }
}
