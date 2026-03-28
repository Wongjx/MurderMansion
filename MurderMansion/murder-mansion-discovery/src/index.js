const ROOM_TTL_MS = 30 * 60_000;
const DEFAULT_MAX_PLAYERS = 6;
const KICK_LOCKOUT_MS = 30 * 60_000;

export default {
  async fetch(request, env) {
    try {
      const url = new URL(request.url);
      const path = url.pathname.replace(/\/+$/, "") || "/";
      const method = request.method.toUpperCase();

      if (method === "OPTIONS") {
        return cors(new Response(null, { status: 204 }));
      }

      if (method === "GET" && path === "/health") {
        return json({ ok: true, service: "murder-mansion-discovery" });
      }

      if (method === "POST" && path === "/telemetry/session-start") {
        return handleSessionStart(env, await request.json());
      }

      if (method === "POST" && path === "/telemetry/session-end") {
        return handleSessionEnd(env, await request.json());
      }

      if (method === "POST" && path === "/telemetry/events") {
        return handleTelemetryEvents(env, await request.json());
      }

      if (method === "POST" && path === "/telemetry/crash") {
        return handleCrashReport(env, await request.json());
      }

      if (method === "POST" && path === "/rooms") {
        const payload = await request.json();
        return createRoom(env, payload, getRequesterIp(request));
      }

      if (method === "POST" && path === "/rooms/quick-start") {
        const payload = await request.json();
        return quickStart(env, payload, request, getRequesterIp(request));
      }

      if (method === "GET" && path.startsWith("/rooms/code/")) {
        const roomCode = path.substring("/rooms/code/".length).toUpperCase();
        return resolveRoomCode(env, roomCode);
      }

      if (method === "GET" && /^\/rooms\/[^/]+$/.test(path)) {
        const roomId = path.split("/")[2];
        return fetchRoom(env, roomId);
      }

      if (method === "POST" && /^\/rooms\/[^/]+\/join$/.test(path)) {
        const roomId = path.split("/")[2];
        const body = await request.json();
        body.requesterIp = getRequesterIp(request);
        return roomCommand(env, roomId, "/join", body, request);
      }

      if (method === "POST" && /^\/rooms\/[^/]+\/connect-info$/.test(path)) {
        const roomId = path.split("/")[2];
        const body = await request.json();
        body.requestOrigin = url.origin;
        return roomCommand(env, roomId, "/connect-info", body, request);
      }

      if (method === "POST" && /^\/rooms\/[^/]+\/poll$/.test(path)) {
        const roomId = path.split("/")[2];
        return roomCommand(env, roomId, "/poll", await request.json(), request);
      }

      if (method === "POST" && /^\/rooms\/[^/]+\/kick$/.test(path)) {
        const roomId = path.split("/")[2];
        return roomCommand(env, roomId, "/kick", await request.json(), request);
      }

      if (method === "POST" && /^\/rooms\/[^/]+\/ready$/.test(path)) {
        const roomId = path.split("/")[2];
        return roomCommand(env, roomId, "/ready", await request.json(), request);
      }

      if (method === "POST" && /^\/rooms\/[^/]+\/start$/.test(path)) {
        const roomId = path.split("/")[2];
        const body = await request.json();
        body.publicAddress = getRequesterIp(request);
        return roomCommand(env, roomId, "/start", body, request);
      }

      if (method === "POST" && /^\/rooms\/[^/]+\/finish$/.test(path)) {
        const roomId = path.split("/")[2];
        return roomCommand(env, roomId, "/finish", await request.json(), request);
      }

      if (method === "POST" && /^\/rooms\/[^/]+\/heartbeat$/.test(path)) {
        const roomId = path.split("/")[2];
        const body = await request.json();
        body.publicAddress = getRequesterIp(request);
        return roomCommand(env, roomId, "/heartbeat", body, request);
      }

      if (method === "POST" && /^\/rooms\/[^/]+\/leave$/.test(path)) {
        const roomId = path.split("/")[2];
        return roomCommand(env, roomId, "/leave", await request.json(), request);
      }

      if (method === "POST" && /^\/rooms\/[^/]+\/close$/.test(path)) {
        const roomId = path.split("/")[2];
        return roomCommand(env, roomId, "/close", await request.json(), request);
      }

      if (method === "GET" && /^\/rooms\/[^/]+\/relay$/.test(path)) {
        const roomId = path.split("/")[2];
        const roomStub = env.ROOM_OBJECT.get(env.ROOM_OBJECT.idFromName(roomId));
        const relayUrl = new URL("https://room/relay");
        relayUrl.search = url.search;
        return roomStub.fetch(new Request(relayUrl.toString(), request));
      }

      return json({ ok: false, error: "not_found" }, 404);
    } catch (error) {
      return json(
        {
          ok: false,
          error: "internal_error",
          message: error instanceof Error ? error.message : String(error)
        },
        500
      );
    }
  }
};

export class RoomDirectory {
  constructor(state) {
    this.state = state;
  }

  async fetch(request) {
    const url = new URL(request.url);
    const path = url.pathname;
    const method = request.method.toUpperCase();
    const directory = (await this.state.storage.get("directory")) || {
      codeToRoomId: {},
      publicRooms: {}
    };
    pruneDirectory(directory);

    if (method === "POST" && path === "/allocate-code") {
      let code = "";
      do {
        code = randomCode();
      } while (directory.codeToRoomId[code]);
      return json({ ok: true, roomCode: code });
    }

    if (method === "POST" && path === "/register-code") {
      const body = await request.json();
      directory.codeToRoomId[body.roomCode] = body.roomId;
      await this.state.storage.put("directory", directory);
      return json({ ok: true });
    }

    if (method === "POST" && path === "/sync-room") {
      const body = await request.json();
      const room = body.room;
      if (!room) {
        return json({ ok: false, error: "missing_room" }, 400);
      }
      directory.codeToRoomId[room.roomCode] = room.roomId;
      if (room.visibility === "public" && room.phase !== "closed") {
        directory.publicRooms[room.roomId] = toPublicSummary(room);
      } else {
        delete directory.publicRooms[room.roomId];
      }
      await this.state.storage.put("directory", directory);
      return json({ ok: true });
    }

    if (method === "POST" && path === "/remove-room") {
      const body = await request.json();
      if (body.roomId) {
        delete directory.publicRooms[body.roomId];
      }
      if (body.roomCode) {
        delete directory.codeToRoomId[body.roomCode];
      }
      await this.state.storage.put("directory", directory);
      return json({ ok: true });
    }

    if (method === "GET" && path.startsWith("/code/")) {
      const code = path.substring("/code/".length).toUpperCase();
      const roomId = directory.codeToRoomId[code] || null;
      return json({ ok: true, roomId, roomCode: code });
    }

    if (method === "POST" && path === "/quick-start") {
      const body = await request.json();
      const publicRooms = Object.values(directory.publicRooms)
        .filter((room) => room.protocolVersion === body.protocolVersion)
        .filter((room) => room.appVersion === body.appVersion)
        .filter((room) => room.visibility === "public")
        .filter((room) => room.phase === "lobby" || room.phase === "starting" || room.phase === "in_game")
        .filter((room) => room.playerCount + room.spectatorCount < room.maxPlayers)
        .sort((a, b) => a.playerCount - b.playerCount);
      return json({ ok: true, room: publicRooms[0] || null });
    }

    return json({ ok: false, error: "not_found" }, 404);
  }
}

export class RoomObject {
  constructor(state, env) {
    this.state = state;
    this.env = env;
    this.connections = new Map();
    this.lastVolatileActivityAt = 0;
    this.debugCounters = {
      persistentWrites: 0,
      relayMessagesReceived: 0,
      relayMessagesBroadcast: 0,
      roomPolls: 0
    };
  }

  async fetch(request) {
    const url = new URL(request.url);
    const path = url.pathname;
    const method = request.method.toUpperCase();
    let room = await this.state.storage.get("room");

    if (method === "GET" && path === "/relay") {
      return this.handleRelayUpgrade(request, room);
    }

    if (!room && !(method === "POST" && path === "/create")) {
      return json({ ok: false, error: "room_not_found" }, 404);
    }

    if (room) {
      room = this.ensureRoomFresh(room);
    }

    if (method === "POST" && path === "/create") {
      const body = await request.json();
      room = createRoomState(body);
      await this.persistRoom(room, "create");
      return json({ ok: true, roomId: room.roomId, roomCode: room.roomCode, occupantId: room.hostOccupantId, role: "player", room });
    }

    if (method === "GET" && path === "/summary") {
      return json({ ok: true, room });
    }

    if (method === "POST" && path === "/poll") {
      const body = await request.json();
      this.noteCounter("roomPolls");
      const occupant = findOccupant(room, body.occupantId);
      if (!occupant) {
        return json({ ok: false, error: "occupant_not_found" }, 404);
      }
      return json({ ok: true, room });
    }

    if (method === "POST" && path === "/connect-info") {
      const body = await request.json();
      const occupant = findOccupant(room, body.occupantId);
      if (!occupant) {
        return json({ ok: false, error: "occupant_not_found" }, 404);
      }
      if (room.phase !== "starting" && room.phase !== "in_game") {
        return json({ ok: false, error: "room_not_connectable", phase: room.phase, room }, 409);
      }
      const requestOrigin = body.requestOrigin || url.origin;
      return json({
        ok: true,
        relayUrl: buildRelayUrl(requestOrigin, room.roomId, body.occupantId),
        hostOccupantId: room.hostOccupantId,
        phase: room.phase,
        room
      });
    }

    if (method === "POST" && path === "/join") {
      const body = await request.json();
      let joinResult;
      try {
        joinResult = joinRoom(room, body);
      } catch (error) {
        const message = error instanceof Error ? error.message : String(error);
        const status = message === "room_full" || message === "room_not_joinable"
          || message === "You were kicked from this room. Try again later."
          ? 409
          : 400;
        return json({ ok: false, error: message }, status);
      }
      await this.persistRoom(joinResult.room, "join");
      return json({ ok: true, occupantId: joinResult.occupantId, role: joinResult.role, room: joinResult.room });
    }

    if (method === "POST" && path === "/kick") {
      const body = await request.json();
      assertHost(room, body.occupantId);
      const target = findOccupant(room, body.targetOccupantId);
      if (target) {
        registerKick(room, target);
      }
      room.players = room.players.filter((player) => player.occupantId !== body.targetOccupantId);
      room.spectators = room.spectators.filter((spectator) => spectator.occupantId !== body.targetOccupantId);
      room.updatedAt = Date.now();
      await this.persistRoom(room, "kick");
      return json({ ok: true, room });
    }

    if (method === "POST" && path === "/ready") {
      const body = await request.json();
      const player = room.players.find((candidate) => candidate.occupantId === body.occupantId);
      if (!player) {
        return json({ ok: false, error: "player_not_found" }, 404);
      }
      player.ready = !!body.ready;
      room.updatedAt = Date.now();
      await this.persistRoom(room, "ready");
      return json({ ok: true, room });
    }

    if (method === "POST" && path === "/start") {
      const body = await request.json();
      assertHost(room, body.occupantId);
      if (room.phase !== "lobby") {
        return json({ ok: false, error: "room_not_in_lobby" }, 409);
      }
      if (room.players.length < 2) {
        return json({ ok: false, error: "not_enough_players" }, 409);
      }
      const allReady = room.players.every((player) => player.ready);
      if (!allReady) {
        return json({ ok: false, error: "players_not_ready" }, 409);
      }
      room.phase = "starting";
      room.matchId = crypto.randomUUID();
      room.endpoint = {
        publicAddress: body.publicAddress || null,
        localAddress: body.localAddress || null,
        port: body.port || null
      };
      room.lastHeartbeatAt = Date.now();
      room.updatedAt = Date.now();
      await this.persistRoom(room, "start");
      return json({ ok: true, room });
    }

    if (method === "POST" && path === "/heartbeat") {
      const body = await request.json();
      assertHost(room, body.occupantId);
      room.lastHeartbeatAt = Date.now();
      if (body.port) {
        room.endpoint = {
          publicAddress: body.publicAddress || room.endpoint?.publicAddress || null,
          localAddress: body.localAddress || room.endpoint?.localAddress || null,
          port: body.port
        };
      }
      room.updatedAt = Date.now();
      await this.persistRoom(room, "heartbeat");
      return json({ ok: true, room });
    }

    if (method === "POST" && path === "/finish") {
      const body = await request.json();
      assertHost(room, body.occupantId);
      const finishedMatchId = room.matchId;
      room.phase = "lobby";
      room.matchId = null;
      room.endpoint = null;
      room.players.forEach((player) => {
        player.ready = false;
      });
      promoteSpectators(room);
      room.updatedAt = Date.now();
      await this.persistRoom(room, "finish");
      this.closeActiveSocketsSilently(1000, "Match finished");
      return json({ ok: true, room, finishedMatchId });
    }

    if (method === "POST" && path === "/leave") {
      const body = await request.json();
      const leavingMatchId = room.matchId;
      const wasHost = body.occupantId === room.hostOccupantId;
      if (body.occupantId === room.hostOccupantId) {
        removeOccupant(room, body.occupantId);
        if (room.phase === "lobby") {
          transferLobbyHost(room);
        } else {
          room.phase = "closed";
        }
      } else {
        removeOccupant(room, body.occupantId);
        if (room.phase === "lobby") {
          promoteSpectators(room);
        }
      }
      room.updatedAt = Date.now();
      await this.persistRoom(room, "leave");
      if (room.phase === "closed") {
        this.closeActiveSockets("room_closed", 1000, "Room closed");
      }
      return json({ ok: true, room, endedMatchId: room.phase === "closed" ? leavingMatchId : null, hostTransferred: wasHost && room.phase === "lobby" });
    }

    if (method === "POST" && path === "/close") {
      const body = await request.json();
      assertHost(room, body.occupantId);
      const closingMatchId = room.matchId;
      room.phase = "closed";
      room.updatedAt = Date.now();
      await this.persistRoom(room, "close");
      this.closeActiveSockets("room_closed", 1000, "Room closed");
      return json({ ok: true, room, endedMatchId: closingMatchId });
    }

    return json({ ok: false, error: "not_found" }, 404);
  }

  async handleRelayUpgrade(request, room) {
    if (!room) {
      return json({ ok: false, error: "room_not_found" }, 404);
    }
    const upgradeHeader = request.headers.get("Upgrade");
    if (!upgradeHeader || upgradeHeader.toLowerCase() !== "websocket") {
      return json({ ok: false, error: "expected_websocket" }, 426);
    }
    room = this.ensureRoomFresh(room);
    const url = new URL(request.url);
    const occupantId = url.searchParams.get("occupantId");
    const occupant = findOccupant(room, occupantId);
    if (!occupant || !room.players.some((player) => player.occupantId === occupantId)) {
      return json({ ok: false, error: "occupant_not_found" }, 404);
    }
    if (room.phase === "closed") {
      return json({ ok: false, error: "room_closed" }, 409);
    }

    const pair = new WebSocketPair();
    const [clientSocket, serverSocket] = Object.values(pair);
    serverSocket.accept();
    this.connections.set(occupantId, serverSocket);
    this.touchVolatileActivity();
    serverSocket.addEventListener("message", (event) => {
      this.handleRelayMessage(occupantId, String(event.data));
    });
    serverSocket.addEventListener("close", async () => {
      await this.handleSocketClose(room, occupantId);
    });
    serverSocket.addEventListener("error", async () => {
      await this.handleSocketClose(room, occupantId);
    });

    serverSocket.send(JSON.stringify({
      type: "connected",
      isHost: occupantId === room.hostOccupantId,
      occupantId
    }));

    if (occupantId !== room.hostOccupantId) {
      this.sendToOccupant(room.hostOccupantId, {
        type: "peer_connected",
        occupantId
      });
    }

    return new Response(null, { status: 101, webSocket: clientSocket });
  }

  async handleRelayMessage(fromOccupantId, rawMessage) {
    this.noteCounter("relayMessagesReceived");
    this.touchVolatileActivity();
    const room = this.ensureRoomFresh(await this.state.storage.get("room"));
    if (!room || room.phase === "closed") {
      return;
    }

    let message;
    try {
      message = JSON.parse(rawMessage);
    } catch (error) {
      this.sendToOccupant(fromOccupantId, { type: "error", message: "invalid_json" });
      return;
    }

    if (message.type === "ping") {
      return;
    }

    if (message.type !== "payload") {
      this.sendToOccupant(fromOccupantId, { type: "error", message: "unsupported_type" });
      return;
    }

    if (fromOccupantId === room.hostOccupantId) {
      if (message.targetOccupantId) {
        this.sendToOccupant(message.targetOccupantId, {
          type: "payload",
          fromOccupantId,
          payload: message.payload || ""
        });
        return;
      }
      if (message.broadcast) {
        for (const player of room.players) {
          if (player.occupantId === fromOccupantId) {
            continue;
          }
          this.sendToOccupant(player.occupantId, {
            type: "payload",
            fromOccupantId,
            payload: message.payload || ""
          });
        }
        return;
      }
    }

    this.sendToOccupant(room.hostOccupantId, {
      type: "payload",
      fromOccupantId,
      payload: message.payload || ""
    });
  }

  async handleSocketClose(room, occupantId) {
    if (!this.connections.has(occupantId)) {
      return;
    }
    this.connections.delete(occupantId);
    this.touchVolatileActivity();

    room = this.ensureRoomFresh(await this.state.storage.get("room"));
    if (!room) {
      return;
    }

    if (occupantId === room.hostOccupantId) {
      room.phase = "closed";
      room.updatedAt = Date.now();
      await this.persistRoom(room, "host_socket_close");
      for (const [peerId, socket] of this.connections.entries()) {
        socket.send(JSON.stringify({ type: "host_disconnected" }));
        try {
          socket.close(1012, "Host disconnected");
        } catch (error) {}
      }
      this.connections.clear();
      await removeRoom(this.env, room);
      return;
    }

    this.sendToOccupant(room.hostOccupantId, { type: "peer_disconnected", occupantId });
  }

  sendToOccupant(occupantId, payload) {
    const socket = this.connections.get(occupantId);
    if (!socket) {
      return;
    }
    this.noteCounter("relayMessagesBroadcast");
    this.touchVolatileActivity();
    socket.send(JSON.stringify(payload));
  }

  closeActiveSockets(messageType, closeCode, closeReason) {
    for (const [, socket] of this.connections.entries()) {
      try {
        socket.send(JSON.stringify({ type: messageType }));
      } catch (error) {}
      try {
        socket.close(closeCode, closeReason);
      } catch (error) {}
    }
    this.connections.clear();
  }

  closeActiveSocketsSilently(closeCode, closeReason) {
    for (const [, socket] of this.connections.entries()) {
      try {
        socket.close(closeCode, closeReason);
      } catch (error) {}
    }
    this.connections.clear();
  }

  async persistRoom(room, reason) {
    this.noteCounter("persistentWrites");
    if (this.env.DEBUG_COSTS === "true" && this.debugCounters.persistentWrites % 25 === 0) {
      console.log("room_persist", room.roomId, reason, this.debugCounters);
    }
    await this.state.storage.put("room", room);
  }

  noteCounter(name) {
    this.debugCounters[name] = (this.debugCounters[name] || 0) + 1;
  }

  touchVolatileActivity() {
    this.lastVolatileActivityAt = Date.now();
  }

  ensureRoomFresh(room) {
    if (!room) {
      return room;
    }
    room = ensureFresh(room);
    if (room.phase === "closed") {
      const volatileActiveAt = this.lastVolatileActivityAt || 0;
      const hasActiveConnections = this.connections.size > 0;
      if (hasActiveConnections && volatileActiveAt > 0 && Date.now() - volatileActiveAt <= ROOM_TTL_MS) {
        room.phase = room.matchId ? "in_game" : "lobby";
      }
    }
    return room;
  }
}

async function handleSessionStart(env, body) {
  if (!telemetryEnabled(env)) {
    return json({ ok: true, accepted: 0, telemetryEnabled: false });
  }
  validateTelemetrySchema(body);
  const now = Date.now();
  await env.TELEMETRY_DB.prepare(
    `INSERT INTO app_sessions (
      session_id, install_id, platform, app_version, build_number, started_at, ended_at, end_reason, device_model, os_version
    ) VALUES (?, ?, ?, ?, ?, ?, NULL, NULL, ?, ?)
    ON CONFLICT(session_id) DO UPDATE SET
      install_id=excluded.install_id,
      platform=excluded.platform,
      app_version=excluded.app_version,
      build_number=excluded.build_number,
      device_model=excluded.device_model,
      os_version=excluded.os_version`
  ).bind(
    body.sessionId,
    body.installId,
    body.platform,
    body.appVersion,
    body.buildNumber,
    body.startedAt || now,
    body.deviceModel || null,
    body.osVersion || null
  ).run();
  return json({ ok: true, accepted: 1 });
}

async function handleSessionEnd(env, body) {
  if (!telemetryEnabled(env)) {
    return json({ ok: true, accepted: 0, telemetryEnabled: false });
  }
  validateTelemetrySchema(body);
  await env.TELEMETRY_DB.prepare(
    `UPDATE app_sessions
     SET ended_at = ?, end_reason = ?
     WHERE session_id = ?`
  ).bind(body.endedAt || Date.now(), body.endReason || null, body.sessionId).run();
  return json({ ok: true, accepted: 1 });
}

async function handleTelemetryEvents(env, body) {
  if (!telemetryEnabled(env)) {
    return json({ ok: true, accepted: 0, telemetryEnabled: false });
  }
  validateTelemetrySchema(body);
  const events = Array.isArray(body.events) ? body.events : [];
  const receivedAt = Date.now();
  for (const event of events) {
    await insertTelemetryEvent(env, event, receivedAt);
    await applyTelemetryEventSideEffects(env, event);
  }
  return json({ ok: true, accepted: events.length });
}

async function handleCrashReport(env, body) {
  if (!telemetryEnabled(env)) {
    return json({ ok: true, accepted: 0, telemetryEnabled: false });
  }
  validateTelemetrySchema(body);
  const receivedAt = Date.now();
  await env.TELEMETRY_DB.prepare(
    `INSERT INTO crash_reports (
      crash_id, occurred_at, received_at, session_id, install_id, room_id, match_id, occupant_id, role,
      platform, app_version, build_number, fatal, kind, thread_name, exception_class, message,
      stacktrace, recent_log_tail, metadata_json
    ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)`
  ).bind(
    body.crashId,
    body.occurredAt || receivedAt,
    receivedAt,
    body.sessionId || null,
    body.installId || null,
    body.roomId || null,
    body.matchId || null,
    body.occupantId || null,
    body.role || null,
    body.platform,
    body.appVersion,
    body.buildNumber,
    body.fatal ? 1 : 0,
    body.kind,
    body.threadName || null,
    body.exceptionClass || null,
    body.message || null,
    body.stacktrace || null,
    body.recentLogTail || null,
    body.metadataJson || null
  ).run();
  if (body.matchId && body.occupantId) {
    await upsertMatchParticipant(env, {
      matchId: body.matchId,
      occupantId: body.occupantId,
      installId: body.installId || null,
      sessionId: body.sessionId || null,
      role: body.role || "client",
      platform: body.platform,
      appVersion: body.appVersion,
      buildNumber: body.buildNumber,
      joinedMatchAt: body.occurredAt || receivedAt
    });
    await updateParticipantTerminal(env, body.matchId, body.occupantId, body.kind);
    if (body.role === "host") {
      await recordMatchFinish(env, body.matchId, body.kind === "freeze_watchdog" ? "freeze_or_hang" : "host_crash",
        body.role === "host" ? "host_crash_report" : "client_crash_report");
    }
  }
  return json({ ok: true, accepted: 1 });
}

function validateTelemetrySchema(body) {
  if (!body || (body.schemaVersion != null && body.schemaVersion !== 1)) {
    throw new Error("invalid_telemetry_schema");
  }
}

async function insertTelemetryEvent(env, event, receivedAt) {
  await env.TELEMETRY_DB.prepare(
    `INSERT INTO telemetry_events (
      event_id, occurred_at, received_at, session_id, install_id, room_id, match_id, occupant_id, role, platform,
      app_version, build_number, event_type, screen_name, payload_json
    ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)`
  ).bind(
    event.eventId || crypto.randomUUID(),
    event.occurredAt || receivedAt,
    receivedAt,
    event.sessionId || null,
    event.installId || null,
    event.roomId || null,
    event.matchId || null,
    event.occupantId || null,
    event.role || null,
    event.platform || null,
    event.appVersion || null,
    event.buildNumber || null,
    event.eventType,
    event.screenName || null,
    event.payloadJson || "{}"
  ).run();
}

async function applyTelemetryEventSideEffects(env, event) {
  if (event.matchId && event.occupantId && event.platform && event.appVersion && event.buildNumber) {
    await upsertMatchParticipant(env, {
      matchId: event.matchId,
      occupantId: event.occupantId,
      installId: event.installId || null,
      sessionId: event.sessionId || null,
      role: event.role || "client",
      platform: event.platform,
      appVersion: event.appVersion,
      buildNumber: event.buildNumber,
      joinedMatchAt: event.occurredAt || Date.now()
    });
  }
  if (event.eventType === "score_screen_shown" && event.matchId && event.occupantId) {
    await env.TELEMETRY_DB.prepare(
      `UPDATE match_participants
       SET reached_score_screen = 1, left_match_at = COALESCE(left_match_at, ?), terminal_reason = COALESCE(terminal_reason, 'finished')
       WHERE match_id = ? AND occupant_id = ?`
    ).bind(event.occurredAt || Date.now(), event.matchId, event.occupantId).run();
    if (event.role === "host") {
      await env.TELEMETRY_DB.prepare(
        `UPDATE matches
         SET score_screen_reached = 1
         WHERE match_id = ?`
      ).bind(event.matchId).run();
    }
  }
  if (event.eventType === "app_exit_intent" && event.matchId && event.occupantId) {
    await updateParticipantTerminal(env, event.matchId, event.occupantId,
      event.role === "host" ? "host_exit" : "app_exit");
  }
}

async function bestEffortTelemetry(env, work) {
  try {
    if (!telemetryEnabled(env) || !env.TELEMETRY_DB) {
      return;
    }
    await work();
  } catch (error) {
    console.error("telemetry failure", error);
  }
}

function telemetryEnabled(env) {
  return env && env.TELEMETRY_ENABLED === "true";
}

async function upsertRoomRecord(env, room) {
  await env.TELEMETRY_DB.prepare(
    `INSERT INTO rooms (
      room_id, room_code, visibility, created_at, closed_at, last_phase, host_occupant_id, max_players, protocol_version, app_version
    ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    ON CONFLICT(room_id) DO UPDATE SET
      room_code=excluded.room_code,
      visibility=excluded.visibility,
      closed_at=excluded.closed_at,
      last_phase=excluded.last_phase,
      host_occupant_id=excluded.host_occupant_id,
      max_players=excluded.max_players,
      protocol_version=excluded.protocol_version,
      app_version=excluded.app_version`
  ).bind(
    room.roomId,
    room.roomCode,
    room.visibility,
    room.createdAt,
    room.phase === "closed" ? Date.now() : null,
    room.phase,
    room.hostOccupantId || null,
    room.maxPlayers,
    room.protocolVersion,
    room.appVersion
  ).run();
}

function buildWorkerEvent(room, occupantId, eventType, payload) {
  return {
    eventId: crypto.randomUUID(),
    occurredAt: Date.now(),
    roomId: room ? room.roomId : null,
    matchId: room ? room.matchId || null : null,
    occupantId: occupantId || null,
    role: room && occupantId ? (occupantId === room.hostOccupantId ? "host" : "client") : null,
    appVersion: room ? room.appVersion : null,
    buildNumber: "worker",
    platform: "worker",
    eventType,
    screenName: null,
    payloadJson: JSON.stringify(payload || {})
  };
}

async function writeTelemetryEvent(env, event) {
  await insertTelemetryEvent(env, event, Date.now());
}

async function recordMatchStart(env, room, hostOccupantId) {
  const startedAt = Date.now();
  await env.TELEMETRY_DB.prepare(
    `INSERT OR REPLACE INTO matches (
      match_id, room_id, host_occupant_id, started_at, ended_at, end_reason, end_source,
      player_count_expected, player_count_started, score_screen_reached, app_version, build_number, protocol_version
    ) VALUES (?, ?, ?, ?, NULL, NULL, NULL, ?, ?, 0, ?, ?, ?)`
  ).bind(
    room.matchId,
    room.roomId,
    hostOccupantId,
    startedAt,
    room.maxPlayers,
    room.players.length,
    room.appVersion,
    "worker",
    room.protocolVersion
  ).run();
  for (const player of room.players) {
    await upsertMatchParticipant(env, {
      matchId: room.matchId,
      occupantId: player.occupantId,
      installId: null,
      sessionId: null,
      role: player.occupantId === room.hostOccupantId ? "host" : "client",
      platform: "unknown",
      appVersion: room.appVersion,
      buildNumber: "unknown",
      joinedMatchAt: startedAt
    });
  }
}

async function recordMatchFinish(env, matchId, endReason, endSource) {
  await env.TELEMETRY_DB.prepare(
    `UPDATE matches
     SET ended_at = COALESCE(ended_at, ?),
         end_reason = COALESCE(end_reason, ?),
         end_source = COALESCE(end_source, ?)
     WHERE match_id = ?`
  ).bind(Date.now(), endReason, endSource, matchId).run();
}

async function upsertMatchParticipant(env, participant) {
  await env.TELEMETRY_DB.prepare(
    `INSERT INTO match_participants (
      match_id, occupant_id, install_id, session_id, role, platform, app_version, build_number,
      joined_match_at, left_match_at, terminal_reason, reached_score_screen
    ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, NULL, NULL, 0)
    ON CONFLICT(match_id, occupant_id) DO UPDATE SET
      install_id = COALESCE(excluded.install_id, match_participants.install_id),
      session_id = COALESCE(excluded.session_id, match_participants.session_id),
      role = COALESCE(excluded.role, match_participants.role),
      platform = CASE WHEN match_participants.platform = 'unknown' THEN excluded.platform ELSE match_participants.platform END,
      app_version = CASE WHEN match_participants.app_version = 'unknown' THEN excluded.app_version ELSE match_participants.app_version END,
      build_number = CASE WHEN match_participants.build_number = 'unknown' THEN excluded.build_number ELSE match_participants.build_number END`
  ).bind(
    participant.matchId,
    participant.occupantId,
    participant.installId,
    participant.sessionId,
    participant.role,
    participant.platform,
    participant.appVersion,
    participant.buildNumber,
    participant.joinedMatchAt
  ).run();
}

async function updateParticipantTerminal(env, matchId, occupantId, terminalReason) {
  await env.TELEMETRY_DB.prepare(
    `UPDATE match_participants
     SET left_match_at = COALESCE(left_match_at, ?),
         terminal_reason = COALESCE(terminal_reason, ?)
     WHERE match_id = ? AND occupant_id = ?`
  ).bind(Date.now(), terminalReason, matchId, occupantId).run();
}

async function createRoom(env, payload, requesterIp) {
  const roomId = crypto.randomUUID();
  const roomCode = await allocateRoomCode(env);
  const roomStub = env.ROOM_OBJECT.get(env.ROOM_OBJECT.idFromName(roomId));
  const response = await roomStub.fetch("https://room/create", {
    method: "POST",
    body: JSON.stringify({
      roomId,
      roomCode,
      visibility: payload.visibility === "private" ? "private" : "public",
      hostName: payload.displayName,
      maxPlayers: payload.maxPlayers || DEFAULT_MAX_PLAYERS,
      protocolVersion: payload.protocolVersion || 1,
      appVersion: payload.appVersion || "1.0.1",
      allowSpectators: payload.allowSpectators !== false,
      publicAddress: requesterIp
    })
  });
  const body = await response.json();
  await syncRoom(env, body.room);
  await registerCode(env, roomCode, roomId);
  await bestEffortTelemetry(env, async () => {
    await upsertRoomRecord(env, body.room);
    await writeTelemetryEvent(env, buildWorkerEvent(body.room, body.occupantId, "room_created", {
      visibility: body.room.visibility
    }));
  });
  return json(body, response.status);
}

async function quickStart(env, payload, request, requesterIp) {
  const directory = env.ROOM_DIRECTORY.get(env.ROOM_DIRECTORY.idFromName("global"));
  const response = await directory.fetch("https://directory/quick-start", {
    method: "POST",
    body: JSON.stringify({
      protocolVersion: payload.protocolVersion || 1,
      appVersion: payload.appVersion || "1.0.1"
    })
  });
  const directoryBody = await response.json();
  if (directoryBody.room) {
    const joinResponse = await roomCommand(env, directoryBody.room.roomId, "/join", {
      displayName: payload.displayName,
      requesterIp
    }, request);
    const joinBody = await joinResponse.clone().json().catch(() => null);
    if (joinResponse.status < 400) {
      return joinResponse;
    }
    const isExpectedMatchmakingConflict = joinResponse.status === 409
      && (joinBody?.error === "room_full"
        || joinBody?.error === "room_not_joinable"
        || joinBody?.error === "room_closed");
    const isExpectedStaleCandidate = joinResponse.status === 404
      && joinBody?.error === "room_not_found";
    if (!isExpectedMatchmakingConflict && !isExpectedStaleCandidate) {
      return joinResponse;
    }
  }
  return createRoom(env, {
    visibility: "public",
    displayName: payload.displayName,
    maxPlayers: payload.maxPlayers || DEFAULT_MAX_PLAYERS,
    protocolVersion: payload.protocolVersion || 1,
    appVersion: payload.appVersion || "1.0.1",
    allowSpectators: true
  }, requesterIp);
}

async function resolveRoomCode(env, roomCode) {
  const directory = env.ROOM_DIRECTORY.get(env.ROOM_DIRECTORY.idFromName("global"));
  const lookupResponse = await directory.fetch(`https://directory/code/${roomCode}`);
  const lookup = await lookupResponse.json();
  if (!lookup.roomId) {
    return json({ ok: false, error: "room_not_found" }, 404);
  }
  return fetchRoom(env, lookup.roomId);
}

async function fetchRoom(env, roomId) {
  const roomStub = env.ROOM_OBJECT.get(env.ROOM_OBJECT.idFromName(roomId));
  const response = await roomStub.fetch("https://room/summary");
  const body = await response.json();
  return json(body, response.status);
}

async function roomCommand(env, roomId, commandPath, payload, request) {
  const roomStub = env.ROOM_OBJECT.get(env.ROOM_OBJECT.idFromName(roomId));
  const response = await roomStub.fetch(`https://room${commandPath}`, {
    method: "POST",
    body: JSON.stringify(payload)
  });
  const body = await response.json();
  await bestEffortTelemetry(env, async () => {
    if (body.room) {
      await upsertRoomRecord(env, body.room);
    }
    await instrumentRoomCommand(env, roomId, commandPath, payload, body, response.status);
  });
  if (body.room && shouldSyncRoomCommand(commandPath)) {
    if (body.room.phase === "closed") {
      await removeRoom(env, body.room);
    } else {
      await syncRoom(env, body.room);
    }
  }
  return json(body, response.status);
}

function shouldSyncRoomCommand(commandPath) {
  return commandPath !== "/poll" && commandPath !== "/connect-info";
}

async function instrumentRoomCommand(env, roomId, commandPath, payload, body, statusCode) {
  if (commandPath === "/poll" && statusCode === 404 && body.error === "occupant_not_found") {
    await writeTelemetryEvent(env, {
      eventId: crypto.randomUUID(),
      occurredAt: Date.now(),
      roomId,
      occupantId: payload.occupantId || null,
      role: null,
      platform: "worker",
      appVersion: null,
      buildNumber: "worker",
      eventType: "poll_rejected_occupant_not_found",
      screenName: null,
      payloadJson: JSON.stringify({})
    });
    return;
  }
  if (!body.room) {
    return;
  }
  if (commandPath === "/join" && body.occupantId) {
    await writeTelemetryEvent(env, buildWorkerEvent(body.room, body.occupantId, "room_joined", {
      role: body.role
    }));
    return;
  }
  if (commandPath === "/kick" && payload.targetOccupantId) {
    await writeTelemetryEvent(env, buildWorkerEvent(body.room, payload.targetOccupantId, "room_kicked", {
      byOccupantId: payload.occupantId
    }));
    if (body.room.matchId) {
      await updateParticipantTerminal(env, body.room.matchId, payload.targetOccupantId, "kicked");
    }
    return;
  }
  if (commandPath === "/start" && body.room.matchId) {
    await recordMatchStart(env, body.room, payload.occupantId);
    await writeTelemetryEvent(env, buildWorkerEvent(body.room, payload.occupantId, "match_started", {
      player_count_started: body.room.players.length,
      expected_player_count: body.room.maxPlayers
    }));
    return;
  }
  if (commandPath === "/finish" && body.finishedMatchId) {
    await recordMatchFinish(env, body.finishedMatchId, "finished_normal", "worker_finish");
    await writeTelemetryEvent(env, {
      eventId: crypto.randomUUID(),
      occurredAt: Date.now(),
      roomId: body.room.roomId,
      matchId: body.finishedMatchId,
      occupantId: payload.occupantId,
      role: "host",
      platform: "worker",
      appVersion: body.room.appVersion,
      buildNumber: "worker",
      eventType: "match_finished",
      screenName: null,
      payloadJson: JSON.stringify({ finished_match_id: body.finishedMatchId })
    });
    return;
  }
  if (commandPath === "/leave") {
    await writeTelemetryEvent(env, buildWorkerEvent(body.room, payload.occupantId, "room_left", {
      phase: body.room.phase
    }));
    if (body.hostTransferred) {
      await writeTelemetryEvent(env, buildWorkerEvent(body.room, payload.occupantId, "host_transferred", {
        newHostOccupantId: body.room.hostOccupantId
      }));
    }
    const activeMatchId = body.endedMatchId || (body.room.phase !== "lobby" ? body.room.matchId : null);
    if (activeMatchId) {
      const terminalReason = payload.occupantId === body.room.hostOccupantId
        ? "app_exit"
        : "app_exit";
      await updateParticipantTerminal(env, activeMatchId, payload.occupantId, terminalReason);
    }
    if (body.endedMatchId) {
      await recordMatchFinish(env, body.endedMatchId, "room_closed", "worker_room_close");
    }
    return;
  }
  if (commandPath === "/close") {
    await writeTelemetryEvent(env, buildWorkerEvent(body.room, payload.occupantId, "room_closed", null));
    if (body.endedMatchId) {
      await recordMatchFinish(env, body.endedMatchId, "room_closed", "worker_room_close");
    }
  }
}

function createRoomState(body) {
  const hostOccupantId = crypto.randomUUID();
  const now = Date.now();
  return {
    roomId: body.roomId,
    roomCode: body.roomCode,
    visibility: body.visibility,
    phase: "lobby",
    maxPlayers: body.maxPlayers || DEFAULT_MAX_PLAYERS,
    protocolVersion: body.protocolVersion || 1,
    appVersion: body.appVersion || "1.0.1",
    allowSpectators: body.allowSpectators !== false,
    createdAt: now,
    updatedAt: now,
    lastHeartbeatAt: now,
    kicked: [],
    hostOccupantId,
    hostName: body.hostName,
    matchId: null,
    endpoint: null,
    players: [
      {
        occupantId: hostOccupantId,
        displayName: body.hostName,
        ready: false,
        isHost: true,
        requesterIp: body.publicAddress || null
      }
    ],
    spectators: []
  };
}

function buildRelayUrl(origin, roomId, occupantId) {
  const relayUrl = new URL(`/rooms/${roomId}/relay`, origin);
  relayUrl.searchParams.set("occupantId", occupantId);
  relayUrl.protocol = relayUrl.protocol === "https:" ? "wss:" : "ws:";
  return relayUrl.toString();
}

function joinRoom(room, body) {
  if (room.phase === "closed") {
    return { room: ensureFresh(room), occupantId: null, role: null };
  }
  const lockoutMessage = getJoinLockoutMessage(room, body.displayName, body.requesterIp);
  if (lockoutMessage != null) {
    throw new Error(lockoutMessage);
  }
  const occupantId = crypto.randomUUID();
  const player = {
    occupantId,
    displayName: body.displayName,
    ready: false,
    isHost: false,
    requesterIp: body.requesterIp || null
  };

  if (room.phase === "lobby" && room.players.length < room.maxPlayers) {
    room.players.push(player);
    room.updatedAt = Date.now();
    return { room, occupantId, role: "player" };
  }

  const reservedSlots = room.players.length + room.spectators.length;
  if ((room.phase === "starting" || room.phase === "in_game")
      && room.allowSpectators
      && reservedSlots < room.maxPlayers) {
    room.spectators.push({
      occupantId,
      displayName: body.displayName,
      requesterIp: body.requesterIp || null
    });
    room.updatedAt = Date.now();
    return { room, occupantId, role: "spectator" };
  }

  throw new Error(reservedSlots >= room.maxPlayers ? "room_full" : "room_not_joinable");
}

function removeOccupant(room, occupantId) {
  room.players = room.players.filter((player) => player.occupantId !== occupantId);
  room.spectators = room.spectators.filter((spectator) => spectator.occupantId !== occupantId);
}

function registerKick(room, occupant) {
  pruneExpiredKicks(room);
  room.kicked.push({
    displayName: normalizeName(occupant.displayName),
    requesterIp: occupant.requesterIp || null,
    expiresAt: Date.now() + KICK_LOCKOUT_MS
  });
}

function getJoinLockoutMessage(room, displayName, requesterIp) {
  pruneExpiredKicks(room);
  const normalizedName = normalizeName(displayName);
  const banned = room.kicked.find((entry) => {
    if (entry.expiresAt <= Date.now()) {
      return false;
    }
    if (requesterIp && entry.requesterIp && entry.requesterIp === requesterIp) {
      return true;
    }
    return normalizedName.length > 0 && entry.displayName === normalizedName;
  });
  return banned ? "You were kicked from this room. Try again later." : null;
}

function normalizeName(displayName) {
  return (displayName || "").trim().toLowerCase();
}

function pruneExpiredKicks(room) {
  if (!room.kicked) {
    room.kicked = [];
    return;
  }
  const now = Date.now();
  room.kicked = room.kicked.filter((entry) => entry.expiresAt > now);
}

function promoteSpectators(room) {
  while (room.players.length < room.maxPlayers && room.spectators.length > 0) {
    const spectator = room.spectators.shift();
    room.players.push({
      occupantId: spectator.occupantId,
      displayName: spectator.displayName,
      ready: false,
      isHost: false
    });
  }
}

function transferLobbyHost(room) {
  promoteSpectators(room);
  if (room.players.length === 0) {
    room.phase = "closed";
    room.hostOccupantId = null;
    room.hostName = null;
    return;
  }
  const nextHost = room.players[0];
  room.hostOccupantId = nextHost.occupantId;
  room.hostName = nextHost.displayName;
  for (const player of room.players) {
    player.isHost = player.occupantId === room.hostOccupantId;
    player.ready = false;
  }
}

function assertHost(room, occupantId) {
  if (room.hostOccupantId !== occupantId) {
    throw new Error("host_only");
  }
}

function findOccupant(room, occupantId) {
  if (!room || !occupantId) {
    return null;
  }
  return room.players.find((player) => player.occupantId === occupantId)
    || room.spectators.find((spectator) => spectator.occupantId === occupantId)
    || null;
}

function ensureFresh(room) {
  pruneExpiredKicks(room);
  const now = Date.now();
  const lastActiveAt = Math.max(room.updatedAt || 0, room.lastHeartbeatAt || 0, room.createdAt || 0);
  if (room.phase !== "closed" && now - lastActiveAt > ROOM_TTL_MS) {
    room.phase = "closed";
    room.updatedAt = now;
  }
  return room;
}

function toPublicSummary(room) {
  return {
    roomId: room.roomId,
    roomCode: room.roomCode,
    visibility: room.visibility,
    phase: room.phase,
    maxPlayers: room.maxPlayers,
    playerCount: room.players.length,
    spectatorCount: room.spectators.length,
    hostName: room.hostName,
    protocolVersion: room.protocolVersion,
    appVersion: room.appVersion,
    updatedAt: room.updatedAt,
    lastHeartbeatAt: room.lastHeartbeatAt
  };
}

function pruneDirectory(directory) {
  const now = Date.now();
  for (const [roomId, room] of Object.entries(directory.publicRooms)) {
    if (now - room.updatedAt > ROOM_TTL_MS) {
      delete directory.publicRooms[roomId];
    }
  }
}

async function allocateRoomCode(env) {
  const directory = env.ROOM_DIRECTORY.get(env.ROOM_DIRECTORY.idFromName("global"));
  const response = await directory.fetch("https://directory/allocate-code", {
    method: "POST"
  });
  const body = await response.json();
  return body.roomCode;
}

async function registerCode(env, roomCode, roomId) {
  const directory = env.ROOM_DIRECTORY.get(env.ROOM_DIRECTORY.idFromName("global"));
  await directory.fetch("https://directory/register-code", {
    method: "POST",
    body: JSON.stringify({ roomCode, roomId })
  });
}

async function syncRoom(env, room) {
  const directory = env.ROOM_DIRECTORY.get(env.ROOM_DIRECTORY.idFromName("global"));
  await directory.fetch("https://directory/sync-room", {
    method: "POST",
    body: JSON.stringify({ room })
  });
}

async function removeRoom(env, room) {
  const directory = env.ROOM_DIRECTORY.get(env.ROOM_DIRECTORY.idFromName("global"));
  await directory.fetch("https://directory/remove-room", {
    method: "POST",
    body: JSON.stringify({ roomId: room.roomId, roomCode: room.roomCode })
  });
}

function randomCode() {
  const alphabet = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
  let output = "";
  for (let i = 0; i < 6; i += 1) {
    output += alphabet[Math.floor(Math.random() * alphabet.length)];
  }
  return output;
}

function getRequesterIp(request) {
  return request.headers.get("CF-Connecting-IP") || null;
}

function json(payload, status = 200) {
  return cors(
    new Response(JSON.stringify(payload), {
      status,
      headers: {
        "content-type": "application/json; charset=utf-8"
      }
    })
  );
}

function cors(response) {
  response.headers.set("access-control-allow-origin", "*");
  response.headers.set("access-control-allow-methods", "GET,POST,OPTIONS");
  response.headers.set("access-control-allow-headers", "content-type");
  return response;
}
