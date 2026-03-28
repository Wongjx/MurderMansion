package com.jkjk.Telemetry;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.jkjk.MMHelpers.MMLog;
import com.jkjk.MMHelpers.MultiplayerSessionInfo;
import com.jkjk.Multiplayer.DiscoveryApiClient;
import com.jkjk.Multiplayer.MultiplayerPreferences;

public class TelemetryService {
	private static final String PREFS_NAME = "murder_mansion_telemetry";
	private static final String KEY_INSTALL_ID = "install_id";
	private static final String KEY_PENDING_EVENTS = "pending_events";
	private static final long WATCHDOG_THRESHOLD_MS = 5000L;
	private static final long WATCHDOG_POLL_MS = 1000L;
	private static final long MAX_LOG_FILE_BYTES = 512 * 1024L;
	private static final int MAX_RECENT_LOG_LINES = 120;

	private static volatile TelemetryService instance;

	private final MultiplayerSessionInfo sessionInfo;
	private final Preferences preferences;
	private final ArrayList<EventEnvelope> pendingEvents;
	private final ArrayDeque<String> recentLogLines;
	private final Object lock;
	private final FileHandle logFile;
	private final FileHandle crashFile;
	private final String installId;

	private volatile String sessionId;
	private volatile String currentScreenName;
	private volatile boolean flushInFlight;
	private volatile long lastHeartbeatAt;
	private volatile boolean watchdogRunning;
	private volatile boolean matchCompletedNormally;
	private Thread watchdogThread;

	public static synchronized TelemetryService initialize(MultiplayerSessionInfo sessionInfo) {
		instance = new TelemetryService(sessionInfo);
		return instance;
	}

	public static TelemetryService get() {
		return instance;
	}

	public static void reportUnhandledException(Thread thread, Throwable throwable) {
		TelemetryService telemetryService = instance;
		if (telemetryService != null) {
			telemetryService.recordUnhandledException(thread == null ? "unknown" : thread.getName(), throwable);
		}
	}

	private TelemetryService(MultiplayerSessionInfo sessionInfo) {
		this.sessionInfo = sessionInfo;
		this.preferences = Gdx.app.getPreferences(PREFS_NAME);
		this.pendingEvents = new ArrayList<EventEnvelope>();
		this.recentLogLines = new ArrayDeque<String>();
		this.lock = new Object();
		this.logFile = Gdx.files.local("telemetry/mm.log");
		this.crashFile = Gdx.files.local("telemetry/pending-crash.json");
		String storedInstallId = preferences.getString(KEY_INSTALL_ID, null);
		if (storedInstallId == null || storedInstallId.trim().isEmpty()) {
			storedInstallId = UUID.randomUUID().toString();
			preferences.putString(KEY_INSTALL_ID, storedInstallId);
			preferences.flush();
		}
		this.installId = storedInstallId;
		loadPendingEvents();
		normalizePendingCrashFile();
	}

	public void start() {
		sessionId = UUID.randomUUID().toString();
		lastHeartbeatAt = System.currentTimeMillis();
		matchCompletedNormally = false;
		startWatchdog();
		uploadPendingCrashReports();
		sendSessionStart();
		recordEvent("app_session_started", null);
	}

	public void stop(String endReason) {
		recordEvent("app_exit_intent", mapOf("reason", endReason == null ? "app_dispose" : endReason));
		sendSessionEnd(endReason == null ? "app_dispose" : endReason);
		stopWatchdog();
		flushAsync();
	}

	public void setScreenName(String screenName) {
		currentScreenName = screenName;
	}

	public void noteRenderHeartbeat() {
		lastHeartbeatAt = System.currentTimeMillis();
	}

	public void setMatchId(String matchId) {
		sessionInfo.matchId = matchId;
		matchCompletedNormally = false;
	}

	public void clearMatchId() {
		sessionInfo.matchId = null;
		matchCompletedNormally = false;
	}

	public boolean shouldRecordHostDisconnected() {
		return !matchCompletedNormally;
	}

	public void syncRoomContext() {
		// Context is read lazily from sessionInfo when building envelopes.
	}

	public void appendLogLine(String line) {
		if (line == null) {
			return;
		}
		synchronized (lock) {
			if (recentLogLines.size() >= MAX_RECENT_LOG_LINES) {
				recentLogLines.removeFirst();
			}
			recentLogLines.addLast(line);
			try {
				if (logFile.exists() && logFile.length() > MAX_LOG_FILE_BYTES) {
					logFile.writeString("", false, "UTF-8");
				}
				logFile.writeString(line + "\n", true, "UTF-8");
			} catch (Exception ignored) {
			}
		}
	}

	public void recordEvent(String eventType, Map<String, Object> payload) {
		recordEvent(eventType, currentScreenName, payload);
	}

	public void recordEvent(String eventType, String screenName, Map<String, Object> payload) {
		if (eventType == null || eventType.trim().isEmpty()) {
			return;
		}
		EventEnvelope envelope = new EventEnvelope();
		populateEnvelope(envelope);
		envelope.eventId = UUID.randomUUID().toString();
		envelope.occurredAt = System.currentTimeMillis();
		envelope.eventType = eventType;
		envelope.screenName = screenName;
		envelope.payloadJson = stringifyJsonValue(payload == null ? new HashMap<String, Object>() : payload);
		synchronized (lock) {
			pendingEvents.add(envelope);
			savePendingEvents();
		}
		flushAsync();
	}

	public void recordScoreScreenShown(boolean disconnected, boolean civWin, boolean murWin) {
		HashMap<String, Object> payload = mapOf(
				"disconnected", Boolean.valueOf(disconnected),
				"civWin", Boolean.valueOf(civWin),
				"murWin", Boolean.valueOf(murWin));
		if (!disconnected) {
			matchCompletedNormally = true;
		}
		recordEvent("score_screen_shown", "ScoreScreen", payload);
	}

	public void recordUnhandledException(String threadName, Throwable throwable) {
		recordCrash("uncaught_exception", true, threadName, throwable, null);
	}

	public void recordCrash(String kind, boolean fatal, String threadName, Throwable throwable,
			Map<String, Object> metadata) {
		CrashEnvelope crash = new CrashEnvelope();
		populateEnvelope(crash);
		crash.crashId = UUID.randomUUID().toString();
		crash.occurredAt = System.currentTimeMillis();
		crash.fatal = fatal;
		crash.kind = kind;
		crash.threadName = threadName;
		crash.exceptionClass = throwable == null ? null : throwable.getClass().getName();
		crash.message = throwable == null ? null : throwable.getMessage();
		crash.stacktrace = stackTraceToString(throwable);
		crash.recentLogTail = recentLogTail();
		crash.metadataJson = stringifyJsonValue(metadata == null ? new HashMap<String, Object>() : metadata);
		writePendingCrash(crash);
		flushCrashAsync(crash);
	}

	private void startWatchdog() {
		if (watchdogRunning) {
			return;
		}
		watchdogRunning = true;
		watchdogThread = new Thread(new Runnable() {
			@Override
			public void run() {
				boolean reported = false;
				while (watchdogRunning) {
					try {
						Thread.sleep(WATCHDOG_POLL_MS);
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
						return;
					}
					long blockedFor = System.currentTimeMillis() - lastHeartbeatAt;
					if (blockedFor >= WATCHDOG_THRESHOLD_MS) {
						if (!reported) {
							if (!shouldRecordFreezeWatchdog()) {
								reported = true;
								continue;
							}
							HashMap<String, Object> metadata = mapOf(
									"blocked_for_ms", Long.valueOf(blockedFor),
									"thread_dump_summary", buildThreadDump());
							recordCrash("freeze_watchdog", false, "render-watchdog", null, metadata);
							recordEvent("freeze_watchdog", currentScreenName, metadata);
							reported = true;
						}
					} else {
						reported = false;
					}
				}
			}
		}, "telemetry-watchdog");
		watchdogThread.setDaemon(true);
		watchdogThread.start();
	}

	private void stopWatchdog() {
		watchdogRunning = false;
		if (watchdogThread != null) {
			watchdogThread.interrupt();
			watchdogThread = null;
		}
	}

	private void sendSessionStart() {
		final SessionEnvelope envelope = buildSessionEnvelope();
		runAsync("telemetry-session-start", new Runnable() {
			@Override
			public void run() {
				postJson("/telemetry/session-start", serializeSessionEnvelope(envelope));
			}
		});
	}

	private boolean shouldRecordFreezeWatchdog() {
		if (sessionInfo == null) {
			return false;
		}
		if (sessionInfo.mRoomId == null || sessionInfo.matchId == null) {
			return false;
		}
		return "GameScreen".equals(currentScreenName);
	}

	private void sendSessionEnd(final String endReason) {
		final SessionEnvelope envelope = buildSessionEnvelope();
		envelope.endedAt = System.currentTimeMillis();
		envelope.endReason = endReason;
		runAsync("telemetry-session-end", new Runnable() {
			@Override
			public void run() {
				postJson("/telemetry/session-end", serializeSessionEnvelope(envelope));
			}
		});
	}

	private SessionEnvelope buildSessionEnvelope() {
		SessionEnvelope envelope = new SessionEnvelope();
		envelope.sessionId = sessionId;
		envelope.installId = installId;
		envelope.platform = sessionInfo.telemetryPlatform;
		envelope.appVersion = sessionInfo.telemetryAppVersion;
		envelope.buildNumber = sessionInfo.telemetryBuildNumber;
		envelope.startedAt = System.currentTimeMillis();
		envelope.deviceModel = sessionInfo.telemetryDeviceModel;
		envelope.osVersion = sessionInfo.telemetryOsVersion;
		return envelope;
	}

	private void flushAsync() {
		synchronized (lock) {
			if (flushInFlight || pendingEvents.isEmpty()) {
				return;
			}
			flushInFlight = true;
		}
		runAsync("telemetry-flush", new Runnable() {
			@Override
			public void run() {
				ArrayList<EventEnvelope> snapshot;
					synchronized (lock) {
						snapshot = new ArrayList<EventEnvelope>(pendingEvents);
					}
					boolean uploaded = postJson("/telemetry/events", serializeEventBatch(snapshot));
				synchronized (lock) {
					flushInFlight = false;
					if (uploaded) {
						pendingEvents.removeAll(snapshot);
						savePendingEvents();
					}
				}
			}
		});
	}

	private void flushCrashAsync(final CrashEnvelope crash) {
		runAsync("telemetry-crash", new Runnable() {
			@Override
			public void run() {
				boolean uploaded = postJson("/telemetry/crash", serializeCrashEnvelope(crash));
				if (uploaded) {
					removePendingCrash(crash.crashId);
				}
			}
		});
	}

	private void uploadPendingCrashReports() {
		final ArrayList<CrashEnvelope> crashes = readPendingCrashes();
		if (crashes.isEmpty()) {
			return;
		}
		runAsync("telemetry-crash-retry", new Runnable() {
			@Override
			public void run() {
				for (CrashEnvelope crash : crashes) {
					if (postJson("/telemetry/crash", serializeCrashEnvelope(crash))) {
						removePendingCrash(crash.crashId);
						recordEvent("freeze_watchdog".equals(crash.kind) ? "resume_after_freeze_report"
								: "resume_after_crash", mapOf("kind", crash.kind));
					}
				}
			}
		});
	}

	private void populateEnvelope(BaseEnvelope envelope) {
		envelope.sessionId = sessionId;
		envelope.installId = installId;
		envelope.roomId = sessionInfo.mRoomId;
		envelope.matchId = sessionInfo.matchId;
		envelope.occupantId = sessionInfo.occupantId;
		envelope.role = sessionInfo.isServer ? "host" : (sessionInfo.occupantId == null ? null : "client");
		envelope.platform = sessionInfo.telemetryPlatform;
		envelope.appVersion = sessionInfo.telemetryAppVersion;
		envelope.buildNumber = sessionInfo.telemetryBuildNumber;
	}

	private void loadPendingEvents() {
		String raw = preferences.getString(KEY_PENDING_EVENTS, "");
		if (raw == null || raw.trim().isEmpty()) {
			return;
		}
		try {
			pendingEvents.addAll(parseEventBatch(raw));
		} catch (Exception ignored) {
		}
	}

	private void normalizePendingCrashFile() {
		if (!crashFile.exists()) {
			return;
		}
		try {
			ArrayList<CrashEnvelope> crashes = readPendingCrashes();
			crashFile.writeString(serializeCrashList(crashes), false, "UTF-8");
		} catch (Exception ignored) {
		}
	}

	private void savePendingEvents() {
		preferences.putString(KEY_PENDING_EVENTS, serializeEventBatch(pendingEvents));
		preferences.flush();
	}

	private void writePendingCrash(CrashEnvelope crash) {
		ArrayList<CrashEnvelope> crashes = readPendingCrashes();
		crashes.add(crash);
		try {
			crashFile.writeString(serializeCrashList(crashes), false, "UTF-8");
		} catch (Exception ignored) {
		}
	}

	private ArrayList<CrashEnvelope> readPendingCrashes() {
		ArrayList<CrashEnvelope> crashes = new ArrayList<CrashEnvelope>();
		if (!crashFile.exists()) {
			return crashes;
		}
		try {
			crashes.addAll(parseCrashList(crashFile.readString("UTF-8")));
		} catch (Exception ignored) {
		}
		return crashes;
	}

	private void removePendingCrash(String crashId) {
		ArrayList<CrashEnvelope> crashes = readPendingCrashes();
		ArrayList<CrashEnvelope> kept = new ArrayList<CrashEnvelope>();
		for (CrashEnvelope crash : crashes) {
			if (crash == null) {
				continue;
			}
			if (crashId == null || crash.crashId == null || !crashId.equals(crash.crashId)) {
				kept.add(crash);
			}
		}
			try {
				if (kept.isEmpty()) {
					crashFile.writeString("[]", false, "UTF-8");
				} else {
					crashFile.writeString(serializeCrashList(kept), false, "UTF-8");
				}
			} catch (Exception ignored) {
			}
		}

	private String serializeSessionEnvelope(SessionEnvelope envelope) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("schemaVersion", Integer.valueOf(envelope.schemaVersion));
		map.put("sessionId", envelope.sessionId);
		map.put("installId", envelope.installId);
		map.put("platform", envelope.platform);
		map.put("appVersion", envelope.appVersion);
		map.put("buildNumber", envelope.buildNumber);
		map.put("startedAt", Long.valueOf(envelope.startedAt));
		map.put("endedAt", envelope.endedAt);
		map.put("endReason", envelope.endReason);
		map.put("deviceModel", envelope.deviceModel);
		map.put("osVersion", envelope.osVersion);
		return stringifyJsonObject(map);
	}

	private String serializeEventBatch(ArrayList<EventEnvelope> events) {
		ArrayList<HashMap<String, Object>> serializedEvents = new ArrayList<HashMap<String, Object>>();
		for (EventEnvelope event : events) {
			serializedEvents.add(toEventMap(event));
		}
		HashMap<String, Object> batch = new HashMap<String, Object>();
		batch.put("schemaVersion", Integer.valueOf(1));
		batch.put("events", serializedEvents);
		return stringifyJsonObject(batch);
	}

	private String serializeCrashEnvelope(CrashEnvelope crash) {
		return stringifyJsonObject(toCrashMap(crash));
	}

	private String serializeCrashList(ArrayList<CrashEnvelope> crashes) {
		ArrayList<HashMap<String, Object>> serializedCrashes = new ArrayList<HashMap<String, Object>>();
		for (CrashEnvelope crash : crashes) {
			serializedCrashes.add(toCrashMap(crash));
		}
		return stringifyJsonArray(serializedCrashes);
	}

	private HashMap<String, Object> toEventMap(EventEnvelope event) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		putBaseEnvelope(map, event);
		map.put("eventId", event.eventId);
		map.put("occurredAt", Long.valueOf(event.occurredAt));
		map.put("eventType", event.eventType);
		map.put("screenName", event.screenName);
		map.put("payloadJson", event.payloadJson);
		return map;
	}

	private HashMap<String, Object> toCrashMap(CrashEnvelope crash) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		putBaseEnvelope(map, crash);
		map.put("schemaVersion", Integer.valueOf(crash.schemaVersion));
		map.put("crashId", crash.crashId);
		map.put("occurredAt", Long.valueOf(crash.occurredAt));
		map.put("fatal", Boolean.valueOf(crash.fatal));
		map.put("kind", crash.kind);
		map.put("threadName", crash.threadName);
		map.put("exceptionClass", crash.exceptionClass);
		map.put("message", crash.message);
		map.put("stacktrace", crash.stacktrace);
		map.put("recentLogTail", crash.recentLogTail);
		map.put("metadataJson", crash.metadataJson);
		return map;
	}

	private void putBaseEnvelope(HashMap<String, Object> map, BaseEnvelope envelope) {
		map.put("sessionId", envelope.sessionId);
		map.put("installId", envelope.installId);
		map.put("roomId", envelope.roomId);
		map.put("matchId", envelope.matchId);
		map.put("occupantId", envelope.occupantId);
		map.put("role", envelope.role);
		map.put("platform", envelope.platform);
		map.put("appVersion", envelope.appVersion);
		map.put("buildNumber", envelope.buildNumber);
	}

	private String stringifyJsonObject(Map<String, Object> map) {
		StringBuilder builder = new StringBuilder();
		builder.append('{');
		boolean first = true;
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			if (!first) {
				builder.append(',');
			}
			first = false;
			builder.append('"').append(escapeJson(entry.getKey())).append('"').append(':');
			builder.append(stringifyJsonValue(entry.getValue()));
		}
		builder.append('}');
		return builder.toString();
	}

	private String stringifyJsonArray(Iterable<?> values) {
		StringBuilder builder = new StringBuilder();
		builder.append('[');
		boolean first = true;
		for (Object value : values) {
			if (!first) {
				builder.append(',');
			}
			first = false;
			builder.append(stringifyJsonValue(value));
		}
		builder.append(']');
		return builder.toString();
	}

	@SuppressWarnings("unchecked")
	private String stringifyJsonValue(Object value) {
		if (value == null) {
			return "null";
		}
		if (value instanceof String) {
			return "\"" + escapeJson((String) value) + "\"";
		}
		if (value instanceof Number || value instanceof Boolean) {
			return String.valueOf(value);
		}
		if (value instanceof Map<?, ?>) {
			return stringifyJsonObject((Map<String, Object>) value);
		}
		if (value instanceof Iterable<?>) {
			return stringifyJsonArray((Iterable<?>) value);
		}
		if (value.getClass().isArray()) {
			ArrayList<Object> arrayValues = new ArrayList<Object>();
			if (value instanceof Object[]) {
				for (Object element : (Object[]) value) {
					arrayValues.add(element);
				}
			} else if (value instanceof int[]) {
				for (int element : (int[]) value) {
					arrayValues.add(Integer.valueOf(element));
				}
			} else if (value instanceof long[]) {
				for (long element : (long[]) value) {
					arrayValues.add(Long.valueOf(element));
				}
			} else if (value instanceof float[]) {
				for (float element : (float[]) value) {
					arrayValues.add(Float.valueOf(element));
				}
			} else if (value instanceof double[]) {
				for (double element : (double[]) value) {
					arrayValues.add(Double.valueOf(element));
				}
			} else if (value instanceof boolean[]) {
				for (boolean element : (boolean[]) value) {
					arrayValues.add(Boolean.valueOf(element));
				}
			}
			return stringifyJsonArray(arrayValues);
		}
		return "\"" + escapeJson(String.valueOf(value)) + "\"";
	}

	private String escapeJson(String value) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < value.length(); i++) {
			char ch = value.charAt(i);
			switch (ch) {
			case '\\':
				builder.append("\\\\");
				break;
			case '"':
				builder.append("\\\"");
				break;
			case '\b':
				builder.append("\\b");
				break;
			case '\f':
				builder.append("\\f");
				break;
			case '\n':
				builder.append("\\n");
				break;
			case '\r':
				builder.append("\\r");
				break;
			case '\t':
				builder.append("\\t");
				break;
			default:
				if (ch < 0x20) {
					builder.append(String.format("\\u%04x", Integer.valueOf(ch)));
				} else {
					builder.append(ch);
				}
				break;
			}
		}
		return builder.toString();
	}

	private ArrayList<EventEnvelope> parseEventBatch(String raw) {
		ArrayList<EventEnvelope> events = new ArrayList<EventEnvelope>();
		JsonValue root = new JsonReader().parse(raw);
		JsonValue eventValues = root == null ? null : root.get("events");
		if (eventValues == null) {
			return events;
		}
		for (JsonValue eventValue = eventValues.child; eventValue != null; eventValue = eventValue.next) {
			events.add(parseEventEnvelope(eventValue));
		}
		return events;
	}

	private ArrayList<CrashEnvelope> parseCrashList(String raw) {
		ArrayList<CrashEnvelope> crashes = new ArrayList<CrashEnvelope>();
		JsonValue root = new JsonReader().parse(raw);
		if (root == null) {
			return crashes;
		}
		if (root.isArray()) {
			for (JsonValue crashValue = root.child; crashValue != null; crashValue = crashValue.next) {
				crashes.add(parseCrashEnvelope(crashValue));
			}
		} else {
			crashes.add(parseCrashEnvelope(root));
		}
		return crashes;
	}

	private EventEnvelope parseEventEnvelope(JsonValue value) {
		EventEnvelope envelope = new EventEnvelope();
		populateBaseEnvelope(envelope, value);
		envelope.eventId = value.getString("eventId", null);
		envelope.occurredAt = value.getLong("occurredAt", 0L);
		envelope.eventType = value.getString("eventType", null);
		envelope.screenName = value.getString("screenName", null);
		envelope.payloadJson = value.getString("payloadJson", "{}");
		return envelope;
	}

	private CrashEnvelope parseCrashEnvelope(JsonValue value) {
		CrashEnvelope envelope = new CrashEnvelope();
		populateBaseEnvelope(envelope, value);
		envelope.schemaVersion = value.getInt("schemaVersion", 1);
		envelope.crashId = value.getString("crashId", null);
		if (envelope.crashId == null || envelope.crashId.trim().isEmpty()) {
			envelope.crashId = UUID.randomUUID().toString();
		}
		envelope.occurredAt = value.getLong("occurredAt", 0L);
		envelope.fatal = value.getBoolean("fatal", false);
		envelope.kind = value.getString("kind", null);
		envelope.threadName = value.getString("threadName", null);
		envelope.exceptionClass = value.getString("exceptionClass", null);
		envelope.message = value.getString("message", null);
		envelope.stacktrace = value.getString("stacktrace", null);
		envelope.recentLogTail = value.getString("recentLogTail", null);
		envelope.metadataJson = value.getString("metadataJson", "{}");
		return envelope;
	}

	private void populateBaseEnvelope(BaseEnvelope envelope, JsonValue value) {
		envelope.sessionId = value.getString("sessionId", null);
		envelope.installId = value.getString("installId", null);
		envelope.roomId = value.getString("roomId", null);
		envelope.matchId = value.getString("matchId", null);
		envelope.occupantId = value.getString("occupantId", null);
		envelope.role = value.getString("role", null);
		envelope.platform = value.getString("platform", null);
		envelope.appVersion = value.getString("appVersion", null);
		envelope.buildNumber = value.getString("buildNumber", null);
	}

	private String recentLogTail() {
		StringBuilder builder = new StringBuilder();
		synchronized (lock) {
			for (String line : recentLogLines) {
				builder.append(line).append('\n');
			}
		}
		return builder.toString();
	}

	private String buildThreadDump() {
		StringBuilder builder = new StringBuilder();
		Map<Thread, StackTraceElement[]> traces = Thread.getAllStackTraces();
		for (Map.Entry<Thread, StackTraceElement[]> entry : traces.entrySet()) {
			builder.append(entry.getKey().getName()).append('\n');
			for (StackTraceElement element : entry.getValue()) {
				builder.append("  at ").append(element.toString()).append('\n');
			}
		}
		return builder.toString();
	}

	private boolean postJson(String path, String body) {
		HttpURLConnection connection = null;
		try {
			URL url = new URL(getBaseUrl() + path);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setConnectTimeout(4000);
			connection.setReadTimeout(4000);
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setDoOutput(true);
			byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
			try (OutputStream outputStream = connection.getOutputStream()) {
				outputStream.write(bytes);
			}
			int statusCode = connection.getResponseCode();
			if (statusCode >= 400) {
				InputStream errorStream = connection.getErrorStream();
				if (errorStream != null) {
					readFully(errorStream);
				}
				return false;
			}
			InputStream inputStream = connection.getInputStream();
			if (inputStream != null) {
				readFully(inputStream);
			}
			return true;
		} catch (Exception e) {
			return false;
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}

	private String getBaseUrl() {
		String discoveryUrl = MultiplayerPreferences.getDiscoveryUrl();
		if (discoveryUrl == null || discoveryUrl.trim().isEmpty()) {
			discoveryUrl = sessionInfo.discoveryUrl;
		}
		if (discoveryUrl == null || discoveryUrl.trim().isEmpty()) {
			discoveryUrl = DiscoveryApiClient.DEFAULT_BASE_URL;
		}
		if (discoveryUrl.endsWith("/")) {
			return discoveryUrl.substring(0, discoveryUrl.length() - 1);
		}
		return discoveryUrl;
	}

	private void runAsync(String threadName, Runnable runnable) {
		Thread thread = new Thread(runnable, threadName);
		thread.setDaemon(true);
		thread.start();
	}

	private static String readFully(InputStream inputStream) throws IOException {
		StringBuilder builder = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
			String line;
			while ((line = reader.readLine()) != null) {
				builder.append(line);
			}
		}
		return builder.toString();
	}

	private static String stackTraceToString(Throwable throwable) {
		if (throwable == null) {
			return null;
		}
		StringBuilder builder = new StringBuilder();
		builder.append(throwable.toString()).append('\n');
		for (StackTraceElement element : throwable.getStackTrace()) {
			builder.append("  at ").append(element.toString()).append('\n');
		}
		Throwable cause = throwable.getCause();
		if (cause != null && cause != throwable) {
			builder.append("Caused by: ").append(stackTraceToString(cause));
		}
		return builder.toString();
	}

	private static HashMap<String, Object> mapOf(Object... values) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		for (int i = 0; i + 1 < values.length; i += 2) {
			map.put(String.valueOf(values[i]), values[i + 1]);
		}
		return map;
	}

	private static class BaseEnvelope {
		public String sessionId;
		public String installId;
		public String roomId;
		public String matchId;
		public String occupantId;
		public String role;
		public String platform;
		public String appVersion;
		public String buildNumber;
	}

	private static class EventEnvelope extends BaseEnvelope {
		public String eventId;
		public long occurredAt;
		public String eventType;
		public String screenName;
		public String payloadJson;
	}

	private static class EventBatch {
		public int schemaVersion = 1;
		public ArrayList<EventEnvelope> events = new ArrayList<EventEnvelope>();

		public EventBatch() {
		}

		public EventBatch(ArrayList<EventEnvelope> events) {
			this.events = new ArrayList<EventEnvelope>(events);
		}
	}

	private static class CrashEnvelope extends BaseEnvelope {
		public int schemaVersion = 1;
		public String crashId;
		public long occurredAt;
		public boolean fatal;
		public String kind;
		public String threadName;
		public String exceptionClass;
		public String message;
		public String stacktrace;
		public String recentLogTail;
		public String metadataJson;
	}

	private static class SessionEnvelope {
		public int schemaVersion = 1;
		public String sessionId;
		public String installId;
		public String platform;
		public String appVersion;
		public String buildNumber;
		public long startedAt;
		public Long endedAt;
		public String endReason;
		public String deviceModel;
		public String osVersion;
	}
}
