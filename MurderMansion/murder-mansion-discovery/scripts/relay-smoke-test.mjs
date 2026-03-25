const DEFAULT_BASE_URL = "https://murder-mansion-discovery.jxlee92.workers.dev";
const baseUrl = (process.env.MURDER_MANSION_DISCOVERY_URL || DEFAULT_BASE_URL).replace(/\/+$/, "");
const protocolVersion = 1;
const appVersion = "1.0.1";
const timeoutMs = 8000;

let roomId = null;
let hostOccupantId = null;

function log(message) {
  process.stdout.write(`[relay-smoke] ${message}\n`);
}

function randomName(prefix) {
  return `${prefix}-${Math.random().toString(36).slice(2, 8)}`;
}

async function api(method, path, body) {
  const response = await fetch(`${baseUrl}${path}`, {
    method,
    headers: {
      "content-type": "application/json",
      "accept": "application/json"
    },
    body: body === undefined ? undefined : JSON.stringify(body)
  });

  const text = await response.text();
  let json;
  try {
    json = text ? JSON.parse(text) : {};
  } catch (error) {
    throw new Error(`Invalid JSON from ${method} ${path}: ${text}`);
  }

  if (!response.ok || json.ok === false) {
    throw new Error(`${method} ${path} failed: ${JSON.stringify(json)}`);
  }

  return json;
}

function assert(condition, message) {
  if (!condition) {
    throw new Error(message);
  }
}

function withTimeout(promise, message, ms = timeoutMs) {
  return Promise.race([
    promise,
    new Promise((_, reject) => {
      setTimeout(() => reject(new Error(message)), ms);
    })
  ]);
}

class RelayTestSocket {
  constructor(name, url) {
    this.name = name;
    this.url = url;
    this.socket = null;
    this.messages = [];
    this.waiters = [];
    this.closePromise = new Promise((resolve) => {
      this.resolveClosed = resolve;
    });
  }

  async connect() {
    await withTimeout(new Promise((resolve, reject) => {
      this.socket = new WebSocket(this.url);
      this.socket.addEventListener("open", () => resolve(), { once: true });
      this.socket.addEventListener("message", (event) => {
        const payload = JSON.parse(String(event.data));
        this.messages.push(payload);
        this.flushWaiters();
      });
      this.socket.addEventListener("error", () => {
        reject(new Error(`${this.name} websocket error`));
      }, { once: true });
      this.socket.addEventListener("close", (event) => {
        this.resolveClosed(event);
        this.flushWaiters();
      }, { once: true });
    }), `Timed out connecting ${this.name}`);
  }

  flushWaiters() {
    const remaining = [];
    for (const waiter of this.waiters) {
      const match = this.messages.find(waiter.predicate);
      if (match) {
        waiter.resolve(match);
      } else {
        remaining.push(waiter);
      }
    }
    this.waiters = remaining;
  }

  waitFor(predicate, description, ms = timeoutMs) {
    const existing = this.messages.find(predicate);
    if (existing) {
      return Promise.resolve(existing);
    }
    return withTimeout(new Promise((resolve) => {
      this.waiters.push({ predicate, resolve });
    }), `${this.name} timed out waiting for ${description}`, ms);
  }

  sendJson(payload) {
    this.socket.send(JSON.stringify(payload));
  }

  async close(code = 1000, reason = "client close", awaitClosed = true) {
    if (!this.socket || this.socket.readyState >= WebSocket.CLOSING) {
      return;
    }
    this.socket.close(code, reason);
    if (awaitClosed) {
      await withTimeout(this.closePromise, `${this.name} timed out closing`);
    }
  }
}

async function cleanup() {
  if (!roomId || !hostOccupantId) {
    return;
  }
  try {
    await api("POST", `/rooms/${roomId}/close`, { occupantId: hostOccupantId });
  } catch (error) {
    log(`cleanup warning: ${error.message}`);
  }
}

async function main() {
  log(`using ${baseUrl}`);

  const hostName = randomName("SmokeHost");
  const guestName = randomName("SmokeGuest");

  const create = await api("POST", "/rooms", {
    visibility: "private",
    displayName: hostName,
    maxPlayers: 4,
    protocolVersion,
    appVersion
  });
  roomId = create.roomId;
  hostOccupantId = create.occupantId;
  assert(roomId, "room creation did not return roomId");
  assert(hostOccupantId, "room creation did not return host occupant");
  log(`created room ${roomId}`);

  const join = await api("POST", `/rooms/${roomId}/join`, {
    displayName: guestName
  });
  const guestOccupantId = join.occupantId;
  assert(guestOccupantId, "join did not return guest occupant");

  await api("POST", `/rooms/${roomId}/ready`, { occupantId: hostOccupantId, ready: true });
  await api("POST", `/rooms/${roomId}/ready`, { occupantId: guestOccupantId, ready: true });
  await api("POST", `/rooms/${roomId}/start`, {
    occupantId: hostOccupantId,
    localAddress: "relay",
    port: 0
  });

  const hostConnect = await api("POST", `/rooms/${roomId}/connect-info`, { occupantId: hostOccupantId });
  const guestConnect = await api("POST", `/rooms/${roomId}/connect-info`, { occupantId: guestOccupantId });
  assert(hostConnect.relayUrl?.startsWith("ws"), "host relayUrl was not ws/wss");
  assert(guestConnect.relayUrl?.startsWith("ws"), "guest relayUrl was not ws/wss");

  const hostSocket = new RelayTestSocket("host", hostConnect.relayUrl);
  const guestSocket = new RelayTestSocket("guest", guestConnect.relayUrl);

  try {
    await hostSocket.connect();
    const hostConnected = await hostSocket.waitFor(
      (message) => message.type === "connected" && message.isHost === true,
      "host connected"
    );
    assert(hostConnected.occupantId === hostOccupantId, "host connected message occupant mismatch");

    await guestSocket.connect();
    const guestConnected = await guestSocket.waitFor(
      (message) => message.type === "connected" && message.isHost === false,
      "guest connected"
    );
    assert(guestConnected.occupantId === guestOccupantId, "guest connected message occupant mismatch");

    const peerConnected = await hostSocket.waitFor(
      (message) => message.type === "peer_connected" && message.occupantId === guestOccupantId,
      "peer connected event"
    );
    assert(peerConnected.occupantId === guestOccupantId, "peer_connected occupant mismatch");

    guestSocket.sendJson({
      type: "payload",
      roomId,
      occupantId: guestOccupantId,
      payload: "smoke_client_to_host"
    });
    const hostPayload = await hostSocket.waitFor(
      (message) =>
        message.type === "payload"
        && message.fromOccupantId === guestOccupantId
        && message.payload === "smoke_client_to_host",
      "client payload at host"
    );
    assert(hostPayload.payload === "smoke_client_to_host", "host payload mismatch");

    hostSocket.sendJson({
      type: "payload",
      roomId,
      occupantId: hostOccupantId,
      targetOccupantId: guestOccupantId,
      payload: "smoke_host_to_client"
    });
    const guestPayload = await guestSocket.waitFor(
      (message) =>
        message.type === "payload"
        && message.fromOccupantId === hostOccupantId
        && message.payload === "smoke_host_to_client",
      "host payload at guest"
    );
    assert(guestPayload.payload === "smoke_host_to_client", "guest payload mismatch");

    await guestSocket.close(1000, "client close", false);
    const peerDisconnected = await hostSocket.waitFor(
      (message) => message.type === "peer_disconnected" && message.occupantId === guestOccupantId,
      "peer disconnected event"
    );
    assert(peerDisconnected.occupantId === guestOccupantId, "peer_disconnected occupant mismatch");

    log("relay smoke test passed");
  } finally {
    try {
      await hostSocket.close(1000, "host cleanup", false);
    } catch (error) {
      log(`host close warning: ${error.message}`);
    }
  }
}

try {
  await main();
  await cleanup();
  process.exit(0);
} catch (error) {
  await cleanup();
  process.stderr.write(`[relay-smoke] FAILED: ${error.stack || error.message}\n`);
  process.exit(1);
}
