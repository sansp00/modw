package com.github.modw.maven;

import com.github.modw.Spinner;
import org.eclipse.aether.transfer.TransferCancelledException;
import org.eclipse.aether.transfer.TransferEvent;
import org.eclipse.aether.transfer.TransferListener;

public class LoggingTransferListener implements TransferListener {

	final Spinner spinner = new Spinner();

	@Override
	public void transferInitiated(TransferEvent event) throws TransferCancelledException {
	}

	@Override
	public void transferStarted(TransferEvent event) throws TransferCancelledException {
		System.out.printf("Download from %s for %s%s %s", event.getResource().getRepositoryId(),
				event.getResource().getRepositoryUrl(), event.getResource().getResourceName(), spinner.display());
	}

	@Override
	public void transferProgressed(TransferEvent event) throws TransferCancelledException {
		System.out.printf(spinner.refresh());
	}

	@Override
	public void transferCorrupted(TransferEvent event) throws TransferCancelledException {
		System.out.printf("%s [CORRUPTED]%n", spinner.erase());
	}

	@Override
	public void transferSucceeded(TransferEvent event) {
		System.out.printf("%s [SUCCEEDED]%n", spinner.erase());
	}

	@Override
	public void transferFailed(TransferEvent event) {
		System.out.printf("%s [FAILED]%n", spinner.erase());
	}

}
