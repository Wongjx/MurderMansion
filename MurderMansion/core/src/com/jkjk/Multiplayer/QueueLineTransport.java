package com.jkjk.Multiplayer;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class QueueLineTransport implements GameLineTransport {
	private static final String CLOSED_SENTINEL = "\u0000__MM_CLOSED__";

	private final BlockingQueue<String> incoming;
	private final LineSender outgoing;
	private final AtomicBoolean closed;
	private final AtomicBoolean peerClosed;

	public QueueLineTransport(LineSender outgoing) {
		this(new LinkedBlockingQueue<String>(), outgoing, new AtomicBoolean(false), new AtomicBoolean(false));
	}

	public QueueLineTransport(BlockingQueue<String> incoming, LineSender outgoing, AtomicBoolean closed,
			AtomicBoolean peerClosed) {
		this.incoming = incoming;
		this.outgoing = outgoing;
		this.closed = closed;
		this.peerClosed = peerClosed;
	}

	public void offerIncomingLine(String line) {
		if (!closed.get()) {
			incoming.offer(line);
		}
	}

	public void signalPeerClosed() {
		peerClosed.set(true);
		incoming.offer(CLOSED_SENTINEL);
	}

	@Override
	public String readLine() throws IOException {
		try {
			String line = incoming.take();
			return unwrapLine(line);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new IOException("Interrupted while reading transport.", e);
		}
	}

	@Override
	public String readLine(long timeoutMillis) throws IOException, SocketTimeoutException {
		if (timeoutMillis <= 0L) {
			return readLine();
		}
		try {
			String line = incoming.poll(timeoutMillis, TimeUnit.MILLISECONDS);
			if (line == null) {
				throw new SocketTimeoutException("Transport read timed out.");
			}
			return unwrapLine(line);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new IOException("Interrupted while reading transport.", e);
		}
	}

	private String unwrapLine(String line) throws SocketException {
		if (CLOSED_SENTINEL.equals(line)) {
			throw new SocketException("Socket closed");
		}
		return line;
	}

	@Override
	public void sendLine(String line) throws IOException {
		if (closed.get() || peerClosed.get()) {
			throw new SocketException("Socket closed");
		}
		outgoing.send(line);
	}

	@Override
	public void close() {
		if (closed.compareAndSet(false, true)) {
			if (!peerClosed.get()) {
				outgoing.closePeer();
			}
			incoming.offer(CLOSED_SENTINEL);
		}
	}

	@Override
	public boolean isClosed() {
		return closed.get();
	}

	public interface LineSender {
		void send(String line) throws IOException;

		void closePeer();
	}
}
