package com.github.modw;

import org.eclipse.aether.transfer.TransferCancelledException;
import org.eclipse.aether.transfer.TransferEvent;
import org.eclipse.aether.transfer.TransferListener;

public class LoggingTransferListener implements TransferListener {

	@Override
	public void transferInitiated(TransferEvent event) throws TransferCancelledException {
	}

	@Override
	public void transferStarted(TransferEvent event) throws TransferCancelledException {
		System.out.printf("Download from %s for %s%s ...", event.getResource().getRepositoryId(),
				event.getResource().getRepositoryUrl(), event.getResource().getResourceName());
	}

	@Override
	public void transferProgressed(TransferEvent event) throws TransferCancelledException {
		System.out.printf(".");
	}

	@Override
	public void transferCorrupted(TransferEvent event) throws TransferCancelledException {
		System.out.printf(" [CORRUPTED]%n");
	}

	@Override
	public void transferSucceeded(TransferEvent event) {
		System.out.printf(" [SUCCEDED]%n");
	}

	@Override
	public void transferFailed(TransferEvent event) {
		System.out.printf(" [FAILED]%n");
	}

}
