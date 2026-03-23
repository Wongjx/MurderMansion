# Murder Mansion Multiplayer Architecture Proposal

Date: 2026-03-18

## Executive Summary

This version of the plan assumes:

1. no web target
2. no hosted backend you have to maintain
3. a listen-server model for native clients only

Under those constraints, online multiplayer is still possible for:

- Android
- iOS
- desktop

The recommended architecture is:

1. native-only clients
2. host-authoritative listen server
3. direct-connect or LAN discovery
4. platform-neutral room/session flow
5. rewritten transport and protocol, while reusing the current gameplay authority model

The risky part is no longer "need a backend." The risky part is public internet connectivity between native devices without a relay. So this plan is viable, but it should be framed as:

- strong fit for LAN and private matches
- acceptable fit for desktop-hosted internet matches
- weaker fit for phone-hosted public internet matches

## Why The Current Online Model Needs A Rewrite

### Current Design

The current online flow is:

1. Android Google Play Games creates a room / invite flow.
2. One player becomes the host.
3. That host opens a raw Java `ServerSocket`: [MMServer.java](/Users/junxianglee/Documents/GitHub/MurderMansion/MurderMansion/core/src/com/jkjk/Host/MMServer.java#L354)
4. The host discovers its local IPv4 address: [MMServer.java](/Users/junxianglee/Documents/GitHub/MurderMansion/MurderMansion/core/src/com/jkjk/Host/MMServer.java#L404)
5. Google Play room messages are used to broadcast address/port: [RealTimeCommunication.java](/Users/junxianglee/Documents/GitHub/MurderMansion/MurderMansion/android/src/com/jkjk/MurderMansion/android/RealTimeCommunication.java#L37), [RealTimeCommunication.java](/Users/junxianglee/Documents/GitHub/MurderMansion/MurderMansion/android/src/com/jkjk/MurderMansion/android/RealTimeCommunication.java#L67)
6. All clients connect directly to that player-hosted socket: [MMClient.java](/Users/junxianglee/Documents/GitHub/MurderMansion/MurderMansion/core/src/com/jkjk/GameWorld/MMClient.java#L273)

### Why It Fails As A Modern Native-Only Design As-Is

- Mobile NAT traversal is unreliable.
  - A host device reporting a local or carrier-assigned IP is not a dependable internet-facing multiplayer design.

- The room/lobby layer is Android-specific.
  - The current entry path depends on old Google Play Games room APIs: [AndroidLauncher.java](/Users/junxianglee/Documents/GitHub/MurderMansion/MurderMansion/android/src/com/jkjk/MurderMansion/android/AndroidLauncher.java#L203), [AndroidLauncher.java](/Users/junxianglee/Documents/GitHub/MurderMansion/MurderMansion/android/src/com/jkjk/MurderMansion/android/AndroidLauncher.java#L240), [AndroidLauncher.java](/Users/junxianglee/Documents/GitHub/MurderMansion/MurderMansion/android/src/com/jkjk/MurderMansion/android/AndroidLauncher.java#L327)
  - iOS and desktop need a platform-neutral replacement even if you keep listen-server hosting.

- The transport and gameplay are tightly coupled.
  - `MMClient` both talks over sockets and applies gameplay events.
  - `MMServer` both accepts sockets and runs game rules.

- Disconnect handling is fragile.
  - The current fanout logic assumes observer list index equals player id: [PlayerStatuses.java](/Users/junxianglee/Documents/GitHub/MurderMansion/MurderMansion/core/src/com/jkjk/Host/PlayerStatuses.java#L195), [ObjectLocations.java](/Users/junxianglee/Documents/GitHub/MurderMansion/MurderMansion/core/src/com/jkjk/Host/ObjectLocations.java#L178), [GameStatus.java](/Users/junxianglee/Documents/GitHub/MurderMansion/MurderMansion/core/src/com/jkjk/Host/GameStatus.java#L49)
  - Once players leave and observers are removed, “skip the origin by index” becomes unsafe.

- Some state containers are not network-safe long term.
  - `SpawnBuffer` keys objects by `x * y` float products: [SpawnBuffer.java](/Users/junxianglee/Documents/GitHub/MurderMansion/MurderMansion/core/src/com/jkjk/Host/Helpers/SpawnBuffer.java#L15), [SpawnBuffer.java](/Users/junxianglee/Documents/GitHub/MurderMansion/MurderMansion/core/src/com/jkjk/Host/Helpers/SpawnBuffer.java#L23)
  - That is acceptable in a student project, but too brittle for a production protocol where objects should have stable ids.

## What You Can Reuse

Quite a lot of the gameplay-side logic is worth keeping.

### Reusable Domain Logic

- Match state authority concept in `MMServer`.
  - The server already owns spawning, win conditions, lightning timing, obstacle removal, and game start sequencing: [MMServer.java](/Users/junxianglee/Documents/GitHub/MurderMansion/MurderMansion/core/src/com/jkjk/Host/MMServer.java#L124), [MMServer.java](/Users/junxianglee/Documents/GitHub/MurderMansion/MurderMansion/core/src/com/jkjk/Host/MMServer.java#L152), [MMServer.java](/Users/junxianglee/Documents/GitHub/MurderMansion/MurderMansion/core/src/com/jkjk/Host/MMServer.java#L186)

- Player state model in `PlayerStatuses`.
  - Alive/stun/type/position/angle/velocity/safe-region are already centralized: [PlayerStatuses.java](/Users/junxianglee/Documents/GitHub/MurderMansion/MurderMansion/core/src/com/jkjk/Host/PlayerStatuses.java#L10), [PlayerStatuses.java](/Users/junxianglee/Documents/GitHub/MurderMansion/MurderMansion/core/src/com/jkjk/Host/PlayerStatuses.java#L174)

- Spawn and consumable logic in `ObjectLocations`.
  - Items, weapons, weapon parts, traps, and respawn behaviors are already described there: [ObjectLocations.java](/Users/junxianglee/Documents/GitHub/MurderMansion/MurderMansion/core/src/com/jkjk/Host/ObjectLocations.java#L46), [ObjectLocations.java](/Users/junxianglee/Documents/GitHub/MurderMansion/MurderMansion/core/src/com/jkjk/Host/ObjectLocations.java#L89)

- World rendering and client-side presentation in `GameWorld`, `GameRenderer`, `HudRenderer`.
  - Those can continue to exist on clients as rendering/prediction layers.

- Most message semantics.
  - The current protocol already distinguishes:
    - player movement
    - status updates
    - item/weapon/trap events
    - room start / win / lightning / obstacle events
  - Those event categories are useful even if the wire format changes.

### Reuse Strategy

Do not reuse the current classes as-is.

Reuse them by extracting:

- rules
- timers
- spawn policies
- win conditions
- state transition logic

into backend-safe domain classes.

## What Must Be Rewritten

### 1. Session / Matchmaking / Invites

These classes are Android-specific and should be replaced:

- [AndroidLauncher.java](/Users/junxianglee/Documents/GitHub/MurderMansion/MurderMansion/android/src/com/jkjk/MurderMansion/android/AndroidLauncher.java)
- [GPSListeners.java](/Users/junxianglee/Documents/GitHub/MurderMansion/MurderMansion/android/src/com/jkjk/MurderMansion/android/GPSListeners.java)
- [RealTimeCommunication.java](/Users/junxianglee/Documents/GitHub/MurderMansion/MurderMansion/android/src/com/jkjk/MurderMansion/android/RealTimeCommunication.java)
- [ActionResolver.java](/Users/junxianglee/Documents/GitHub/MurderMansion/MurderMansion/core/src/com/jkjk/MMHelpers/ActionResolver.java)
- [MultiplayerSessionInfo.java](/Users/junxianglee/Documents/GitHub/MurderMansion/MurderMansion/core/src/com/jkjk/MMHelpers/MultiplayerSessionInfo.java)

These should become platform-neutral concepts:

- auth session
- party / invite
- matchmaking ticket
- room id
- player presence
- reconnect token

### 2. Transport Layer

These parts should be rewritten behind an interface:

- socket connect / accept
- heartbeat
- reconnect
- serialization
- server fanout
- disconnect handling

The current `MMClient` and `MMServer` combine too many concerns and should be split into:

- client netcode layer
- server room runtime
- shared message schema
- shared gameplay state model

### 3. Protocol Format

The current underscore-delimited string protocol is too fragile:

- no versioning
- no schema validation
- no stable entity ids
- string-splitting everywhere
- hard to evolve safely

Replace it with:

- JSON if you want speed of iteration first
- MessagePack or protobuf if you want a stronger binary protocol later

For a comeback release, JSON over WebSocket is acceptable if you keep payloads compact.

## Recommended Target Architecture

## Architecture Choice

I recommend:

- host-authoritative listen-server simulation
- native client transport layer
- direct connect and LAN-first room discovery
- one shared protocol across Android, iOS, and desktop

This is the lowest-maintenance architecture that still gives you online multiplayer without running your own service.

## High-Level Topology

Clients:

- Android libGDX app
- iOS libGDX app
- Desktop libGDX app

Host:

- One of the native clients runs the authoritative room runtime locally.

Peers:

- Other native clients connect directly to the host over the game transport.

Optional local-network helpers:

- LAN broadcast / mDNS discovery
- manual host IP entry
- host code display for user-facing join flow

## Room Model

Each match is a room with:

- `room_id`
- room phase: lobby, countdown, playing, finished
- roster and player presence
- authoritative match state
- server tick loop

Each host runtime owns:

- player positions
- player role/type state
- item/trap/weapon spawns
- timers and environmental events
- win conditions

Clients send:

- join
- ready
- movement input / intent
- action intent: use item / use weapon / use ability
- interact / pickup / place trap

Host sends:

- room snapshot on join
- periodic state deltas
- authoritative events
- win / disconnect / reconnect outcomes

## Why Host-Authoritative Is The Right Fit Here

Murder Mansion is not a twitch shooter with 60 Hz rollback requirements.

The existing code already updates at a low rate:

- `UPDATES_PER_SEC = 5` in `MMClient`: [MMClient.java](/Users/junxianglee/Documents/GitHub/MurderMansion/MurderMansion/core/src/com/jkjk/GameWorld/MMClient.java#L80)

That means:

- you do not need a heavyweight backend architecture
- you do not need rollback netcode
- a room tick of roughly `10-20 Hz` with client interpolation is a realistic target
- host-authoritative simulation is close to what the repo already does conceptually

## Recommended Client/Server Split

### Shared Game Model

Create a new shared module for:

- enums
- ids
- DTOs / schemas
- room state
- event types
- serialization helpers

Suggested structure:

- `shared-protocol`
- `host-game`
- `client-net`
- `client-game`

### Client Responsibilities

Clients should:

- authenticate
- join room
- send intents
- render local predicted movement
- interpolate remote players
- apply authoritative corrections
- render UI, SFX, and animations

Clients should not:

- decide who won
- own spawn timing
- own room lifecycle
- own authoritative item or trap state

### Host Responsibilities

Host room runtime should:

- validate every input
- clamp movement and ability use
- own all spawns and timers
- resolve collisions / pickups / damage / deaths
- emit canonical state
- handle disconnects and simple reconnect windows

## Recommended Wire Protocol

Use message envelopes like:

```json
{
  "type": "player_input",
  "seq": 182,
  "client_time": 1710740032,
  "payload": {
    "move_x": 1,
    "move_y": 0,
    "angle": 3.14,
    "buttons": ["use_weapon"]
  }
}
```

And host events like:

```json
{
  "type": "state_delta",
  "server_tick": 9012,
  "payload": {
    "players": [
      { "id": "p1", "x": 812.4, "y": 503.1, "angle": 2.9, "role": "murderer", "alive": true }
    ],
    "events": [
      { "type": "weapon_consumed", "entity_id": "weapon_17" },
      { "type": "lightning_strike" }
    ]
  }
}
```

Core changes versus today:

- stable object ids instead of coordinate-derived keys
- typed messages
- explicit host tick / sequence numbers
- versionable schema

## Recommended Native Transport Options

You have two realistic no-backend transport directions.

### Option A: Keep TCP, But Modernize Everything Around It

This means:

- host runs a TCP listener
- peers connect directly
- room/session UI is rebuilt without Google Play realtime rooms
- protocol is rewritten

Pros:

- closest to what you already have
- easiest to port gameplay authority logic
- simplest to reason about for LAN

Cons:

- weakest internet-hosted experience
- mobile host connectivity remains fragile
- no browser path later without additional architecture

### Option B: Native P2P / Realtime Library For Session And Transport

This means using a cross-platform native networking layer for:

- discovery
- host/client sockets
- possibly NAT traversal helpers if available

Pros:

- cleaner than hand-maintained raw socket code
- may improve session robustness

Cons:

- still no magic fix for public internet mobile NAT
- still needs a room/session rewrite above the transport

## My Recommendation

For this game, I recommend:

1. host-authoritative listen server
2. native-only clients
3. LAN and private-room play first
4. desktop-hosted internet matches as the first true online target
5. mobile-hosted internet matches only as a best-effort feature, not the promise

I would not recommend promising:

- public automatch
- frictionless phone-hosted internet play
- browser participation

## Platform Client Strategy

### Android / iOS / Desktop

Keep libGDX as the gameplay/rendering client.

Add a platform-neutral client networking interface, for example:

```java
interface RealtimeSession {
    void connect(String authToken);
    void joinRoom(String roomId);
    void send(ClientMessage message);
    void close();
}
```

Then provide native implementations for:

- Android / desktop JVM transport
- iOS native transport bridge

## Migration Map: What To Keep vs Replace

### Keep Conceptually

- `MMServer` rules and timers
- `PlayerStatuses` state model
- `ObjectLocations` spawn logic
- win condition logic
- game event categories

### Replace Entirely

- Android room/invite code
- Google Play Games realtime flow
- the current direct `ServerSocket` / `Socket` glue layer
- `RealTimeCommunication`
- raw underscore string protocol
- observer-based socket fanout

### Rewrite As Shared Modules

- message schemas
- room state DTOs
- entity ids
- connection lifecycle
- snapshots and deltas

## Suggested Delivery Plan

### Phase 1: Local Authoritative Prototype

Goal:

- stand up a host runtime locally
- connect two desktop clients
- prove movement, spawning, and win conditions

Scope:

- manual host/join
- no platform invites
- LAN or localhost only

### Phase 2: Cross-Platform Client Integration

Goal:

- connect Android and desktop
- then iOS

Scope:

- room join
- state sync
- disconnect/reconnect

### Phase 3: Product Features

Goal:

- private room codes or join strings
- LAN room discovery
- desktop-hosted internet join flow
- optional platform-specific social surface integrations

### Phase 4: Hardening

Goal:

- reconnect resilience
- better disconnect messaging
- NAT/host diagnostics
- host migration decision or explicit no-host-migration policy

## Recommended Minimum Viable Online Comeback

For the first online return, I would keep scope tight:

- private rooms first
- host code or direct join first
- 2-6 players max
- no public automatch at launch
- host-authoritative match runtime
- reconnect window of 30-60 seconds

This dramatically lowers risk compared with trying to revive full public quick-match immediately.

## Practical Product Recommendation

If you want the highest chance of shipping native online play without hosted infrastructure:

1. make LAN play excellent
2. make desktop-hosted internet matches acceptable
3. treat mobile-hosted internet play as experimental or unsupported for some networks

That is the most honest product shape for a no-backend listen-server release.

## Bottom Line

The online version is worth reviving without hosted infrastructure, but only if you treat it as:

- a native-only listen-server rebuild
- reusing your gameplay rules
- replacing the old Google Play room flow
- rewriting the transport and protocol cleanly

The best low-maintenance architecture for Murder Mansion is:

- host-authoritative native listen server
- one protocol for Android/iOS/desktop
- LAN and private matches first
- no web target in the online architecture

## Sources

- libGDX project generation docs: https://libgdx.com/wiki/start/project-generation
