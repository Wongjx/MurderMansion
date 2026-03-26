const ROOM_TTL_MS = 45_000;
const DEFAULT_MAX_PLAYERS = 4;
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
      await this.state.storage.put("directory", directory);
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
      await this.state.storage.put("directory", directory);
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
      await this.state.storage.put("directory", directory);
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
      room = ensureFresh(room);
    }

    if (method === "POST" && path === "/create") {
      const body = await request.json();
      room = createRoomState(body);
      await this.state.storage.put("room", room);
      return json({ ok: true, roomId: room.roomId, roomCode: room.roomCode, occupantId: room.hostOccupantId, role: "player", room });
    }

    if (method === "GET" && path === "/summary") {
      return json({ ok: true, room });
    }

    if (method === "POST" && path === "/poll") {
      const body = await request.json();
      const occupant = findOccupant(room, body.occupantId);
      if (!occupant) {
        return json({ ok: false, error: "occupant_not_found" }, 404);
      }
      if (room.phase !== "closed") {
        room.lastHeartbeatAt = Date.now();
        room.updatedAt = Date.now();
        await this.state.storage.put("room", room);
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
      room.updatedAt = Date.now();
      await this.state.storage.put("room", room);
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
      await this.state.storage.put("room", joinResult.room);
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
      await this.state.storage.put("room", room);
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
      await this.state.storage.put("room", room);
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
      room.endpoint = {
        publicAddress: body.publicAddress || null,
        localAddress: body.localAddress || null,
        port: body.port || null
      };
      room.lastHeartbeatAt = Date.now();
      room.updatedAt = Date.now();
      await this.state.storage.put("room", room);
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
      await this.state.storage.put("room", room);
      return json({ ok: true, room });
    }

    if (method === "POST" && path === "/finish") {
      const body = await request.json();
      assertHost(room, body.occupantId);
      room.phase = "lobby";
      room.endpoint = null;
      room.players.forEach((player) => {
        player.ready = false;
      });
      promoteSpectators(room);
      room.updatedAt = Date.now();
      await this.state.storage.put("room", room);
      return json({ ok: true, room });
    }

    if (method === "POST" && path === "/leave") {
      const body = await request.json();
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
      await this.state.storage.put("room", room);
      if (room.phase === "closed") {
        this.closeActiveSockets("room_closed", 1000, "Room closed");
      }
      return json({ ok: true, room });
    }

    if (method === "POST" && path === "/close") {
      const body = await request.json();
      assertHost(room, body.occupantId);
      room.phase = "closed";
      room.updatedAt = Date.now();
      await this.state.storage.put("room", room);
      this.closeActiveSockets("room_closed", 1000, "Room closed");
      return json({ ok: true, room });
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
    room = ensureFresh(room);
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
    serverSocket.addEventListener("message", (event) => {
      this.handleRelayMessage(occupantId, String(event.data));
    });
    serverSocket.addEventListener("close", async () => {
      await this.handleSocketClose(room, occupantId);
    });
    serverSocket.addEventListener("error", async () => {
      await this.handleSocketClose(room, occupantId);
    });

    room.lastHeartbeatAt = Date.now();
    room.updatedAt = Date.now();
    await this.state.storage.put("room", room);

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
    const room = ensureFresh(await this.state.storage.get("room"));
    if (!room || room.phase === "closed") {
      return;
    }
    room.lastHeartbeatAt = Date.now();
    room.updatedAt = Date.now();
    await this.state.storage.put("room", room);

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

    room = ensureFresh(await this.state.storage.get("room"));
    if (!room) {
      return;
    }
    room.lastHeartbeatAt = Date.now();
    room.updatedAt = Date.now();

    if (occupantId === room.hostOccupantId) {
      room.phase = "closed";
      await this.state.storage.put("room", room);
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
    room.players = room.players.filter((player) => player.occupantId !== occupantId);
    room.spectators = room.spectators.filter((spectator) => spectator.occupantId !== occupantId);
    if (room.phase === "lobby") {
      promoteSpectators(room);
    }
    await this.state.storage.put("room", room);
  }

  sendToOccupant(occupantId, payload) {
    const socket = this.connections.get(occupantId);
    if (!socket) {
      return;
    }
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
  if (body.room) {
    await syncRoom(env, body.room);
  }
  return json(body, response.status);
}

async function roomCommand(env, roomId, commandPath, payload, request) {
  const roomStub = env.ROOM_OBJECT.get(env.ROOM_OBJECT.idFromName(roomId));
  const response = await roomStub.fetch(`https://room${commandPath}`, {
    method: "POST",
    body: JSON.stringify(payload)
  });
  const body = await response.json();
  if (body.room) {
    if (body.room.phase === "closed") {
      await removeRoom(env, body.room);
    } else {
      await syncRoom(env, body.room);
    }
  }
  return json(body, response.status);
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
  if (room.phase !== "closed" && now - room.lastHeartbeatAt > ROOM_TTL_MS) {
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
    if (now - room.lastHeartbeatAt > ROOM_TTL_MS) {
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
