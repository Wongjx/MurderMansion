package com.jkjk.Multiplayer;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

public class RelayClientBootstrap {
	private RelayRoomSession session;
	private final QueueLineTransport transport;
	private final AtomicBoolean bootstrapReady;
	private final AtomicBoolean sawClientNames;

	public RelayClientBootstrap() {
		this.transport = new QueueLineTransport(new QueueLineTransport.LineSender() {
			@Override
			public void send(String line) throws IOException {
				try {
					session.sendToHost(line);
				} catch (Exception e) {
					throw new IOException("Failed sending relay payload.", e);
				}
			}

			@Override
			public void closePeer() {
				session.close();
			}
		});
		this.bootstrapReady = new AtomicBoolean(false);
		this.sawClientNames = new AtomicBoolean(false);
	}

	public void attachSession(RelayRoomSession session) {
		this.session = session;
	}

	public RelayRoomSession.Listener createListener() {
		return new RelayRoomSession.Listener() {
			@Override
			public void onOpen() {
			}

			@Override
			public void onConnected(boolean isHost) {
			}

			@Override
			public void onPayload(String fromOccupantId, String payload) {
				transport.offerIncomingLine(payload);
				if ("clientNames".equals(payload)) {
					sawClientNames.set(true);
				} else if (sawClientNames.get() && "end".equals(payload)) {
					bootstrapReady.set(true);
				}
			}

			@Override
			public void onPeerConnected(String occupantId) {
			}

			@Override
			public void onPeerDisconnected(String occupantId) {
			}

			@Override
			public void onHostDisconnected() {
				transport.signalPeerClosed();
			}

			@Override
			public void onRoomClosed() {
				transport.signalPeerClosed();
			}

			@Override
			public void onClose(String reason) {
				transport.signalPeerClosed();
			}

			@Override
			public void onError(Throwable throwable) {
				transport.signalPeerClosed();
			}
		};
	}

	public QueueLineTransport getTransport() {
		return transport;
	}

	public boolean isBootstrapReady() {
		return bootstrapReady.get();
	}

	public void awaitBootstrap() throws IOException {
		while (!bootstrapReady.get()) {
			try {
				transport.readLine(10000);
			} catch (SocketTimeoutException e) {
				// Keep waiting while the room finishes connecting.
			} catch (SocketException e) {
				throw e;
			}
		}
	}
}
