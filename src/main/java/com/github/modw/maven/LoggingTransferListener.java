package com.github.modw.maven;

import com.github.modw.console.Console;
import com.github.modw.console.Spinner;
import org.eclipse.aether.transfer.TransferCancelledException;
import org.eclipse.aether.transfer.TransferEvent;
import org.eclipse.aether.transfer.TransferListener;

public class LoggingTransferListener implements TransferListener {

  final Spinner spinner = new Spinner();

  @Override
  public void transferInitiated(TransferEvent event) throws TransferCancelledException {
    final String action =
        "Transfer initiated from %s [%s%s]"
            .formatted(
                event.getResource().getRepositoryId(),
                event.getResource().getRepositoryUrl(),
                event.getResource().getResourceName());

    Console.Display.message(action);
  }

  @Override
  public void transferStarted(TransferEvent event) throws TransferCancelledException {
    final String action =
        "Transfer started from %s [%s%s]"
            .formatted(
                event.getResource().getRepositoryId(),
                event.getResource().getRepositoryUrl(),
                event.getResource().getResourceName());

    Console.Display.message(action);
    Console.Display.spinnerBegin(spinner);

  }

  @Override
  public void transferProgressed(TransferEvent event) throws TransferCancelledException {
    Console.Display.spinnerProgress(spinner);
  }

  @Override
  public void transferCorrupted(TransferEvent event) throws TransferCancelledException {
    Console.Display.spinnerEnd(spinner);
    Console.endline();
    Console.Display.warning("Transfer corrupted");
  }

  @Override
  public void transferSucceeded(TransferEvent event) {
    Console.Display.spinnerEnd(spinner);
    Console.endline();
    Console.Display.success("Transfer succeeded");
  }

  @Override
  public void transferFailed(TransferEvent event) {
    Console.Display.spinnerEnd(spinner);
    Console.endline();
    Console.Display.error("Transfer failed");
  }
}
