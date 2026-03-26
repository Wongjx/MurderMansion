package com.jkjk.MurderMansion.android;

import java.net.URI;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import com.jkjk.Multiplayer.RelaySocketClient;
import com.jkjk.Multiplayer.RelaySocketFactory;
import com.jkjk.Multiplayer.RelaySocketListener;

public class AndroidRelaySocketFactory implements RelaySocketFactory {
	@Override
	public RelaySocketClient create() {
		return new AndroidRelaySocketClient();
	}

	private static class AndroidRelaySocketClient implements RelaySocketClient {
		private WebSocketClient client;

		@Override
		public void connect(String url, final RelaySocketListener listener) throws Exception {
			client = new WebSocketClient(URI.create(url)) {
				@Override
				public void onOpen(ServerHandshake handshakedata) {
					listener.onOpen();
				}

				@Override
				public void onMessage(String message) {
					listener.onMessage(message);
				}

				@Override
				public void onClose(int code, String reason, boolean remote) {
					listener.onClose(reason);
				}

				@Override
				public void onError(Exception ex) {
					listener.onError(ex);
				}
			};
			client.connectBlocking();
		}

		@Override
		public void send(String text) {
			client.send(text);
		}

		@Override
		public void close() {
			if (client != null) {
				client.close();
			}
		}
	}
}
