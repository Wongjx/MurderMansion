package com.jkjk.Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.jkjk.GameWorld.GameRenderer;
import com.jkjk.GameWorld.GameSession;
import com.jkjk.GameWorld.GameWorld;
import com.jkjk.GameWorld.MMClient;
import com.jkjk.Host.MMServer;
import com.jkjk.MMHelpers.AssetLoader;
import com.jkjk.MMHelpers.MultiplayerSessionInfo;
import com.jkjk.MMHelpers.PresentationFrame;
import com.jkjk.Multiplayer.DiscoveryApiClient;
import com.jkjk.Multiplayer.GameLineTransport;
import com.jkjk.Multiplayer.QueueLineTransport;
import com.jkjk.Multiplayer.RelayClientBootstrap;
import com.jkjk.Multiplayer.DiscoveryApiClient.RoomActionResult;
import com.jkjk.Multiplayer.DiscoveryApiClient.RoomPhase;
import com.jkjk.Multiplayer.DiscoveryApiClient.RelayConnectInfo;
import com.jkjk.Multiplayer.MultiplayerPreferences;
import com.jkjk.Multiplayer.RelayHostedMatchCoordinator;
import com.jkjk.Multiplayer.RelayRoomSession;
import com.jkjk.Multiplayer.MultiplayerUi;
import com.jkjk.MurderMansion.MurderMansion;
import com.jkjk.Telemetry.TelemetryService;

public class MultiplayerConnectingScreen implements Screen {
	private static final long CONNECTING_POLL_INTERVAL_MS = 2500L;

	private final MurderMansion game;
	private final float gameWidth;
	private final float gameHeight;
	private final boolean hostStart;
	private final Stage stage;

	private volatile String statusMessage;
	private volatile String errorMessage;
	private volatile boolean workStarted;
	private volatile boolean gameReady;
	private volatile boolean pendingClientCreation;
	private volatile GameLineTransport pendingTransport;
	private volatile GameWorld readyWorld;
	private volatile GameRenderer readyRenderer;
	private volatile MMClient readyClient;
	private volatile RelayClientBootstrap pendingRelayBootstrap;
	private volatile RelayHostedMatchCoordinator hostCoordinator;

	public MultiplayerConnectingScreen(MurderMansion game, float gameWidth, float gameHeight, boolean hostStart) {
		this.game = game;
		this.gameWidth = gameWidth;
		this.gameHeight = gameHeight;
		this.hostStart = hostStart;
		this.stage = new Stage(PresentationFrame.createViewport());
		this.statusMessage = hostStart ? "Starting room..." : "Waiting for host...";
	}

	@Override
	public void show() {
		TelemetryService telemetryService = TelemetryService.get();
		if (telemetryService != null) {
			telemetryService.setScreenName("MultiplayerConnectingScreen");
			telemetryService.recordEvent("connecting_shown", "MultiplayerConnectingScreen", null);
		}
		stage.clear();
		Image background = new Image(AssetLoader.menuBackground);
		background.setFillParent(true);
		stage.addActor(background);
		stage.addActor(MultiplayerUi.createDimOverlay(0.34f));
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void render(float delta) {
		if (!workStarted) {
			workStarted = true;
			if (hostStart) {
				startHostAndConnect();
			} else {
				waitForRoomAndConnect();
			}
		}

		stage.clear();
		Image background = new Image(AssetLoader.menuBackground);
		background.setFillParent(true);
		stage.addActor(background);
		stage.addActor(MultiplayerUi.createDimOverlay(0.34f));
		Label label = new Label(errorMessage != null ? errorMessage : statusMessage, MultiplayerUi.createLabelStyle());
		label.setPosition(70f, 180f);
		stage.addActor(label);

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.getViewport().apply();
		stage.act(delta);
		stage.draw();

		if (hostStart && hostCoordinator != null && hostCoordinator.isLocalClientReady() && pendingTransport == null
				&& !pendingClientCreation && !gameReady && errorMessage == null) {
			statusMessage = "Finalizing host session...";
			pendingTransport = hostCoordinator.getLocalClientTransport();
			pendingClientCreation = true;
		}
		if (!hostStart && pendingRelayBootstrap != null && pendingRelayBootstrap.isBootstrapReady()
				&& pendingTransport == null && !pendingClientCreation && !gameReady && errorMessage == null) {
			statusMessage = "Preparing match...";
			pendingTransport = pendingRelayBootstrap.getTransport();
			pendingClientCreation = true;
		}

		if (pendingClientCreation && !gameReady && errorMessage == null) {
			pendingClientCreation = false;
			try {
				createClientConnectionOnRenderThread(pendingTransport);
			} catch (Exception e) {
				errorMessage = e.getMessage() == null ? "Failed to connect." : e.getMessage();
			}
		}

		if (gameReady) {
			GameSession session = readyClient;
			((Game) Gdx.app.getApplicationListener()).setScreen(new LoadingScreen(game, gameWidth, gameHeight,
					readyWorld, readyRenderer, session));
		}
	}

	private void startHostAndConnect() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					MultiplayerSessionInfo info = game.mMultiplayerSession;
					if (info.relaySocketFactory == null) {
						throw new IllegalStateException("Relay transport is unavailable on this platform.");
					}
					info.isServer = true;
					MMServer server = new MMServer(fetchPlayerCount(), info, false, -1);
					info.setServer(server);

					DiscoveryApiClient apiClient = new DiscoveryApiClient(MultiplayerPreferences.getDiscoveryUrl());
					RoomActionResult result = apiClient.startRoom(info.mRoomId, info.occupantId, "relay", 0);
					if (result == null || !result.ok || result.room == null) {
						throw new IllegalStateException("Unable to start room.");
					}
					RelayConnectInfo connectInfo = apiClient.fetchConnectInfo(info.mRoomId, info.occupantId);
					if (connectInfo == null || !connectInfo.ok || connectInfo.relayUrl == null) {
						throw new IllegalStateException("Unable to fetch relay connection.");
					}

					info.roomPhase = result.room.phase;
					info.matchId = result.room.matchId;
					TelemetryService telemetryService = TelemetryService.get();
					if (telemetryService != null) {
						telemetryService.setMatchId(info.matchId);
					}
					info.relayUrl = connectInfo.relayUrl;
					statusMessage = "Opening relay room...";
					RelayHostedMatchCoordinator coordinator = new RelayHostedMatchCoordinator(info, server, result.room,
							connectInfo.relayUrl);
					coordinator.connect();
					info.relayHostedMatchCoordinator = coordinator;
					hostCoordinator = coordinator;
					statusMessage = "Waiting for players to connect...";
				} catch (Exception e) {
					errorMessage = e.getMessage() == null ? "Failed to start host." : e.getMessage();
				}
			}
		}, "host-start").start();
	}

	private void waitForRoomAndConnect() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					DiscoveryApiClient apiClient = new DiscoveryApiClient(MultiplayerPreferences.getDiscoveryUrl());
					while (true) {
						RoomActionResult result = apiClient.pollRoom(game.mMultiplayerSession.mRoomId,
								game.mMultiplayerSession.occupantId);
						if (result != null && result.ok && result.room != null) {
							game.mMultiplayerSession.roomPhase = result.room.phase;
							game.mMultiplayerSession.matchId = result.room.matchId;
							if (result.room.phase == RoomPhase.STARTING || result.room.phase == RoomPhase.IN_GAME) {
								TelemetryService telemetryService = TelemetryService.get();
								if (telemetryService != null) {
									telemetryService.setMatchId(result.room.matchId);
								}
								RelayConnectInfo connectInfo = apiClient.fetchConnectInfo(game.mMultiplayerSession.mRoomId,
										game.mMultiplayerSession.occupantId);
								if (connectInfo == null || !connectInfo.ok || connectInfo.relayUrl == null) {
									throw new IllegalStateException("Host could not be reached.");
								}
								statusMessage = "Connecting to relay...";
								RelayClientBootstrap bootstrap = new RelayClientBootstrap();
								RelayRoomSession session = new RelayRoomSession(game.mMultiplayerSession.mRoomId,
										game.mMultiplayerSession.occupantId, false, connectInfo.relayUrl,
										game.mMultiplayerSession.relaySocketFactory, bootstrap.createListener());
								bootstrap.attachSession(session);
								session.connect();
								pendingRelayBootstrap = bootstrap;
								game.mMultiplayerSession.relayUrl = connectInfo.relayUrl;
								statusMessage = "Waiting for host setup...";
								return;
							}
						}
						Thread.sleep(CONNECTING_POLL_INTERVAL_MS);
					}
				} catch (Exception e) {
					errorMessage = e.getMessage() == null ? "Failed to connect." : e.getMessage();
				}
			}
		}, "client-wait").start();
	}

	private void createClientConnectionOnRenderThread(GameLineTransport transport) throws Exception {
		GameWorld gWorld = new GameWorld(false);
		GameRenderer renderer = new GameRenderer(gWorld, gameWidth, gameHeight);
		MMClient client = new MMClient(gWorld, renderer, transport, game.mMultiplayerSession.occupantId,
				game.mMultiplayerSession.mName, false, false);
		game.mMultiplayerSession.setClient(client);
		readyWorld = gWorld;
		readyRenderer = renderer;
		readyClient = client;
		gameReady = true;
	}

	private int fetchPlayerCount() throws Exception {
		DiscoveryApiClient apiClient = new DiscoveryApiClient(MultiplayerPreferences.getDiscoveryUrl());
		RoomActionResult result = apiClient.pollRoom(game.mMultiplayerSession.mRoomId,
				game.mMultiplayerSession.occupantId);
		if (result == null || !result.ok || result.room == null) {
			throw new IllegalStateException("Unable to fetch room roster.");
		}
		return result.room.players.size();
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void hide() {
		dispose();
	}

	@Override
	public void dispose() {
		if (!gameReady && hostCoordinator != null) {
			hostCoordinator.close();
		}
		stage.dispose();
	}
}
