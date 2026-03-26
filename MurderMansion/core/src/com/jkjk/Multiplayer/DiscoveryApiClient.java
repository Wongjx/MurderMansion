package com.jkjk.Multiplayer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

public class DiscoveryApiClient {
	public static final int DEFAULT_MAX_PLAYERS = 6;
	public static final int DEFAULT_PORT = 28765;
	public static final int PROTOCOL_VERSION = 1;
	public static final String APP_VERSION = "1.0.1";
	private static final int CONNECT_TIMEOUT_MS = 5000;
	private static final int READ_TIMEOUT_MS = 5000;

	// Fill this in after deploy if not overridden by a preference.
	public static final String DEFAULT_BASE_URL = "https://murder-mansion-discovery.jxlee92.workers.dev";

	private final JsonReader jsonReader;
	private final String baseUrl;

	public DiscoveryApiClient(String baseUrl) {
		this.baseUrl = stripTrailingSlash(baseUrl);
		this.jsonReader = new JsonReader();
	}

	public RoomActionResult createRoom(RoomVisibility visibility, String displayName) throws IOException {
		String payload = "{"
				+ "\"visibility\":\"" + visibility.name().toLowerCase() + "\","
				+ "\"displayName\":\"" + escape(displayName) + "\","
				+ "\"maxPlayers\":" + DEFAULT_MAX_PLAYERS + ","
				+ "\"protocolVersion\":" + PROTOCOL_VERSION + ","
				+ "\"appVersion\":\"" + APP_VERSION + "\""
				+ "}";
		return parseRoomAction(send("POST", "/rooms", payload));
	}

	public RoomActionResult quickStart(String displayName) throws IOException {
		String payload = "{"
				+ "\"displayName\":\"" + escape(displayName) + "\","
				+ "\"maxPlayers\":" + DEFAULT_MAX_PLAYERS + ","
				+ "\"protocolVersion\":" + PROTOCOL_VERSION + ","
				+ "\"appVersion\":\"" + APP_VERSION + "\""
				+ "}";
		return parseRoomAction(send("POST", "/rooms/quick-start", payload));
	}

	public RoomActionResult fetchRoomByCode(String roomCode) throws IOException {
		return parseRoomAction(send("GET", "/rooms/code/" + roomCode.trim().toUpperCase(), null));
	}

	public RoomActionResult fetchRoom(String roomId) throws IOException {
		return parseRoomAction(send("GET", "/rooms/" + roomId, null));
	}

	public RoomActionResult pollRoom(String roomId, String occupantId) throws IOException {
		String payload = "{"
				+ "\"occupantId\":\"" + escape(occupantId) + "\""
				+ "}";
		return parseRoomAction(send("POST", "/rooms/" + roomId + "/poll", payload));
	}

	public RelayConnectInfo fetchConnectInfo(String roomId, String occupantId) throws IOException {
		String payload = "{"
				+ "\"occupantId\":\"" + escape(occupantId) + "\""
				+ "}";
		JsonValue root = jsonReader.parse(send("POST", "/rooms/" + roomId + "/connect-info", payload));
		RelayConnectInfo info = new RelayConnectInfo();
		info.ok = root.getBoolean("ok", false);
		info.error = root.getString("error", null);
		info.message = root.getString("message", null);
		info.relayUrl = root.getString("relayUrl", null);
		info.hostOccupantId = root.getString("hostOccupantId", null);
		String phase = root.getString("phase", null);
		info.phase = phase == null ? null : RoomPhase.valueOf(phase.toUpperCase());
		JsonValue roomJson = root.get("room");
		if (roomJson != null) {
			info.room = parseRoom(roomJson);
		}
		return info;
	}

	public RoomActionResult joinRoom(String roomId, String displayName) throws IOException {
		String payload = "{"
				+ "\"displayName\":\"" + escape(displayName) + "\""
				+ "}";
		return parseRoomAction(send("POST", "/rooms/" + roomId + "/join", payload));
	}

	public RoomActionResult setReady(String roomId, String occupantId, boolean ready) throws IOException {
		String payload = "{"
				+ "\"occupantId\":\"" + escape(occupantId) + "\","
				+ "\"ready\":" + ready
				+ "}";
		return parseRoomAction(send("POST", "/rooms/" + roomId + "/ready", payload));
	}

	public RoomActionResult startRoom(String roomId, String occupantId, String localAddress, int port)
			throws IOException {
		String payload = "{"
				+ "\"occupantId\":\"" + escape(occupantId) + "\","
				+ "\"localAddress\":\"" + escape(localAddress) + "\","
				+ "\"port\":" + port
				+ "}";
		return parseRoomAction(send("POST", "/rooms/" + roomId + "/start", payload));
	}

	public RoomActionResult heartbeat(String roomId, String occupantId, String localAddress, int port)
			throws IOException {
		String payload = "{"
				+ "\"occupantId\":\"" + escape(occupantId) + "\","
				+ "\"localAddress\":\"" + escape(localAddress) + "\","
				+ "\"port\":" + port
				+ "}";
		return parseRoomAction(send("POST", "/rooms/" + roomId + "/heartbeat", payload));
	}

	public RoomActionResult finishRoom(String roomId, String occupantId) throws IOException {
		String payload = "{"
				+ "\"occupantId\":\"" + escape(occupantId) + "\""
				+ "}";
		return parseRoomAction(send("POST", "/rooms/" + roomId + "/finish", payload));
	}

	public RoomActionResult leaveRoom(String roomId, String occupantId) throws IOException {
		String payload = "{"
				+ "\"occupantId\":\"" + escape(occupantId) + "\""
				+ "}";
		return parseRoomAction(send("POST", "/rooms/" + roomId + "/leave", payload));
	}

	public RoomActionResult closeRoom(String roomId, String occupantId) throws IOException {
		String payload = "{"
				+ "\"occupantId\":\"" + escape(occupantId) + "\""
				+ "}";
		return parseRoomAction(send("POST", "/rooms/" + roomId + "/close", payload));
	}

	public RoomActionResult kick(String roomId, String occupantId, String targetOccupantId) throws IOException {
		String payload = "{"
				+ "\"occupantId\":\"" + escape(occupantId) + "\","
				+ "\"targetOccupantId\":\"" + escape(targetOccupantId) + "\""
				+ "}";
		return parseRoomAction(send("POST", "/rooms/" + roomId + "/kick", payload));
	}

	private String send(String method, String path, String requestBody) throws IOException {
		HttpURLConnection connection = null;
		try {
			URL url = new URL(baseUrl + path);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod(method);
			connection.setConnectTimeout(CONNECT_TIMEOUT_MS);
			connection.setReadTimeout(READ_TIMEOUT_MS);
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Accept", "application/json");
			connection.setDoInput(true);
			if (requestBody != null) {
				connection.setDoOutput(true);
				byte[] bytes = requestBody.getBytes(StandardCharsets.UTF_8);
				try (OutputStream outputStream = connection.getOutputStream()) {
					outputStream.write(bytes);
				}
			}
			int statusCode = connection.getResponseCode();
			InputStream inputStream = statusCode >= 400 ? connection.getErrorStream() : connection.getInputStream();
			String body = inputStream == null ? "" : readAll(inputStream);
			if (statusCode >= 400) {
				throw new IOException(body.isEmpty() ? ("HTTP " + statusCode) : body);
			}
			return body;
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}

	private RoomActionResult parseRoomAction(String body) {
		JsonValue root = jsonReader.parse(body);
		RoomActionResult result = new RoomActionResult();
		result.ok = root.getBoolean("ok", false);
		result.error = root.getString("error", null);
		result.message = root.getString("message", null);
		result.roomId = root.getString("roomId", null);
		result.roomCode = root.getString("roomCode", null);
		result.occupantId = root.getString("occupantId", null);
		String role = root.getString("role", null);
		result.role = role == null ? null : OccupantRole.valueOf(role.toUpperCase());
		JsonValue roomJson = root.get("room");
		if (roomJson != null) {
			result.room = parseRoom(roomJson);
			if (result.roomId == null) {
				result.roomId = result.room.roomId;
			}
			if (result.roomCode == null) {
				result.roomCode = result.room.roomCode;
			}
		}
		return result;
	}

	private RoomState parseRoom(JsonValue roomJson) {
		RoomState room = new RoomState();
		room.roomId = roomJson.getString("roomId");
		room.roomCode = roomJson.getString("roomCode");
		room.visibility = RoomVisibility.valueOf(roomJson.getString("visibility").toUpperCase());
		room.phase = RoomPhase.valueOf(roomJson.getString("phase").toUpperCase());
		room.maxPlayers = roomJson.getInt("maxPlayers", DEFAULT_MAX_PLAYERS);
		room.protocolVersion = roomJson.getInt("protocolVersion", PROTOCOL_VERSION);
		room.appVersion = roomJson.getString("appVersion", APP_VERSION);
		room.allowSpectators = roomJson.getBoolean("allowSpectators", true);
		room.hostOccupantId = roomJson.getString("hostOccupantId", null);
		room.hostName = roomJson.getString("hostName", null);
		room.players = parseOccupants(roomJson.get("players"));
		room.spectators = parseOccupants(roomJson.get("spectators"));

		JsonValue endpointJson = roomJson.get("endpoint");
		if (endpointJson != null) {
			room.endpoint = new ConnectInfo();
			room.endpoint.publicAddress = endpointJson.getString("publicAddress", null);
			room.endpoint.localAddress = endpointJson.getString("localAddress", null);
			room.endpoint.port = endpointJson.getInt("port", 0);
		}
		return room;
	}

	private ArrayList<RoomOccupant> parseOccupants(JsonValue values) {
		ArrayList<RoomOccupant> occupants = new ArrayList<RoomOccupant>();
		if (values == null) {
			return occupants;
		}
		for (JsonValue value = values.child; value != null; value = value.next) {
			RoomOccupant occupant = new RoomOccupant();
			occupant.occupantId = value.getString("occupantId");
			occupant.displayName = value.getString("displayName");
			occupant.ready = value.getBoolean("ready", false);
			occupant.host = value.getBoolean("isHost", false);
			occupants.add(occupant);
		}
		return occupants;
	}

	private String readAll(InputStream inputStream) throws IOException {
		StringBuilder builder = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
			String line;
			while ((line = reader.readLine()) != null) {
				builder.append(line);
			}
		}
		return builder.toString();
	}

	private static String stripTrailingSlash(String value) {
		if (value == null) {
			return "";
		}
		if (value.endsWith("/")) {
			return value.substring(0, value.length() - 1);
		}
		return value;
	}

	private static String escape(String value) {
		if (value == null) {
			return "";
		}
		return value.replace("\\", "\\\\").replace("\"", "\\\"");
	}

	public enum RoomVisibility {
		PUBLIC, PRIVATE
	}

	public enum RoomPhase {
		LOBBY, STARTING, IN_GAME, POST_MATCH, CLOSED
	}

	public enum OccupantRole {
		PLAYER, SPECTATOR
	}

	public static class RoomActionResult {
		public boolean ok;
		public String error;
		public String message;
		public String roomId;
		public String roomCode;
		public String occupantId;
		public OccupantRole role;
		public RoomState room;
	}

	public static class RoomState {
		public String roomId;
		public String roomCode;
		public RoomVisibility visibility;
		public RoomPhase phase;
		public int maxPlayers;
		public int protocolVersion;
		public String appVersion;
		public boolean allowSpectators;
		public String hostOccupantId;
		public String hostName;
		public ArrayList<RoomOccupant> players = new ArrayList<RoomOccupant>();
		public ArrayList<RoomOccupant> spectators = new ArrayList<RoomOccupant>();
		public ConnectInfo endpoint;

		public boolean isHost(String occupantId) {
			return occupantId != null && occupantId.equals(hostOccupantId);
		}
	}

	public static class RoomOccupant {
		public String occupantId;
		public String displayName;
		public boolean ready;
		public boolean host;
	}

	public static class ConnectInfo {
		public String publicAddress;
		public String localAddress;
		public int port;
	}

	public static class RelayConnectInfo {
		public boolean ok;
		public String error;
		public String message;
		public String relayUrl;
		public String hostOccupantId;
		public RoomPhase phase;
		public RoomState room;
	}

}
