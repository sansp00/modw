package com.github.modw;

import org.eclipse.aether.transfer.TransferCancelledException;
import org.eclipse.aether.transfer.TransferEvent;
import org.eclipse.aether.transfer.TransferListener;

public class LoggingTransferListener implements TransferListener {

	@Override
	public void transferInitiated(TransferEvent event) throws TransferCancelledException {
		System.out.printf("---> initiated transfer from %s for %s%s%n", event.getResource().getRepositoryId(),
				event.getResource().getRepositoryUrl(), event.getResource().getRepositoryUrl());
	}

	@Override
	public void transferStarted(TransferEvent event) throws TransferCancelledException {
		System.out.printf("---> started transfer from %s for %s%s%n", event.getResource().getRepositoryId(),
				event.getResource().getRepositoryUrl(), event.getResource().getRepositoryUrl());
	}

	@Override
	public void transferProgressed(TransferEvent event) throws TransferCancelledException {
		System.out.printf("---> transfer progressed for %s%s%n",
				event.getResource().getRepositoryUrl(), event.getResource().getRepositoryUrl());
	}

	@Override
	public void transferCorrupted(TransferEvent event) throws TransferCancelledException {
		System.out.printf("---> transfer corrupted from %s for %s%s%n", event.getResource().getRepositoryId(),
				event.getResource().getRepositoryUrl(), event.getResource().getRepositoryUrl());
	}

	@Override
	public void transferSucceeded(TransferEvent event) {
		System.out.printf("---> transfer succeeded from %s for %s%s%n", event.getResource().getRepositoryId(),
				event.getResource().getRepositoryUrl(), event.getResource().getRepositoryUrl());
	}

	@Override
	public void transferFailed(TransferEvent event) {
		System.out.printf("---> transfer failed from %s for %s%s%n", event.getResource().getRepositoryId(),
				event.getResource().getRepositoryUrl(), event.getResource().getRepositoryUrl());
	}

}
