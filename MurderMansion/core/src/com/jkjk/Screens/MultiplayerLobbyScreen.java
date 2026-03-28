package com.jkjk.Screens;

import java.io.IOException;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.jkjk.MMHelpers.AssetLoader;
import com.jkjk.MMHelpers.MultiplayerSessionInfo;
import com.jkjk.MMHelpers.PresentationFrame;
import com.jkjk.Multiplayer.DiscoveryApiClient;
import com.jkjk.Multiplayer.DiscoveryApiClient.OccupantRole;
import com.jkjk.Multiplayer.DiscoveryApiClient.RoomActionResult;
import com.jkjk.Multiplayer.DiscoveryApiClient.RoomOccupant;
import com.jkjk.Multiplayer.DiscoveryApiClient.RoomPhase;
import com.jkjk.Multiplayer.DiscoveryApiClient.RoomState;
import com.jkjk.Multiplayer.DiscoveryApiClient.RoomVisibility;
import com.jkjk.Multiplayer.MultiplayerPreferences;
import com.jkjk.Multiplayer.MultiplayerUi;
import com.jkjk.MurderMansion.MurderMansion;
import com.jkjk.Telemetry.TelemetryService;

public class MultiplayerLobbyScreen implements Screen {
	private static final int MIN_PLAYERS_TO_START = 2;
	private static final long LOBBY_POLL_INTERVAL_MS = 3000L;

	public enum EntryMode {
		HOST_PUBLIC, HOST_PRIVATE, JOIN_CODE, QUICK_START, RETURN_EXISTING
	}

	private final MurderMansion game;
	private final float gameWidth;
	private final float gameHeight;
	private final EntryMode entryMode;
	private final String joinCode;
	private final Stage stage;

	private volatile RoomState room;
	private volatile boolean requestInFlight;
	private volatile String statusMessage;
	private volatile String errorMessage;
	private volatile boolean removedFromRoom;
	private boolean screenQueued;
	private boolean stageDirty;
	private long lastPollTime;

	public MultiplayerLobbyScreen(MurderMansion game, float gameWidth, float gameHeight,
			EntryMode entryMode, String joinCode) {
		this.game = game;
		this.gameWidth = gameWidth;
		this.gameHeight = gameHeight;
		this.entryMode = entryMode;
		this.joinCode = joinCode;
		this.stage = new Stage(PresentationFrame.createViewport());
		this.statusMessage = "Connecting to multiplayer service...";
		this.stageDirty = true;
	}

	@Override
	public void show() {
		TelemetryService telemetryService = TelemetryService.get();
		if (telemetryService != null) {
			telemetryService.setScreenName("MultiplayerLobbyScreen");
			telemetryService.recordEvent("lobby_shown", "MultiplayerLobbyScreen", null);
		}
		if (AssetLoader.gameMusic != null) {
			AssetLoader.gameMusic.stop();
		}
		AssetLoader.menuMusic.play();
		Gdx.input.setInputProcessor(stage);
		if (entryMode == EntryMode.RETURN_EXISTING) {
			fetchRoom();
		} else {
			enterRoom();
		}
		rebuildStage();
	}

	private void enterRoom() {
		final String discoveryUrl = MultiplayerPreferences.getDiscoveryUrl();
		if (discoveryUrl == null || discoveryUrl.trim().isEmpty()) {
			errorMessage = "Discovery service URL is not configured.";
			stageDirty = true;
			return;
		}
		final String displayName = MultiplayerPreferences.getDisplayName();
		game.mMultiplayerSession.mName = displayName;
		requestInFlight = true;
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					DiscoveryApiClient client = new DiscoveryApiClient(discoveryUrl);
					RoomActionResult result;
					if (entryMode == EntryMode.HOST_PUBLIC) {
						result = client.createRoom(RoomVisibility.PUBLIC, displayName);
					} else if (entryMode == EntryMode.HOST_PRIVATE) {
						result = client.createRoom(RoomVisibility.PRIVATE, displayName);
					} else if (entryMode == EntryMode.QUICK_START) {
						result = client.quickStart(displayName);
					} else {
						result = client.fetchRoomByCode(joinCode);
						if (result.ok && result.room != null) {
							result = client.joinRoom(result.room.roomId, displayName);
						}
					}
					applyResult(result);
				} catch (Exception e) {
					errorMessage = userFacingError(e);
					stageDirty = true;
				} finally {
					requestInFlight = false;
				}
			}
		}, "lobby-entry").start();
	}

	private void fetchRoom() {
		final String discoveryUrl = MultiplayerPreferences.getDiscoveryUrl();
		if (discoveryUrl == null || discoveryUrl.trim().isEmpty()) {
			errorMessage = "Discovery service URL is not configured.";
			stageDirty = true;
			return;
		}
		requestInFlight = true;
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					DiscoveryApiClient client = new DiscoveryApiClient(discoveryUrl);
					RoomActionResult result = client.pollRoom(game.mMultiplayerSession.mRoomId,
							game.mMultiplayerSession.occupantId);
					applyResult(result);
				} catch (Exception e) {
					errorMessage = userFacingError(e);
					stageDirty = true;
				} finally {
					requestInFlight = false;
				}
			}
		}, "lobby-fetch").start();
	}

	private void applyResult(RoomActionResult result) {
		if (result == null || !result.ok || result.room == null) {
			errorMessage = result != null && result.error != null ? result.error : "Room request failed.";
			stageDirty = true;
			return;
		}
		room = result.room;
		MultiplayerSessionInfo info = game.mMultiplayerSession;
		info.mRoomId = result.room.roomId;
		info.roomCode = result.room.roomCode;
		info.roomVisibility = result.room.visibility;
		info.roomPhase = result.room.phase;
		info.matchId = result.room.matchId;
		if (result.occupantId != null) {
			info.occupantId = result.occupantId;
		}
		if (result.role != null) {
			info.isSpectator = result.role == OccupantRole.SPECTATOR;
		} else if (info.occupantId != null) {
			info.isSpectator = isOccupantInSpectators(result.room, info.occupantId);
		}
		if (result.room.phase == RoomPhase.CLOSED) {
			TelemetryService telemetryService = TelemetryService.get();
			if (telemetryService != null) {
				telemetryService.recordEvent("room_closed_seen", "MultiplayerLobbyScreen", null);
			}
			removedFromRoom = true;
			Gdx.app.postRunnable(new Runnable() {
				@Override
				public void run() {
					game.mMultiplayerSession.roomNotice = "The room was closed.";
					game.mMultiplayerSession.clearRoomState();
					((Game) Gdx.app.getApplicationListener()).setScreen(new MultiplayerMenuScreen(game,
							gameWidth, gameHeight));
				}
			});
			return;
		}
		if (info.occupantId != null && !containsOccupant(result.room, info.occupantId)) {
			TelemetryService telemetryService = TelemetryService.get();
			if (telemetryService != null) {
				telemetryService.recordEvent("kick_seen", "MultiplayerLobbyScreen", null);
			}
			removedFromRoom = true;
			Gdx.app.postRunnable(new Runnable() {
				@Override
				public void run() {
					game.mMultiplayerSession.roomNotice = "You were removed from the room.";
					game.mMultiplayerSession.clearRoomState();
					((Game) Gdx.app.getApplicationListener()).setScreen(new MultiplayerMenuScreen(game,
							gameWidth, gameHeight));
				}
			});
			return;
		}
		TelemetryService telemetryService = TelemetryService.get();
		if (telemetryService != null) {
			telemetryService.syncRoomContext();
		}
		statusMessage = null;
		errorMessage = null;
		stageDirty = true;
	}

	@Override
	public void render(float delta) {
		if (removedFromRoom) {
			return;
		}
		if (room != null && !requestInFlight && System.currentTimeMillis() - lastPollTime > LOBBY_POLL_INTERVAL_MS
				&& !screenQueued) {
			lastPollTime = System.currentTimeMillis();
			fetchRoom();
		}
		if (room != null) {
			game.mMultiplayerSession.roomPhase = room.phase;
			if (!screenQueued && room.phase != null && (room.phase == RoomPhase.STARTING || room.phase == RoomPhase.IN_GAME)
					&& !game.mMultiplayerSession.isSpectator) {
				screenQueued = true;
				((Game) Gdx.app.getApplicationListener()).setScreen(new MultiplayerConnectingScreen(game,
						gameWidth, gameHeight, false));
				return;
			}
		}
		if (stageDirty) {
			rebuildStage();
		}
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.getViewport().apply();
		stage.act(delta);
		stage.draw();
	}

	private void rebuildStage() {
		stageDirty = false;
		stage.clear();
		Image background = new Image(AssetLoader.menuBackground);
		background.setFillParent(true);
		stage.addActor(background);
		stage.addActor(MultiplayerUi.createDimOverlay(0.34f));

		Label title = new Label("Online Lobby", MultiplayerUi.createLabelStyle());
		title.setPosition(240f, 320f);
		stage.addActor(title);

		String codeText = room != null ? "Room Code: " + room.roomCode : "Room Code: -";
		Label codeLabel = new Label(codeText, MultiplayerUi.createLabelStyle());
		codeLabel.setPosition(34f, 292f);
		stage.addActor(codeLabel);

		String nameText = "Name: " + MultiplayerPreferences.getDisplayName();
		Label nameLabel = new Label(nameText, MultiplayerUi.createLabelStyle());
		nameLabel.setPosition(34f, 268f);
		stage.addActor(nameLabel);

		if (statusMessage != null) {
			Label statusLabel = new Label(statusMessage, MultiplayerUi.createLabelStyle());
			statusLabel.setPosition(34f, 240f);
			stage.addActor(statusLabel);
		}
		if (errorMessage != null) {
			Label errorLabel = new Label(errorMessage, MultiplayerUi.createLabelStyle());
			errorLabel.setPosition(34f, 214f);
			stage.addActor(errorLabel);
		}

		if (room == null) {
			addNavButton("Back", 22f, 16f, new Runnable() {
				@Override
				public void run() {
					leaveAndReturnToMenu();
				}
			});
			return;
		}

		if (!isHost()) {
			addNavButton("Back", 22f, 16f, new Runnable() {
				@Override
				public void run() {
					leaveAndReturnToMenu();
				}
			});
		}

		float y = 192f;
		Label playersHeader = new Label("Players", MultiplayerUi.createLabelStyle());
		playersHeader.setPosition(34f, y);
		stage.addActor(playersHeader);
		y -= 26f;
		for (final RoomOccupant occupant : room.players) {
			Label playerLabel = new Label(occupant.displayName, MultiplayerUi.createLabelStyle());
			playerLabel.setPosition(50f, y);
			stage.addActor(playerLabel);
			float badgeX = 240f;
			if (occupant.host) {
				Label hostLabel = new Label("[HOST]", MultiplayerUi.createLabelStyle());
				hostLabel.setPosition(badgeX, y);
				stage.addActor(hostLabel);
				badgeX += 100f;
			}
			if (occupant.ready) {
				Label readyLabel = new Label("[READY]", MultiplayerUi.createLabelStyle());
				readyLabel.setPosition(badgeX, y);
				stage.addActor(readyLabel);
			}

			if (isHost() && !occupant.host) {
				addSmallButton("Kick", 488f, y - 4f, new Runnable() {
					@Override
					public void run() {
						kickOccupant(occupant.occupantId);
					}
				});
			}
			y -= 24f;
		}

		if (!room.spectators.isEmpty()) {
			y -= 10f;
			Label spectatorHeader = new Label("Spectators", MultiplayerUi.createLabelStyle());
			spectatorHeader.setPosition(34f, y);
			stage.addActor(spectatorHeader);
			y -= 26f;
			for (final RoomOccupant spectator : room.spectators) {
				Label spectatorLabel = new Label(spectator.displayName, MultiplayerUi.createLabelStyle());
				spectatorLabel.setPosition(50f, y);
				stage.addActor(spectatorLabel);
				if (isHost()) {
					addSmallButton("Kick", 488f, y - 4f, new Runnable() {
						@Override
						public void run() {
							kickOccupant(spectator.occupantId);
						}
					});
				}
				y -= 24f;
			}
		}

		if (room.phase == RoomPhase.LOBBY && !game.mMultiplayerSession.isSpectator) {
			boolean ready = isCurrentPlayerReady();
			addButton(ready ? "Unready" : "Ready", 250f, 20f, new Runnable() {
				@Override
				public void run() {
					setReady(!isCurrentPlayerReady());
				}
			});
		}

		if (isHost() && room.phase == RoomPhase.LOBBY) {
			String startDisabledReason = getStartDisabledReason();
			if (startDisabledReason != null) {
				Label startHint = new Label(startDisabledReason, MultiplayerUi.createLabelStyle());
				startHint.setPosition(292f, 62f);
				stage.addActor(startHint);
			}
			addButton("Start", 400f, 20f, startDisabledReason == null, new Runnable() {
				@Override
				public void run() {
					screenQueued = true;
					((Game) Gdx.app.getApplicationListener()).setScreen(new MultiplayerConnectingScreen(game,
							gameWidth, gameHeight, true));
				}
			});
			addButton("Close Room", 500f, 20f, new Runnable() {
				@Override
				public void run() {
					closeRoomAndReturn();
				}
			});
		} else if (isHost()) {
			addNavButton("Close Room", 22f, 16f, new Runnable() {
				@Override
				public void run() {
					closeRoomAndReturn();
				}
			});
		}

		if (game.mMultiplayerSession.isSpectator && room.phase == RoomPhase.IN_GAME) {
			Label waiting = new Label("Match in progress. You're queued for next round.",
					MultiplayerUi.createLabelStyle());
			waiting.setPosition(34f, 60f);
			stage.addActor(waiting);
			Label eta = new Label("Estimated wait: about 3 minutes.", MultiplayerUi.createLabelStyle());
			eta.setPosition(34f, 36f);
			stage.addActor(eta);
		} else if (game.mMultiplayerSession.isSpectator && room.phase == RoomPhase.STARTING) {
			Label waiting = new Label("Round is starting. You're queued for the next round.",
					MultiplayerUi.createLabelStyle());
			waiting.setPosition(34f, 60f);
			stage.addActor(waiting);
			Label eta = new Label("Estimated wait: about 3 minutes.", MultiplayerUi.createLabelStyle());
			eta.setPosition(34f, 36f);
			stage.addActor(eta);
		}
	}

	private boolean containsOccupant(RoomState roomState, String occupantId) {
		for (RoomOccupant occupant : roomState.players) {
			if (occupantId.equals(occupant.occupantId)) {
				return true;
			}
		}
		for (RoomOccupant occupant : roomState.spectators) {
			if (occupantId.equals(occupant.occupantId)) {
				return true;
			}
		}
		return false;
	}

	private boolean isOccupantInSpectators(RoomState roomState, String occupantId) {
		for (RoomOccupant occupant : roomState.spectators) {
			if (occupantId.equals(occupant.occupantId)) {
				return true;
			}
		}
		return false;
	}

	private boolean isCurrentPlayerReady() {
		for (RoomOccupant occupant : room.players) {
			if (occupant.occupantId.equals(game.mMultiplayerSession.occupantId)) {
				return occupant.ready;
			}
		}
		return false;
	}

	private boolean isHost() {
		return room != null && room.isHost(game.mMultiplayerSession.occupantId);
	}

	private String getStartDisabledReason() {
		if (room == null || room.players == null) {
			return "Waiting for room info...";
		}
		if (room.players.size() < MIN_PLAYERS_TO_START) {
			return "Need at least " + MIN_PLAYERS_TO_START + " players to start.";
		}
		for (RoomOccupant occupant : room.players) {
			if (!occupant.ready) {
				return "All players must be ready.";
			}
		}
		return null;
	}

	private void setReady(final boolean ready) {
		runRoomMutation(new RoomMutation() {
			@Override
			public RoomActionResult execute(DiscoveryApiClient client) throws IOException {
				return client.setReady(game.mMultiplayerSession.mRoomId, game.mMultiplayerSession.occupantId,
						ready);
			}
		});
	}

	private void kickOccupant(final String targetOccupantId) {
		runRoomMutation(new RoomMutation() {
			@Override
			public RoomActionResult execute(DiscoveryApiClient client) throws IOException {
				return client.kick(game.mMultiplayerSession.mRoomId, game.mMultiplayerSession.occupantId,
						targetOccupantId);
			}
		});
	}

	private void closeRoomAndReturn() {
		runRoomMutation(new RoomMutation() {
			@Override
			public RoomActionResult execute(DiscoveryApiClient client) throws IOException {
				return client.closeRoom(game.mMultiplayerSession.mRoomId, game.mMultiplayerSession.occupantId);
			}
		}, new Runnable() {
			@Override
			public void run() {
				game.mMultiplayerSession.clearRoomState();
				((Game) Gdx.app.getApplicationListener()).setScreen(new MultiplayerMenuScreen(game, gameWidth,
						gameHeight));
			}
		});
	}

	private void leaveAndReturnToMenu() {
		if (game.mMultiplayerSession.mRoomId == null || game.mMultiplayerSession.occupantId == null) {
			((Game) Gdx.app.getApplicationListener()).setScreen(new MultiplayerMenuScreen(game, gameWidth,
					gameHeight));
			return;
		}
		runRoomMutation(new RoomMutation() {
			@Override
			public RoomActionResult execute(DiscoveryApiClient client) throws IOException {
				return client.leaveRoom(game.mMultiplayerSession.mRoomId, game.mMultiplayerSession.occupantId);
			}
		}, new Runnable() {
			@Override
			public void run() {
				game.mMultiplayerSession.clearRoomState();
				((Game) Gdx.app.getApplicationListener()).setScreen(new MultiplayerMenuScreen(game, gameWidth,
						gameHeight));
			}
		});
	}

	private void runRoomMutation(final RoomMutation mutation) {
		runRoomMutation(mutation, null);
	}

	private void runRoomMutation(final RoomMutation mutation, final Runnable onSuccess) {
		final String discoveryUrl = MultiplayerPreferences.getDiscoveryUrl();
		requestInFlight = true;
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					DiscoveryApiClient client = new DiscoveryApiClient(discoveryUrl);
					RoomActionResult result = mutation.execute(client);
					if (result != null && result.ok && result.room != null) {
						applyResult(result);
						if (onSuccess != null) {
							Gdx.app.postRunnable(onSuccess);
						}
					} else {
						errorMessage = result != null && result.error != null ? result.error : "Room update failed.";
						stageDirty = true;
					}
				} catch (Exception e) {
					errorMessage = userFacingError(e);
					stageDirty = true;
				} finally {
					requestInFlight = false;
				}
			}
		}, "room-mutation").start();
	}

	private void addButton(String text, float x, float y, final Runnable action) {
		addButton(text, x, y, true, action);
	}

	private void addButton(String text, float x, float y, boolean enabled, final Runnable action) {
		TextButton button = new TextButton(text, AssetLoader.normal);
		button.setSize(100f, 36f);
		button.setPosition(x, y);
		button.setDisabled(!enabled);
		button.setTouchable(enabled ? Touchable.enabled : Touchable.disabled);
		if (!enabled) {
			button.getColor().a = 0.5f;
		}
		button.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (button.isDisabled()) {
					return;
				}
				AssetLoader.clickSound.play(AssetLoader.VOLUME);
				action.run();
			}
		});
		stage.addActor(button);
	}

	private void addSmallButton(String text, float x, float y, final Runnable action) {
		TextButton button = new TextButton(text, AssetLoader.normal);
		button.setSize(70f, 24f);
		button.getLabel().setFontScale(0.45f);
		button.setPosition(x, y);
		button.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				AssetLoader.clickSound.play(AssetLoader.VOLUME);
				action.run();
			}
		});
		stage.addActor(button);
	}

	private void addNavButton(String text, float x, float y, final Runnable action) {
		TextButton button = new TextButton(text, AssetLoader.normal);
		button.setSize(90f, 32f);
		button.setPosition(x, y);
		button.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				AssetLoader.clickSound.play(AssetLoader.VOLUME);
				action.run();
			}
		});
		stage.addActor(button);
	}

	private String userFacingError(Exception e) {
		String message = e.getMessage();
		if (message == null || message.trim().isEmpty()) {
			return "Network request failed.";
		}
		return message;
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
		stage.dispose();
	}

	private interface RoomMutation {
		RoomActionResult execute(DiscoveryApiClient client) throws IOException;
	}
}
