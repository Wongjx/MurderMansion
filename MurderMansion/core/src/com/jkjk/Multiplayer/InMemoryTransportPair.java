package com.jkjk.Multiplayer;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public final class InMemoryTransportPair {
	private InMemoryTransportPair() {
	}

	public static EndpointPair create() {
		final LinkedBlockingQueue<String> firstIncoming = new LinkedBlockingQueue<String>();
		final LinkedBlockingQueue<String> secondIncoming = new LinkedBlockingQueue<String>();
		final AtomicBoolean firstClosed = new AtomicBoolean(false);
		final AtomicBoolean secondClosed = new AtomicBoolean(false);

		QueueLineTransport first = new QueueLineTransport(firstIncoming, new QueueLineTransport.LineSender() {
			@Override
			public void send(String line) throws IOException {
				secondIncoming.offer(line);
			}

			@Override
			public void closePeer() {
				secondIncoming.offer("\u0000__MM_CLOSED__");
				secondClosed.set(true);
			}
		}, firstClosed, secondClosed);

		QueueLineTransport second = new QueueLineTransport(secondIncoming, new QueueLineTransport.LineSender() {
			@Override
			public void send(String line) throws IOException {
				firstIncoming.offer(line);
			}

			@Override
			public void closePeer() {
				firstIncoming.offer("\u0000__MM_CLOSED__");
				firstClosed.set(true);
			}
		}, secondClosed, firstClosed);

		return new EndpointPair(first, second);
	}

	public static final class EndpointPair {
		public final QueueLineTransport endpointA;
		public final QueueLineTransport endpointB;

		private EndpointPair(QueueLineTransport endpointA, QueueLineTransport endpointB) {
			this.endpointA = endpointA;
			this.endpointB = endpointB;
		}
	}
}
