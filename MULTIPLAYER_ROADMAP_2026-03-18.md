# Murder Mansion Multiplayer Roadmap

Date: 2026-03-18

## Goal

Lay out a progression from:

- zero-backend native listen-server multiplayer

to:

- global matchmaking and broader online reach

without throwing away the core architecture each time.

This roadmap assumes:

- no web target
- Android, iOS, desktop only
- libGDX clients
- host-authoritative game logic

## Pre-Phase 0: Build Modernization

Before any multiplayer work, the project needs a modern, runnable foundation.

### Goal

- get the game building again on a current toolchain

### Work

- create or regenerate a modern libGDX project layout
- port the current code/assets into the new project structure
- modernize desktop, Android, and later iOS targets
- fix obvious compile/runtime blockers

### Why This Comes First

Because multiplayer work on top of a dead build is wasted motion.

## Phase 0: Single-Player / Local Playable Recovery

Before Phase A, get a stable non-networked gameplay baseline.

### Goal

- one clean playable path from menu to gameplay to game over

### Likely Scope

- desktop first
- tutorial/local mode first
- remove or stub obsolete Google Play room/login flows
- fix Java correctness issues that affect runtime behavior
- stabilize screen flow, controls, collisions, and game state transitions

### Why This Comes Before Multiplayer

Because it gives you:

- a known-good gameplay baseline
- a clean host-runtime reference for later extraction
- a way to debug game rules without network noise

If this phase is unstable, multiplayer debugging will be much slower and less trustworthy.

## Core Principle

The part you want to stabilize first is not "matchmaking."

It is:

- host/client simulation
- room/session flow
- typed protocol
- disconnect/reconnect behavior
- cross-platform native compatibility

If those are built cleanly, later service layers can sit on top of them.

## Phase A: Zero-Backend Listen Server

### Product Shape

- native clients only
- LAN play
- private direct-connect play
- host one match from a player device or desktop
- no public matchmaking
- no relay

### Features

- host game
- join by IP / host string
- local network discovery if feasible
- ready room
- start match
- reconnect window
- clear host-disconnect behavior

### What You Build

- new native transport/session abstraction
- rewritten host runtime
- rewritten client network layer
- typed protocol with stable entity ids
- join/ready/start lifecycle
- diagnostics for connection failures

### What You Do Not Build

- account system
- central room list
- invites service
- public quick match
- NAT traversal service
- relay service

### Why This Phase Matters

This proves the actual game can survive the multiplayer rewrite.

If Phase 0 is unstable, this phase will be much harder. If this phase fails, later matchmaking work would just be polish on a broken foundation.

### Success Criteria

- two desktop clients can complete a match
- desktop host plus Android client works
- Android host plus Android client works on LAN
- disconnects fail cleanly instead of corrupting the room

### Risk Profile

- engineering risk: medium
- infrastructure risk: low
- product reach: narrow

## Phase B: Minimal Service Layer For Discovery

### Product Shape

- still listen-server
- still host-authoritative
- still no dedicated authoritative game backend
- optional lightweight service for room discovery only

### What This Adds

- room directory / room codes
- lightweight identity
- invite links or friend invites
- host public endpoint registration
- optional host capability check

### What This Does Not Add

- relay of gameplay traffic
- authoritative match hosting
- backend-owned game simulation

### Why This Is The Best Next Step

This is the smallest service layer that meaningfully improves usability.

It lets players:

- find each other
- exchange room codes more easily
- avoid manual IP entry
- support broader internet play if direct connectivity works

### What You Build

- tiny service with:
  - room registration
  - room lookup
  - host metadata
  - optional auth/user ids

- client flow changes:
  - create room
  - get room code
  - join room by code
  - fetch host connection info

### What Carries Over From Phase A

- host runtime
- client protocol
- room state model
- join/start flow
- reconnect model

### What Still Limits You

Direct connectivity is still the bottleneck.

If two players cannot reach each other over the internet, matchmaking can succeed but the match can still fail to start.

### Success Criteria

- private internet matches via room code work for a meaningful portion of users
- desktop hosting is reliable
- manual IP entry is mostly replaced

### Risk Profile

- engineering risk: medium
- infrastructure risk: low-to-medium
- product reach: moderate

## Phase C: Global Matchmaking Without Full Backend Rewrite

### Product Shape

- public quick match
- private matches
- host still runs the authoritative simulation
- central service handles matchmaking and connectivity assistance

### What This Requires

- account / skill / matchmaking queue system
- host candidate selection
- region awareness
- host reachability checks
- session quality heuristics
- possibly host migration policy

### Important Constraint

At this phase, you are still limited by direct-connect networking.

So global matchmaking is possible as a product flow, but not guaranteed as a reliable connectivity outcome.

### What You Build

- matchmaking service
- room creation and reservation flow
- candidate host selection
- host quality telemetry
- failover and retry behavior

### What You Still Do Not Have

- guaranteed session establishment
- network relay fallback

### Success Criteria

- players can enter a queue and get matched
- a high enough percentage of matches connect successfully
- failure and retry UX is acceptable

### Risk Profile

- engineering risk: medium-to-high
- infrastructure risk: medium
- product risk: high, because connectivity can still fail even with good matchmaking

## Phase D: Global Matchmaking With Relay Assistance

### Product Shape

- public matchmaking
- private matches
- much higher connection success rate
- host can still be the gameplay authority, but traffic may be assisted by infrastructure

### What This Adds

- NAT traversal helpers and/or relay fallback
- broader compatibility across mobile networks
- better support for phone-hosted internet matches

### What Changes Architecturally

Not the game rules.

The main change is the transport/session layer:

- connection establishment
- fallback routing
- possibly relayed traffic

### What Carries Over

- host-authoritative simulation
- room model
- client protocol
- most gameplay logic

### What New Infrastructure Exists

- signaling and/or relay services
- operational monitoring
- service reliability expectations

This is the point where "no backend" is no longer really true.

### Success Criteria

- public matchmaking has strong connection success
- mobile-hosted internet matches become realistic
- user friction drops materially

### Risk Profile

- engineering risk: high
- infrastructure risk: high
- product reach: high

## What Scales Cleanly Across All Phases

These investments are safe now:

- typed protocol
- stable player/entity ids
- host runtime extraction from current `MMServer`
- client netcode extraction from current `MMClient`
- session abstraction
- room lifecycle model
- reconnect logic

These are not phase-specific. They are the foundation.

## What Does Not Scale Without More Service

These cannot be solved well forever with zero infrastructure:

- public quick match
- host reachability on hostile mobile networks
- frictionless private internet matches
- global discovery
- strong reliability for phone-hosted games

That is the hard boundary.

## Recommended Path For Murder Mansion

Given your constraints, I would recommend:

### Recommended Phase Order

1. Pre-Phase 0 fully
2. Phase 0 fully
3. Phase A fully
4. Ship or soft-launch native private multiplayer
5. Evaluate actual player demand
6. Only then decide whether Phase B is worth doing
7. Treat Phase C as optional
8. Do not plan Phase D unless the game shows real traction

### Why This Is The Right Order

Because Pre-Phase 0 + Phase 0 + Phase A already gets you:

- playable online multiplayer
- no hosted game backend
- low operational burden
- a real product on Android/iOS/desktop

That may be enough.

If it is not enough, then you add service in the smallest useful increments.

## Blunt Assessment

### If You Stop At Phase A

You get:

- real multiplayer
- lowest maintenance
- constrained reach

This is the highest-probability path.

### If You Go To Phase B

You get:

- much better usability
- still modest infrastructure burden

This is probably the best long-term balance.

### If You Go To Phase C

You get:

- public matchmaking flow
- but still inconsistent real-world connectivity in some cases

This is where product complexity starts to outrun a pure zero-backend philosophy.

### If You Go To Phase D

You get:

- the most product-complete online experience
- but you are now operating real multiplayer infrastructure

At that point, you have left the original constraint set.

## Bottom Line

Yes, LAN/private direct-connect first is restrictive.

But it is also the right stepping stone because it proves the expensive architecture work:

- host runtime
- protocol
- room flow
- cross-platform native connectivity

That work carries forward into any future global matchmaking plan.

What does not carry forward cleanly is the assumption that global internet play can stay frictionless forever without at least some service for discovery, and likely eventually some service for connectivity assistance.
