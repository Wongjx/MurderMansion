package com.jkjk.Multiplayer;

import java.util.concurrent.atomic.AtomicBoolean;

import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.jkjk.MMHelpers.MMLog;

public class RelayRoomSession {
	private final String roomId;
	private final String occupantId;
	private final boolean host;
	private final String relayUrl;
	private final RelaySocketClient socketClient;
	private final Listener listener;
	private final AtomicBoolean closed;
	private final JsonReader jsonReader;

	public RelayRoomSession(String roomId, String occupantId, boolean host, String relayUrl,
			RelaySocketFactory socketFactory, Listener listener) {
		this.roomId = roomId;
		this.occupantId = occupantId;
		this.host = host;
		this.relayUrl = relayUrl;
		this.socketClient = socketFactory.create();
		this.listener = listener;
		this.closed = new AtomicBoolean(false);
		this.jsonReader = new JsonReader();
	}

	public void connect() throws Exception {
		socketClient.connect(relayUrl, new RelaySocketListener() {
			@Override
			public void onOpen() {
				listener.onOpen();
			}

			@Override
			public void onMessage(String text) {
				handleMessage(text);
			}

			@Override
			public void onClose(String reason) {
				if (closed.compareAndSet(false, true)) {
					listener.onClose(reason);
				}
			}

			@Override
			public void onError(Throwable throwable) {
				listener.onError(throwable);
			}
		});
	}

	private void handleMessage(String text) {
		JsonValue root = jsonReader.parse(text);
		String type = root.getString("type", "");
		if ("payload".equals(type)) {
			listener.onPayload(root.getString("fromOccupantId", null), root.getString("payload", ""));
		} else if ("peer_connected".equals(type)) {
			listener.onPeerConnected(root.getString("occupantId", null));
		} else if ("peer_disconnected".equals(type)) {
			listener.onPeerDisconnected(root.getString("occupantId", null));
		} else if ("host_disconnected".equals(type)) {
			listener.onHostDisconnected();
		} else if ("room_closed".equals(type)) {
			listener.onRoomClosed();
		} else if ("connected".equals(type)) {
			listener.onConnected(root.getBoolean("isHost", false));
		} else if ("error".equals(type)) {
			listener.onError(new IllegalStateException(root.getString("message", "Relay error")));
		} else {
			MMLog.log("MM-RELAY", "Unknown relay message type: " + type + " body=" + text);
		}
	}

	public void sendToHost(String payload) throws Exception {
		sendEnvelope("{"
				+ "\"type\":\"payload\","
				+ "\"roomId\":\"" + escape(roomId) + "\","
				+ "\"occupantId\":\"" + escape(occupantId) + "\","
				+ "\"payload\":\"" + escape(payload) + "\""
				+ "}");
	}

	public void sendToTarget(String targetOccupantId, String payload) throws Exception {
		sendEnvelope("{"
				+ "\"type\":\"payload\","
				+ "\"roomId\":\"" + escape(roomId) + "\","
				+ "\"occupantId\":\"" + escape(occupantId) + "\","
				+ "\"targetOccupantId\":\"" + escape(targetOccupantId) + "\","
				+ "\"payload\":\"" + escape(payload) + "\""
				+ "}");
	}

	public void broadcast(String payload) throws Exception {
		sendEnvelope("{"
				+ "\"type\":\"payload\","
				+ "\"roomId\":\"" + escape(roomId) + "\","
				+ "\"occupantId\":\"" + escape(occupantId) + "\","
				+ "\"broadcast\":true,"
				+ "\"payload\":\"" + escape(payload) + "\""
				+ "}");
	}

	public void sendPing() throws Exception {
		sendEnvelope("{"
				+ "\"type\":\"ping\","
				+ "\"roomId\":\"" + escape(roomId) + "\","
				+ "\"occupantId\":\"" + escape(occupantId) + "\""
				+ "}");
	}

	private void sendEnvelope(String json) throws Exception {
		if (closed.get()) {
			throw new IllegalStateException("Relay session already closed.");
		}
		socketClient.send(json);
	}

	public void close() {
		if (closed.compareAndSet(false, true)) {
			socketClient.close();
		}
	}

	public boolean isHost() {
		return host;
	}

	public String getOccupantId() {
		return occupantId;
	}

	private static String escape(String value) {
		if (value == null) {
			return "";
		}
		return value.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
	}

	public interface Listener {
		void onOpen();

		void onConnected(boolean isHost);

		void onPayload(String fromOccupantId, String payload);

		void onPeerConnected(String occupantId);

		void onPeerDisconnected(String occupantId);

		void onHostDisconnected();

		void onRoomClosed();

		void onClose(String reason);

		void onError(Throwable throwable);
	}
}
