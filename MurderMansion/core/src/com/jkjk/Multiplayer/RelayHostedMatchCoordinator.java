package com.jkjk.Multiplayer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.jkjk.Host.LineDispatchWriter;
import com.jkjk.Host.MMServer;
import com.jkjk.MMHelpers.MMLog;
import com.jkjk.MMHelpers.MultiplayerSessionInfo;
import com.jkjk.Multiplayer.DiscoveryApiClient.RoomOccupant;
import com.jkjk.Multiplayer.DiscoveryApiClient.RoomState;

public class RelayHostedMatchCoordinator {
	private final MMServer server;
	private final RelayRoomSession relaySession;
	private final Map<String, PlayerSlot> slotsByOccupantId;
	private final QueueLineTransport localClientTransport;
	private final QueueLineTransport localServerTransport;
	private final Thread localInboundThread;
	private final Thread pingThread;
	private volatile boolean localClientReady;
	private volatile boolean closed;

	public RelayHostedMatchCoordinator(MultiplayerSessionInfo info, MMServer server, RoomState room, String relayUrl)
			throws IOException {
		this.server = server;
		this.slotsByOccupantId = createSlots(room);

		InMemoryTransportPair.EndpointPair pair = InMemoryTransportPair.create();
		this.localClientTransport = pair.endpointA;
		this.localServerTransport = pair.endpointB;

		PlayerSlot hostSlot = slotsByOccupantId.get(info.occupantId);
		server.registerLogicalClient(hostSlot.playerId, hostSlot.displayName, new LineDispatchWriter.LineConsumer() {
			@Override
			public void accept(String line) {
				localClientTransport.offerIncomingLine(line);
			}
		});

		this.localInboundThread = new Thread(new Runnable() {
			@Override
			public void run() {
				pumpLocalMessages(hostSlot.playerId, hostSlot.displayName);
			}
		}, "relay-host-local");
		this.localInboundThread.start();

		this.relaySession = new RelayRoomSession(room.roomId, info.occupantId, true, relayUrl, info.relaySocketFactory,
				new RelayRoomSession.Listener() {
					@Override
					public void onOpen() {
						MMLog.log("MM-RELAY", "Host relay connected.");
					}

					@Override
					public void onConnected(boolean isHost) {
					}

					@Override
					public void onPayload(String fromOccupantId, String payload) {
						try {
							server.handleMessage(payload);
						} catch (Exception e) {
							MMLog.log("MM-CRASH", "Failed handling relay payload on host bridge.", e);
						}
					}

					@Override
					public void onPeerConnected(String occupantId) {
						registerRemotePlayer(occupantId);
					}

					@Override
					public void onPeerDisconnected(String occupantId) {
						PlayerSlot slot = slotsByOccupantId.get(occupantId);
						if (slot != null) {
							server.removePlayer(slot.playerId);
						}
					}

					@Override
					public void onHostDisconnected() {
					}

					@Override
					public void onRoomClosed() {
						close();
					}

					@Override
					public void onClose(String reason) {
						close();
					}

					@Override
					public void onError(Throwable throwable) {
						MMLog.log("MM-CRASH", "Host relay socket error.", throwable);
					}
				});

		this.pingThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (!closed) {
					try {
						Thread.sleep(15000L);
						relaySession.sendPing();
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
						return;
					} catch (Exception e) {
						MMLog.log("MM-RELAY", "Host relay ping failed: " + e.getMessage());
						return;
					}
				}
			}
		}, "relay-host-ping");
		this.pingThread.start();
	}

	private Map<String, PlayerSlot> createSlots(RoomState room) {
		HashMap<String, PlayerSlot> slots = new HashMap<String, PlayerSlot>();
		for (int i = 0; i < room.players.size(); i++) {
			RoomOccupant occupant = room.players.get(i);
			slots.put(occupant.occupantId, new PlayerSlot(i, occupant.occupantId, occupant.displayName));
		}
		return slots;
	}

	private void pumpLocalMessages(int playerId, String displayName) {
		while (!closed) {
			try {
				String message = localServerTransport.readLine(10000L);
				server.handleMessage(message);
			} catch (IOException e) {
				return;
			} catch (Exception e) {
				MMLog.log("MM-CRASH", "Local host transport failed for " + displayName, e);
				return;
			}
		}
	}

	private synchronized void registerRemotePlayer(final String occupantId) {
		PlayerSlot slot = slotsByOccupantId.get(occupantId);
		if (slot == null || server.hasRegisteredClient(slot.playerId)) {
			return;
		}
		try {
			server.registerLogicalClient(slot.playerId, slot.displayName, new LineDispatchWriter.LineConsumer() {
				@Override
				public void accept(String line) throws IOException {
					try {
						relaySession.sendToTarget(occupantId, line);
					} catch (Exception e) {
						throw new IOException("Failed sending relay line to player " + occupantId, e);
					}
				}
			});
			if (server.getRegisteredClientCount() == server.getNumOfPlayers()) {
				server.broadcastClientNames();
				localClientReady = true;
			}
		} catch (IOException e) {
			MMLog.log("MM-CRASH", "Failed registering remote relay client " + occupantId, e);
		}
	}

	public void connect() throws Exception {
		relaySession.connect();
	}

	public boolean isLocalClientReady() {
		return localClientReady;
	}

	public QueueLineTransport getLocalClientTransport() {
		return localClientTransport;
	}

	public void close() {
		if (closed) {
			return;
		}
		closed = true;
		relaySession.close();
		localClientTransport.close();
		localServerTransport.close();
		localInboundThread.interrupt();
		pingThread.interrupt();
	}

	private static final class PlayerSlot {
		private final int playerId;
		private final String occupantId;
		private final String displayName;

		private PlayerSlot(int playerId, String occupantId, String displayName) {
			this.playerId = playerId;
			this.occupantId = occupantId;
			this.displayName = displayName;
		}
	}
}
